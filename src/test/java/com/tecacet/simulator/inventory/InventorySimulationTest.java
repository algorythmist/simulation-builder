package com.tecacet.simulator.inventory;

import static org.junit.Assert.assertTrue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;

import com.tecacet.simulator.SimulationException;
import com.tecacet.simulator.Simulator;
import com.tecacet.simulator.StatisticsRegistry;

public class InventorySimulationTest {

    @Test
    public void testInventorySimulation() throws SimulationException, ConfigurationException {
        InventorySystem inventory = new InventorySystem();
        Simulator<InventoryState> simulator = new Simulator<InventoryState>(inventory, inventory);
        simulator.setSeed(123798L);
        simulator.runSimulation();

        StatisticsRegistry registry = simulator.getAccumulatorRegistry();
        SummaryStatistics orderingCost = registry.getStatistics(InventorySystem.ORDERING_COST);
        SummaryStatistics shortageCost = registry.getTimeAwareStatistics(InventorySystem.SHORTAGE_COST);
        SummaryStatistics holdingCost = registry.getTimeAwareStatistics(InventorySystem.HOLDING_COST);

        double averageOrderingCost = orderingCost.getSum() / inventory.getSimulationMonths();
        assertTrue(Math.abs(99.26 - averageOrderingCost) < (orderingCost.getStandardDeviation() / 5.0));
        System.out.println("shortage =" + shortageCost.getMean() * 5);
        assertTrue(Math.abs(9.25 - holdingCost.getMean()) < (holdingCost.getStandardDeviation() / 2.0));
    }

    @Test
    public void testAll() throws ConfigurationException, SimulationException {
        int[] thresholds = { 20, 40, 60 };
        int[] storages = { 40, 60, 80, 100 };

      
        for (int threshold : thresholds) {
            for (int storage : storages) {
                if (storage > threshold) {
                    InventorySystem inventory = new InventorySystem();
                    Simulator<InventoryState> simulator = new Simulator<InventoryState>(inventory, inventory);
                    inventory.setThreshold(threshold);
                    inventory.setMaxStorage(storage);
                    simulator.runSimulation();
                    StatisticsRegistry registry = simulator.getAccumulatorRegistry();
                    SummaryStatistics orderingCost = registry.getStatistics(InventorySystem.ORDERING_COST);
                    SummaryStatistics shortageCost = registry.getTimeAwareStatistics(InventorySystem.SHORTAGE_COST);
                    SummaryStatistics holdingCost = registry.getTimeAwareStatistics(InventorySystem.HOLDING_COST);
                    System.err.println(String.format("(%d,%d): %.2f %f %f", threshold, storage, orderingCost.getSum()
                            / inventory.getSimulationMonths(), holdingCost.getSum() / inventory.getSimulationMonths(),
                            shortageCost.getSum() / inventory.getSimulationMonths()));
                }
            }
        }
    }
}
