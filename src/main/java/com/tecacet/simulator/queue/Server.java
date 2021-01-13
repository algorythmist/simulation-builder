package com.tecacet.simulator.queue;


import java.io.Serializable;

public class Server implements Serializable {

    private Customer customerInService = null;

    public boolean isServerBusy() {
        return customerInService != null;
    }

    public Customer getCustomerInService() {
        return customerInService;
    }

    /**
     * accept a new customer in service
     */
    public void accept(Customer c) {
        customerInService = c;
    }

    public Customer departure() {
        Customer customer = customerInService;
        customerInService = null;
        return customer;
    }
}
