package com.tecacet.simulator.queue;

import com.tecacet.simulator.SimulationEnvironment;
import com.tecacet.simulator.Terminator;
import com.tecacet.util.PropertiesLoader;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The simplest queuing system: 1 server, exponential arrival and departure rate
 *
 * @author Dimitri Papaioannou
 */
public class MM1System extends QueueingSystem {

    protected static final String PROPERTIES_FILE = "mm1.ini";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int delaysRequired;
    private double meanInterarrivalTime;

    // Parameters
    private double meanServiceTime;

    public MM1System() {
        new PropertiesLoader(PROPERTIES_FILE).readInputParameters(this);
    }

    public int getDelaysRequired() {
        return delaysRequired;
    }

    public void setDelaysRequired(int delaysRequired) {
        this.delaysRequired = delaysRequired;
    }

    public double getMeanInterarrivalTime() {
        return meanInterarrivalTime;
    }

    public void setMeanInterarrivalTime(double meanInterarrivalTime) {
        this.meanInterarrivalTime = meanInterarrivalTime;
    }

    public double getMeanServiceTime() {
        return meanServiceTime;
    }

    public void setMeanServiceTime(double meanServiceTime) {
        this.meanServiceTime = meanServiceTime;
    }

    @Override
    public QueueState getInitialState(SimulationEnvironment<QueueState> environment) {
        return new QueueState(1);
    }

    @Override
    protected void addArrival(double time, SimulationEnvironment<QueueState> env) {
        double eventTime;
        eventTime = time + env.getRandomGenerator().nextExponential(meanInterarrivalTime);
        env.addEvent(new QueueEvent(QueueEvent.ARRIVAL, eventTime, 0));
        logger.debug("Scheduling next arrival at " + eventTime);
    }

    @Override
    protected void addDeparture(double time, int server, SimulationEnvironment<QueueState> env) {
        double eventTime = time + env.getRandomGenerator().nextExponential(meanServiceTime);
        env.addEvent(new QueueEvent(QueueEvent.DEPARTURE, eventTime, 0));
        logger.debug("Scheduling next departure at " + eventTime);
    }

    public Terminator<QueueState> getDefaultTerminator() {
        return new DefaultTerminator();
    }


    private final class DefaultTerminator implements Terminator<QueueState> {
        // simulation ends after a certain number of delays
        @Override
        public boolean simulationEnded(SimulationEnvironment<QueueState> env) {
            SummaryStatistics customersDelayed = env.getAccumulatorRegistry().getStatistics(
                    CUSTOMERS_DELAYED_ACCUMULATOR);
            return customersDelayed.getN() >= delaysRequired;
        }
    }
}