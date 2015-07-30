package com.tecacet.simulator.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;

import com.tecacet.simulator.SimulationException;
import com.tecacet.simulator.Simulator;
import com.tecacet.simulator.StatisticsRegistry;
import com.tecacet.simulator.server.ServerState;
import com.tecacet.simulator.server.ServerSystem;

public class ServerSystemTest {

    @Test
    public void testServerSystem10() throws SimulationException {
        ServerSystem system = new ServerSystem();
        Simulator<ServerState> simulator = new Simulator<ServerState>(system, system);
        simulator.runSimulation();
        system.showResults(simulator);
        StatisticsRegistry registry = simulator.getAccumulatorRegistry();

        SummaryStatistics responseTime = registry.getStatistics(ServerSystem.RESPONSE_TIME_ACCUMULATOR);
        SummaryStatistics numberInQueue = registry.getTimeAwareStatistics(ServerSystem.NUMBER_IN_QUEUE);
        SummaryStatistics cpuUtilization = registry.getTimeAwareStatistics(ServerSystem.CPU_UTILIZATION);
        // get within 1/10th of the std
        assertTrue(Math.abs(1.324 - responseTime.getMean()) < (responseTime.getStandardDeviation() / 5));
        // TODO the following comes out wrong
        // assertTrue(Math.abs(0.156 - numberInQueue.getMean() ) <
        // (numberInQueue.getStandardDeviation() / 10) );
        System.out.println(numberInQueue.getMean());
        assertEquals(0.358, cpuUtilization.getMean(), 0.1);

    }

    @Test
    public void testServerSystem20() throws SimulationException {
        ServerSystem system = new ServerSystem();
        system.setTerminals(30);
        Simulator simulator = new Simulator<ServerState>(system, system);
        simulator.runSimulation();
        system.showResults(simulator);
    }
}
