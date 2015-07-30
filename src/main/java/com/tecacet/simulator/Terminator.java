package com.tecacet.simulator;

/**
 * Captures the termination condition of a simulation.
 * 
 * @author Dimitri Papaioannou
 *
 * @param <S> The State space
 */
public interface Terminator<S> {

    boolean simulationEnded(SimulationEnvironment<S> environment);
}
