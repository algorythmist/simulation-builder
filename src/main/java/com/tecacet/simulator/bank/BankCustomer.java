package com.tecacet.simulator.bank;

import com.tecacet.simulator.queue.Customer;

/** Like a Customer but also contains teller information */
public class BankCustomer extends Customer {
    /** teller where this customer is queued */
    private int teller;

    public BankCustomer(double time) {
        super(time);
    }

    /** 
     * teller number where this customer is queued or serviced 
     */
    public int getTeller() {
        return teller;
    }

    /** 
     * teller number where this customer is queued or serviced 
     */
    void setTeller(int teller) {
        this.teller = teller;
    }

}
