package com.tecacet.simulator.bank;

import com.tecacet.simulator.SimulationEvent;

public class BankEvent extends SimulationEvent {
    static final int ARRIVAL = 0;
    static final int DEPARTURE = 1;
    static final int BANK_CLOSES = 2;

    private BankCustomer customer;

    public BankEvent(int type, double time) {
        super(type, time);
    }

    public BankEvent(int type, double time, BankCustomer customer) {
        super(type, time);
        this.customer = customer;
    }

    public BankCustomer getCustomer() {
        return customer;
    }
}
