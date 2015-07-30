package com.tecacet.simulator.inventory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecacet.simulator.SimulationEnvironment;
import com.tecacet.simulator.SimulationEvent;
import com.tecacet.simulator.SimulationException;
import com.tecacet.simulator.StatisticsRegistry;
import com.tecacet.simulator.StochasticSystem;
import com.tecacet.simulator.Terminator;
import com.tecacet.util.PropertiesLoader;

public class InventorySystem implements StochasticSystem<InventoryState>, Terminator<InventoryState> {

    private static final String INPUT_FILE = "inventory.properties";
    public static final String ORDERING_COST = "orderingCost";
    public static final String SHORTAGE_COST = "shortageCost";
    public static final String HOLDING_COST = "holdingCost";
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Params

    private int initialInventoryLevel = 0;
    private int maxIterations = 10000;
    private double setupCost = 0.0;
    private double incrementalCost = 0.0;
    private double simulationMonths = 120;
    private double maxLag = 1.0;
    private double minLag = 0.1;
    private double meanInterdemand = 0.1;
    private double evaluationFrequency = 1.0;
    private double[] demandProbabilityDistribution;
    private double holdingCost = 0.0;
    private double shortageCost = 0.0;

    private Policy policy;

    public InventorySystem() throws ConfigurationException {
        readInputParameters();
    }

    // Runtime params
    protected void readInputParameters() throws ConfigurationException {
        PropertiesLoader loader = new PropertiesLoader(INPUT_FILE);
        loader.readInputParameters(this);
        policy = new Policy(20, 40);
    }

    @Override
    public boolean simulationEnded(SimulationEnvironment<InventoryState> environment) {
        // return (environment.getIteration() == maxIterations);
        return environment.getCurrentTime() >= simulationMonths;
    }

    @Override
    public void initialize(SimulationEnvironment<InventoryState> environment) throws SimulationException {
        // schedule next demand
        scheduleDemand(0.0, environment);
        // schedule next evaluation in a month
        scheduleEvaluation(0.0, environment);
    }

    @Override
    public InventoryState getInitialState(SimulationEnvironment<InventoryState> environment) {
        return new InventoryState(initialInventoryLevel);
    }

    @Override
    public InventoryState getNextState(SimulationEvent e, SimulationEnvironment<InventoryState> environment)
            throws SimulationException {

        InventoryEvent event = (InventoryEvent) e;
        InventoryState state = environment.getCurrentState();
        InventoryDecision decision = policy.getDecision(state);
        StatisticsRegistry registry = environment.getAccumulatorRegistry();
        SummaryStatistics orderingCostStats = registry.getStatistics(ORDERING_COST);
        SummaryStatistics shortageCostStats = registry.getTimeAwareStatistics(SHORTAGE_COST);
        SummaryStatistics holdingCostStats = registry.getTimeAwareStatistics(HOLDING_COST);

        if (state.getInventory() < 0) {
            shortageCostStats.addValue(-state.getInventory()*this.shortageCost);
            holdingCostStats.addValue(0);
        } else {
            shortageCostStats.addValue(0);
            holdingCostStats.addValue(state.getInventory() * holdingCost);
        }

        switch (event.getType()) {

        case InventoryEvent.ORDER_ARRIVAL:
            logger.debug("ARRIVAL of " + event.getOrderAmount() + " items\n");
            break;
        case InventoryEvent.DEMAND:
            logger.debug("DEMAND of " + event.getDemandAmount() + " items\n");
            scheduleDemand(event.getTime(), environment);
            break;
        case InventoryEvent.EVALUATE:
            if (decision.getOrderAmount() > 0) {
                orderingCostStats.addValue(setupCost + incrementalCost * decision.getOrderAmount());
            }
            scheduleEvaluation(event.getTime(), environment);
            scheduleArrival(event.getTime(), decision, environment);
            break;
        default:
            logger.error("Invalid event type " + event.getType());
        }
        state.update(event, decision);
        return state;
    }

    protected void scheduleDemand(double time, SimulationEnvironment<InventoryState> env) {
        double demandTime = time + env.getRandomGenerator().nextExponential(meanInterdemand);
        int demandAmount = randomDemandAmount(env);
        env.addEvent(new InventoryEvent(InventoryEvent.DEMAND, demandTime, demandAmount));
    }

    private int randomDemandAmount(SimulationEnvironment<InventoryState> env) {
        double random = env.getRandomGenerator().nextUniform(0, 1);
        int i;
        for (i = 0; i < demandProbabilityDistribution.length; i++) {
            if (random < demandProbabilityDistribution[i]) {
                return i + 1;
            }
        }
        return i; // if it's a true prob distribution this is never reached
    }

    private void scheduleEvaluation(double time, SimulationEnvironment<InventoryState> env) {
        double evalTime = time + evaluationFrequency;
        env.addEvent(new InventoryEvent(InventoryEvent.EVALUATE, evalTime));
    }

    private void scheduleArrival(double time, InventoryDecision decision, SimulationEnvironment<InventoryState> env) {
        double orderArrivalTime = maxLag == 0?  time : time + env.getRandomGenerator().nextUniform(minLag, maxLag);
        env.addEvent(new InventoryEvent(InventoryEvent.ORDER_ARRIVAL, orderArrivalTime, decision.getOrderAmount()));
    }

    public void setThreshold(int t) {
        policy.setThreshold(t);
    }

    public void setMaxStorage(int s) {
        policy.setMaxStorage(s);
    }

    public int getInitialInventoryLevel() {
        return initialInventoryLevel;
    }

    public void setInitialInventoryLevel(int initialInventoryLevel) {
        this.initialInventoryLevel = initialInventoryLevel;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public double getSetupCost() {
        return setupCost;
    }

    public void setSetupCost(double setupCost) {
        this.setupCost = setupCost;
    }

    public double getIncrementalCost() {
        return incrementalCost;
    }

    public void setIncrementalCost(double incrementalCost) {
        this.incrementalCost = incrementalCost;
    }

    public double getSimulationMonths() {
        return simulationMonths;
    }

    public void setSimulationMonths(double simulationMonths) {
        this.simulationMonths = simulationMonths;
    }

    public double getMaxLag() {
        return maxLag;
    }

    public void setMaxLag(double maxLag) {
        this.maxLag = maxLag;
    }

    public double getMinLag() {
        return minLag;
    }

    public void setMinLag(double minLag) {
        this.minLag = minLag;
    }

    public double getMeanInterdemand() {
        return meanInterdemand;
    }

    public void setMeanInterdemand(double meanInterdemand) {
        this.meanInterdemand = meanInterdemand;
    }

    public double getEvaluationFrequency() {
        return evaluationFrequency;
    }

    public void setEvaluationFrequency(double evaluationFrequency) {
        this.evaluationFrequency = evaluationFrequency;
    }

    public double[] getDemandProbabilityDistribution() {
        return demandProbabilityDistribution;
    }

    public void setDemandProbabilityDistribution(double[] demandProbabilityDistribution) {
        this.demandProbabilityDistribution = demandProbabilityDistribution;
    }

    public double getHoldingCost() {
        return holdingCost;
    }

    public void setHoldingCost(double holdingCost) {
        this.holdingCost = holdingCost;
    }

    public double getShortageCost() {
        return shortageCost;
    }

    public void setShortageCost(double shortageCost) {
        this.shortageCost = shortageCost;
    }

}
