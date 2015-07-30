package com.tecacet.simulator;

/**
 * 
 * Models the interaction of a stochastic system with the simulation environment
 *
 * @param <S> the State space
 */
public interface StochasticSystem<S> {

    /**
     * Provide the initial state for a simulation run
     * 
     * @param environment
     * @return
     */
    S getInitialState(SimulationEnvironment<S> environment);
    
    /**
     * Get the next state based on the current event and the simulation environment.
     * 
     * @param event
     * @param environment
     * @return
     * @throws SimulationException
     */
    S getNextState(final SimulationEvent event, SimulationEnvironment<S> environment) throws SimulationException;

    /**
     * Initialize the system. Usually used to populate event queue with initial events
     * 
     * @param environment
     * @throws SimulationException
     */
    void initialize(SimulationEnvironment<S> environment) throws SimulationException;
}