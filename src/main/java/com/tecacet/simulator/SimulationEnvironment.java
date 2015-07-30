package com.tecacet.simulator;

import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * This interface provides access to the resources of the simulator
 *
 * @param <S> The state space
 */
public interface SimulationEnvironment<S> {

    /**
     * Access the current state
     * @return
     */
    S getCurrentState();
    
    /**
     * The current iteration
     * @return
     */
    int getCurrentIteration();
    
    /**
     * The current time
     * @return
     */
    double getCurrentTime();
    
    /**
     * Add an event to the event queue
     * @param event
     */
    void addEvent(SimulationEvent event);
    
    /**
     * Access to the random generator, potentially initialized with a seed
     * @return
     */
    RandomDataGenerator getRandomGenerator();
    
    /**
     * Access to the accumulator registry
     * @return
     * @see StatisticsRegistry
     */
    StatisticsRegistry getAccumulatorRegistry();

    
}


