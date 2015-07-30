package com.tecacet.simulator.bank;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecacet.simulator.SimulationEnvironment;
import com.tecacet.simulator.SimulationEvent;
import com.tecacet.simulator.SimulationException;
import com.tecacet.simulator.Simulator;
import com.tecacet.simulator.StochasticSystem;
import com.tecacet.simulator.Terminator;
import com.tecacet.util.PropertiesLoader;

public class Bank implements StochasticSystem<BankState>, Terminator<BankState> {

    public static final String NUMBER_IN_QUEUE = "NUMBER_IN_QUEUE";
    public static final String CUSTOMER_DELAY = "CUSTOMER_DELAY";

    private static final String PARAM_FILE = "bank.properties";

    private Logger logger = LoggerFactory.getLogger(Bank.class);

    // params
    private int tellers;
    private double closingTime;
    private double meanIterarrival;
    private double serviceTime;

    public Bank(int tellers) {
        this.tellers = tellers;
        readInputParameters();
    }

    @Override
    public BankState getInitialState(SimulationEnvironment<BankState> env) {
        return new BankState(tellers);
    }

    @Override
    public void initialize(SimulationEnvironment<BankState> env) {
        scheduleArrival(env, 0.0);
        // bank closing event
        env.addEvent(new BankEvent(BankEvent.BANK_CLOSES, closingTime));
    }

    protected void readInputParameters() {
        new PropertiesLoader(PARAM_FILE).readInputParameters(this);
    }
    
    @Override
    public BankState getNextState(SimulationEvent e, SimulationEnvironment<BankState> env) throws SimulationException {
        BankState state = env.getCurrentState();
        BankEvent event = (BankEvent) e;

        SummaryStatistics customerDelay = env.getAccumulatorRegistry().getStatistics(CUSTOMER_DELAY);
        SummaryStatistics numberInQueue = env.getAccumulatorRegistry().getTimeAwareStatistics(NUMBER_IN_QUEUE);
        numberInQueue.addValue(state.getNumberInQueue());
        int teller;
        BankCustomer customer = event.getCustomer();
        switch (event.getType()) {
        case BankEvent.ARRIVAL:
            // schedule the next arrival
            scheduleArrival(env, event.getTime());
            state.update(event);
            // If customer serviced, schedule departure
            // NOTE: The state has already populated the customer with the
            // Teller number
            teller = customer.getTeller();
            if (state.getCustomerInService(teller).getId() == event.getCustomer().getId()) {
                scheduleDeparture(env, event.getTime(), event.getCustomer());
            }

            if (customer.equals(state.getTeller(teller).getCustomerInService())) {
                // arriving customer was serviced immediately
                customerDelay.addValue(0.0);
            }
            break;
        case BankEvent.DEPARTURE:
            teller = customer.getTeller();
            state.update(event);
            BankCustomer customerInService = state.getTeller(teller).getCustomerInService();
            if (null != customerInService) {
                scheduleDeparture(env, event.getTime(), customer);
                double delay = event.getTime() - customerInService.getArrivalTime();
                customerDelay.addValue(delay);
            }
            break;
        case BankEvent.BANK_CLOSES:
            state.setBankClosed(true);
        default:
            // nada
        }

        return state;
    }

    public boolean simulationEnded(SimulationEnvironment<BankState> env) {
        BankState state = env.getCurrentState();
        return state.isBankClosed() && !state.hasCustomers();
        // TODO
        // return env.getCurrentTime() > bankProcess.getClosingTime();
    }

    static String report(String name, SummaryStatistics accumulator) {
        StringBuffer sb = new StringBuffer();
        sb.append("Number of samples = " + accumulator.getN() + "\n");
        sb.append("Total " + name + " = " + accumulator.getSum() + "\n");
        sb.append("Average " + name + " = " + accumulator.getMean() + "\n");
        sb.append("Minimum " + name + " = " + accumulator.getMin() + "\n");
        sb.append("Maximum " + name + " = " + accumulator.getMax() + "\n");

        return sb.toString();
    }

    public int getTellers() {
        return tellers;
    }

    public void setTellers(int tellers) {
        this.tellers = tellers;
    }

    public double getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(double closingTime) {
        this.closingTime = closingTime;
    }

    public double getMeanIterarrival() {
        return meanIterarrival;
    }

    public void setMeanIterarrival(double meanIterarrival) {
        this.meanIterarrival = meanIterarrival;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    private void scheduleArrival(SimulationEnvironment<BankState> environment, double time) {
        double arrivalTime = time + environment.getRandomGenerator().nextExponential(meanIterarrival);
        if (arrivalTime < closingTime) {
            logger.debug("Scheduling next arrival at " + arrivalTime);
            environment.addEvent(new BankEvent(BankEvent.ARRIVAL, arrivalTime, new BankCustomer(arrivalTime)));
        } else {
            logger.debug("Not scheduling arrival after closing time: " + arrivalTime);
        }
    }

    private void scheduleDeparture(SimulationEnvironment<BankState> environment, final double time,
            final BankCustomer customer) {
        double departureTime = time + environment.getRandomGenerator().nextExponential(serviceTime);
        logger.debug("Scheduling next departure at " + departureTime);
        environment.addEvent(new BankEvent(BankEvent.DEPARTURE, departureTime, customer));
    }

    public static void main(String[] args) throws Exception {
        Bank bank = new Bank(4);
        // long seed = 137790L;
        Simulator<BankState> simulator = new Simulator<BankState>(bank, bank);
        simulator.runSimulation();
        SummaryStatistics customerDelay = simulator.getAccumulatorRegistry().getStatistics(CUSTOMER_DELAY);
        SummaryStatistics numberInQueue = simulator.getAccumulatorRegistry().getStatistics(NUMBER_IN_QUEUE);
        System.out.println("Average number in queue = " + numberInQueue.getMean());
        String s = Bank.report("delay", customerDelay);
        System.out.println(s);
    }

}