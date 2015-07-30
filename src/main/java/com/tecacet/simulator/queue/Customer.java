package com.tecacet.simulator.queue;

/**
 * Generic type representing a client of a service queue
 */
public class Customer {
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

    /** Copy constructor */
    public Customer(Customer c) {
        id = c.id;
        arrivalTime = c.arrivalTime;
    }

    public int getId() {
        return id;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public Customer copy() {
        return new Customer(this);
    }

    public String toString() {
        return Integer.toString(id);
    }

    public boolean equals(Object o) {
        return id == ((Customer) o).id;
    }

    public double getTimeServiceStarted() {
        return timeServiceStarted;
    }

    public void setTimeServiceStarted(double timeServiceStarted) {
        this.timeServiceStarted = timeServiceStarted;
    }
}
