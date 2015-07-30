package com.tecacet.simulator.queue;

import com.tecacet.simulator.SimulationEvent;

public class QueueEvent extends SimulationEvent {

    public static final int ARRIVAL = 1;
    public static final int DEPARTURE = 2;

    private int server;

    public QueueEvent(int type, double time, int server) {
        super(type, time);
        this.server = server;
    }

    public int getServer() {
        return server;
    }

}
