package com.tecacet.simulator.queue;

import java.io.Serializable;

/**
 * Generic type representing a client of a service queue
 */
public class Customer implements Serializable {
    static int nextId = 0;

    private int id;
    private double arrivalTime;
    private double timeServiceStarted;

    /** Create a customer arriving at a certain time */
    public Customer(double time) {
        nextId++;
        id = nextId;
        arrivalTime = time;
    }

    public int getId() {
        return id;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public String toString() {
        return Integer.toString(id);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Customer)) {
            return false;
        }
        return id == ((Customer) o).id;
    }

    public double getTimeServiceStarted() {
        return timeServiceStarted;
    }

    public void setTimeServiceStarted(double timeServiceStarted) {
        this.timeServiceStarted = timeServiceStarted;
    }
}
