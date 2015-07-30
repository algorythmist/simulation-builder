package com.tecacet.simulator.jobshop;

public class Server {
    protected Job currentJob = null; /* Job in service */
    protected Station station; /* pointer to parent station */

    public Server(Station station) {
        this.station = station;
    }

    public void addJob(Job job) {
        currentJob = job;
    }

    public void removeJob() {
        currentJob = null;
    }

    public boolean isIdle() {
        return (currentJob == null);
    }
}
