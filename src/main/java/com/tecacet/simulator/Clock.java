package com.tecacet.simulator;


/**
 * A helper class that keeps track of time.
 */
public class Clock {
    double time;

    /** Create a clock starting at time 0.0 */
    public Clock() {
        time = 0.0;
    }

    /** Create a clock starting at a certain time */
    public Clock(double time) {
        this.time = time;
    }
    

    /** Set the clock time */
    public void setTime(double time) {
        this.time = time;
    }

    /** Get the time stored in the clock */
    public double getTime() {
        return time;
    }
}
