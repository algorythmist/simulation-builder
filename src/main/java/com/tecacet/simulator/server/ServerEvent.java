package com.tecacet.simulator.server;

import com.tecacet.simulator.SimulationEvent;

class ServerEvent extends SimulationEvent {
    public static final int ARRIVAL = 0;
    public static final int CPU_DONE = 1;

    private Job job;

    public ServerEvent(int type, double time) {
        super(type, time);
    }

    public ServerEvent(int type, double time, Job job) {
        super(type, time);
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
