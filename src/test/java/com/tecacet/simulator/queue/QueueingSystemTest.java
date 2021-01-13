package com.tecacet.simulator.queue;

import static org.junit.Assert.assertEquals;

import com.tecacet.simulator.SimulationEnvironment;
import com.tecacet.simulator.SimulationException;
import com.tecacet.simulator.Simulator;
import com.tecacet.simulator.Terminator;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;

public class QueueingSystemTest {

    @Test
    public void testMM1QueueingSystem() throws SimulationException {
        ExponentialDistribution arrival = new ExponentialDistribution(1.0);
        ExponentialDistribution service = new ExponentialDistribution(0.5);
        QueueingSystem system = new QueueingSystem(arrival, service, 1);
        Simulator<QueueState> simulator = new Simulator<>(system, environment -> environment.getCurrentIteration() >= 10000);
        simulator.runSimulation();
        SummaryStatistics numberInQueueArea =
                simulator.getAccumulatorRegistry().getTimeAwareStatistics(QueueingSystem.INQUEUE_AREA_ACCUMULATOR);
        assertEquals(0.5, numberInQueueArea.getMean(), 0.1);
        SummaryStatistics delayAccumulator = simulator.getAccumulatorRegistry().getStatistics(QueueingSystem.DELAY_ACCUMULATOR);
        System.out.println(delayAccumulator.getMean());
    }

    @Test
    public void testLittleTheorem() throws SimulationException {
        double arrivalRate = 0.75;
        double serviceRate = 1.0;
        ExponentialDistribution arrival = new ExponentialDistribution(1 / arrivalRate);
        ExponentialDistribution service = new ExponentialDistribution(1 / serviceRate);
        QueueingSystem system = new QueueingSystem(arrival, service, 1);

        Simulator<QueueState> simulator = new Simulator<>(system, environment -> environment.getCurrentIteration() >= 10000);
        simulator.runSimulation();
        SummaryStatistics numberInQueue = simulator.getAccumulatorRegistry().getTimeAwareStatistics(QueueingSystem.INQUEUE_AREA_ACCUMULATOR);
        SummaryStatistics queueWaitingTime = simulator.getAccumulatorRegistry().getStatistics(QueueingSystem.QUEUE_WAITING);
        SummaryStatistics totalWaitingTime = simulator.getAccumulatorRegistry().getStatistics(QueueingSystem.TOTAL_WAITING);
        SummaryStatistics numberInSystem = simulator.getAccumulatorRegistry().getTimeAwareStatistics(QueueingSystem.NUMBER_IN_SYSTEM);

        System.out.printf(" L = lambda * W : %f = %f * %f\n", numberInSystem.getMean(), arrivalRate, totalWaitingTime.getMean());
        System.out.printf(" Lq = lambda * Wq : %f = %f * %f\n", numberInQueue.getMean(), arrivalRate, queueWaitingTime.getMean());
        assertEquals(numberInSystem.getMean(), totalWaitingTime.getMean() * arrivalRate, 0.1);
        assertEquals(numberInQueue.getMean(), queueWaitingTime.getMean() * arrivalRate, 0.1);
    }

}
