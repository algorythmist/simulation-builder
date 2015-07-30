package com.tecacet.simulator.bank;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;

import com.tecacet.simulator.SimulationException;
import com.tecacet.simulator.Simulator;

public class BankTest {

    @Test
    public void testBank() throws SimulationException {
        Bank bank = new Bank(4);
        Simulator<BankState> simulator = new Simulator<BankState>(bank, bank);
        simulator.runSimulation();
        SummaryStatistics numberInQueue = simulator.getAccumulatorRegistry().getStatistics(Bank.NUMBER_IN_QUEUE);
        //there is no way to predict the average in a short iteration
        System.err.println(numberInQueue.getMean());
    }

}
