package com.tecacet.simulator;

public interface Clock {

    double getTime();
}

/**
 * A helper class that keeps track of time.
 */
class InternalClock implements Clock {
    private double time;

    /** Create a clock starting at time 0.0 */
    public InternalClock() {
        time = 0.0;
    }

    /** Create a clock starting at a certain time */
    public InternalClock(double time) {
        this.time = time;
    }
    

    /** Set the clock time */
    public void setTime(double time) {
        this.time = time;
    }

    /** Get the time stored in the clock */
    @Override
    public double getTime() {
        return time;
    }
}
