package com.tecacet.simulator.queue;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;

import com.tecacet.simulator.SimulationEnvironment;
import com.tecacet.simulator.SimulationException;
import com.tecacet.simulator.Simulator;
import com.tecacet.simulator.StatisticsRegistry;
import com.tecacet.simulator.Terminator;
import com.tecacet.simulator.queue.MM1System;
import com.tecacet.simulator.queue.QueueState;
import com.tecacet.simulator.queue.QueueingSystem;

public class MM1Test {

    @Test
    public void testFiveIterations() throws SimulationException {
        final Terminator<QueueState> terminator = env -> (env.getCurrentIteration() >= 5);
        MM1System mm1 = new MM1System();
        Simulator<QueueState> simulator = new Simulator<>(mm1, terminator);
        simulator.runSimulation();

    }

    @Test
    public void testSimulation() throws Exception {
        final MM1System mm1 = new MM1System();
        Simulator<QueueState> simulator = new Simulator<>(mm1, mm1.getDefaultTerminator());
        simulator.setSeed(19348468L);
        simulator.runSimulation();
        StatisticsRegistry registry = simulator.getAccumulatorRegistry();
        assertEquals(10000L, registry.getStatistics(MM1System.CUSTOMERS_DELAYED_ACCUMULATOR).getN());
        assertEquals(0.5, registry.getStatistics("DELAY").getMean(), 0.1); // average
        // delay
        assertEquals(0.5, registry.getTimeAwareStatistics(MM1System.INQUEUE_AREA_ACCUMULATOR).getMean(), 0.1); // time
        // average customers in queue
        assertEquals(0.5, registry.getTimeAwareStatistics(MM1System.SERVER_AREA_ACCUMULATOR).getMean(), 0.1); // average
        // server utilization
    }

    @Test
    public void testAgain() throws SimulationException {
        // meanInterarrivalTime = 1.000
        // meanServiceTime = 0.500
        // delaysRequired = 10000
        // seed = 1000

        ExponentialDistribution arrival = new ExponentialDistribution(1.000);
        ExponentialDistribution service = new ExponentialDistribution(0.500);
        QueueingSystem mm1 = new QueueingSystem(arrival, service, 1);
        final Simulator<QueueState> simulator = new Simulator<>(mm1, env -> {
            SummaryStatistics customersDelayed = env.getAccumulatorRegistry().getStatistics(
                    QueueingSystem.CUSTOMERS_DELAYED_ACCUMULATOR);
            return customersDelayed.getN() >= 10000;
        });

        simulator.runSimulation();
        StatisticsRegistry registry = simulator.getAccumulatorRegistry();
        System.out.println(simulator.getClock().getTime());

        assertEquals(10000L, registry.getStatistics(MM1System.CUSTOMERS_DELAYED_ACCUMULATOR).getN());
        assertEquals(0.5, registry.getStatistics(QueueingSystem.DELAY_ACCUMULATOR).getMean(), 0.1); // average
        // delay
        assertEquals(0.5, registry.getTimeAwareStatistics(MM1System.INQUEUE_AREA_ACCUMULATOR).getMean(), 0.1); // time
        // average customers in queue
        assertEquals(0.5, registry.getTimeAwareStatistics(MM1System.SERVER_AREA_ACCUMULATOR).getMean(), 0.1); // average
        // server utilization
    }
}
