package com.tecacet.simulator;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simulator<S> implements SimulationEnvironment<S> {

    private S currentState;
    protected EventQueue eventQueue;
    protected int iteration = 0;
    protected InternalClock clock;
    protected StochasticSystem<S> stochasticSystem;
    protected Terminator<S> terminator;
    protected RandomDataGenerator generator;
    protected StatisticsRegistry registry;
    
    protected Logger logger = LoggerFactory.getLogger(Simulator.class);

    public Simulator(StochasticSystem<S> system, Terminator<S> terminator) {
        this(system, new RandomDataGenerator(), terminator);
    }

    public Simulator(StochasticSystem<S> system, RandomDataGenerator randomData, Terminator<S> terminator) {
        this.stochasticSystem = system;
        this.generator = randomData;
        this.terminator = terminator;
        eventQueue = new EventQueue();
        clock = new InternalClock();
        registry = new StatisticsRegistry(clock);
    }

    public Simulator(StochasticSystem<S> system, long seed, Terminator<S> terminator) {
        this(system, new RandomDataGenerator(), terminator);
        generator.reSeed(seed);
    }

    /** Get the current iteration */
    public int getCurrentIteration() {
        return iteration;
    }

    /** Get the current time from the beginning of the simulation */
    public double getCurrentTime() {
        return clock.getTime();
    }

    public void runSimulation() throws SimulationException {
        registry.clearAll();
        clock.setTime(0);
        stochasticSystem.initialize(this);
        currentState = stochasticSystem.getInitialState(this);
        while (!terminator.simulationEnded(this)) {
            simulationStep();
        }
       
    }    

    public void simulationStep() throws SimulationException {
        iteration++;
        logger.debug("Simulation iteration " + iteration);
        // an event occurs
        SimulationEvent event = eventQueue.getNextEvent();
        // set the time
        clock.setTime(event.getTime());
        // move to the next state
        currentState = stochasticSystem.getNextState(event, this);
    }

    public StochasticSystem<S> getStochasticSystem() {
        return stochasticSystem;
    }

    public void setStochasticSystem(StochasticSystem<S> stochasticSystem) {
        this.stochasticSystem = stochasticSystem;
    }

    public void setSeed(long seed) {
        RandomGenerator random = new JDKRandomGenerator();
        random.setSeed(seed);
        generator = new RandomDataGenerator(random);
    }

    @Override
    public S getCurrentState() {
        return currentState;
    }

    @Override
    public void addEvent(SimulationEvent event) {
        eventQueue.insert(event);
    }

    @Override
    public RandomDataGenerator getRandomGenerator() {
        return generator;
    }

    @Override
    public StatisticsRegistry getAccumulatorRegistry() {
        return registry;
    }

    public Clock getClock() {
        return clock;
    }

}
