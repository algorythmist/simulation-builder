package com.tecacet.simulator;

/**
 * Base class for a simulation event.
 */
public class SimulationEvent {

    protected int type;
    protected double time;

    public SimulationEvent(int type, double time) {
        this.type = type;
        this.time = time;
    }

    /**
     * This integer identifies the type of event
     * 
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * Time the event occurred
     * 
     * @return
     */
    public double getTime() {
        return time;
    }

}