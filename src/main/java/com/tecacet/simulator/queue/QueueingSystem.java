package com.tecacet.simulator.queue;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecacet.simulator.SimulationEnvironment;
import com.tecacet.simulator.SimulationEvent;
import com.tecacet.simulator.StochasticSystem;

public class QueueingSystem implements StochasticSystem<QueueState> {

    public static final String NUMBER_IN_SYSTEM = "NUMBER_IN_SYSTEM";

    public static final String INQUEUE_AREA_ACCUMULATOR = "INQUEUE_AREA";

    public static final String SERVER_AREA_ACCUMULATOR = "SERVER_AREA";

    public static final String CUSTOMERS_DELAYED_ACCUMULATOR = "CUSTOMERS_DELAYED";

    public static final String TOTAL_WAITING = "TOTAL_WAITING";

    public static final String QUEUE_WAITING = "QUEUE_WAITING";

    public static final String DELAY_ACCUMULATOR = "DELAY";

    private RealDistribution arrivalDistribution;
    private RealDistribution serviceDistribution;
    private int servers;

    private final Logger logger = LoggerFactory.getLogger(QueueingSystem.class);

    protected QueueingSystem() {

    }

    public QueueingSystem(RealDistribution arrivalDistribution, RealDistribution serviceDistribution, int servers) {
        this.arrivalDistribution = arrivalDistribution;
        this.serviceDistribution = serviceDistribution;
        this.servers = servers;

    }

    @Override
    public QueueState getInitialState(SimulationEnvironment<QueueState> environment) {
        return new QueueState(servers);
    }

    @Override
    public void initialize(SimulationEnvironment<QueueState> env) {
        addArrival(0.0, env);
    }

    @Override
    public QueueState getNextState(SimulationEvent event, SimulationEnvironment<QueueState> environment) {
        return getNextStatePrv((QueueEvent) event, environment);
    }

    private QueueState getNextStatePrv(QueueEvent event, SimulationEnvironment<QueueState> environment) {
        SummaryStatistics totalWaitingTime = environment.getAccumulatorRegistry().getStatistics(TOTAL_WAITING);
        SummaryStatistics queueWaitingTime = environment.getAccumulatorRegistry().getStatistics(QUEUE_WAITING);
        SummaryStatistics numberInQueue = environment.getAccumulatorRegistry().getTimeAwareStatistics(INQUEUE_AREA_ACCUMULATOR);
        SummaryStatistics numberInSystem = environment.getAccumulatorRegistry()
                .getTimeAwareStatistics(NUMBER_IN_SYSTEM);
        SummaryStatistics customersDelayed = environment.getAccumulatorRegistry().getStatistics(
                CUSTOMERS_DELAYED_ACCUMULATOR);
        SummaryStatistics delayAccumulator = environment.getAccumulatorRegistry().getStatistics(DELAY_ACCUMULATOR);

        SummaryStatistics serverStatusArea = environment.getAccumulatorRegistry().getTimeAwareStatistics(
                SERVER_AREA_ACCUMULATOR);

        QueueState state = environment.getCurrentState();

        numberInQueue.addValue(state.getQueueSize());
        numberInSystem.addValue(state.getNumberInSystem());

        // update server area
        if (state.allBusy()) {
            serverStatusArea.addValue(1.0);
        } else {
            serverStatusArea.addValue(0.0);
        }
        // numberInQueueArea.addValue(state.customersInQueue());

        switch (event.getType()) {
        case QueueEvent.ARRIVAL:
            logger.debug("Customer arrived at " + event.getTime());
            addArrival(event.getTime(), environment);
            Customer customer = new Customer(event.getTime());
            int server = state.accept(customer);
            if (server >= 0) {
                customer.setTimeServiceStarted(event.getTime());
                // server was idle, serve the new arrival, so we need departure
                logger.debug("Customer entering service at " + event.getTime());
                addDeparture(event.getTime(), server, environment);
                delayAccumulator.addValue(0.0);
            } else {
                customersDelayed.addValue(1.0);
                logger.debug("Customer queued");
            }
            break;
        case QueueEvent.DEPARTURE:
            // logger.debug(event.getFormattedTime() + ": Departure of customer
            // " + state.getCustomerInService().getID());
            Customer customerLeaving = state.departure(event.getServer());
            totalWaitingTime.addValue(event.getTime() - customerLeaving.getArrivalTime());
            queueWaitingTime.addValue(customerLeaving.getTimeServiceStarted() - customerLeaving.getArrivalTime());
            if (state.isServerBusy(event.getServer())) {
                Customer customer1 = state.getCustomerInService(event.getServer());
                customer1.setTimeServiceStarted(event.getTime());
                logger.debug("Customer " + customer1.getId() + " enters service.");
                // queue next departure
                addDeparture(event.getTime(), event.getServer(), environment);
                double delay = event.getTime() - customer1.getArrivalTime();
                delayAccumulator.addValue(delay);
            } else {
                //TODO: measure server idle time
                logger.debug("Server idle...");
            }
            break;
        default:
            logger.error("Invalid event type " + event.getType());
        }
        return state;
    }

    protected void addArrival(double time, SimulationEnvironment<QueueState> env) {
        double uni = env.getRandomGenerator().nextUniform(0.0, 1.0);
        double eventTime = time + arrivalDistribution.inverseCumulativeProbability(uni);
        env.addEvent(new QueueEvent(QueueEvent.ARRIVAL, eventTime, -1));
        logger.debug("Scheduling next arrival at " + eventTime);
    }

    protected void addDeparture(double time, int server, SimulationEnvironment<QueueState> env) {
        double uni = env.getRandomGenerator().nextUniform(0.0, 1.0);
        double eventTime = time + serviceDistribution.inverseCumulativeProbability(uni);
        env.addEvent(new QueueEvent(QueueEvent.DEPARTURE, eventTime, server));
        logger.debug("Scheduling next departure at " + eventTime);
    }

}
