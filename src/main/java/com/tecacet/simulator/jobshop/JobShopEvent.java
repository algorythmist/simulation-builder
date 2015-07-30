package com.tecacet.simulator.jobshop;

import com.tecacet.simulator.SimulationEvent;

class JobShopEvent extends SimulationEvent {
    public static final int ARRIVAL = 1;
    public static final int DEPARTURE = 2;

    protected Job job; /* Job associated with this event */

    public JobShopEvent(int type, double time, Job job) {
        super(type, time);
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
