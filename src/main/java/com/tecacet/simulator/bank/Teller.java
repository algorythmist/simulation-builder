package com.tecacet.simulator.bank;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Each teller is a queue + a customer in service
 * 
 */
class Teller {
    private Deque<BankCustomer> queue = new LinkedList<BankCustomer>();
    private BankCustomer customerInService = null;

    Teller() {
    }

    /** The queue size of that teller including the customer in service */
    public int queueSize() {
        if (null == customerInService) {
            return 0;
        }
        return 1 + queue.size();
    }

    /** Get the customer currently in service */
    public BankCustomer getCustomerInService() {
        return customerInService;
    }

    /**
     * @return true if the teller is servicing a customer, false if the teller
     *         is idle
     */
    public boolean isBusy() {
        return customerInService != null;
    }

    /**
     * Add a customer at the end of the queue. If the teller is not busy, start
     * servicing the customer
     */
    public void addCustomer(BankCustomer customer) {
        if (isBusy()) {
            queue.offer(customer);
        } else {
            customerInService = customer;
        }
    }

    /**
     * Remove the customer in service. If there are any customers in queue,
     * start servicing the next one.
     * 
     * @param time
     *            the time when the transition occurs is used to mark the end of
     *            waiting for the customer entering service (if any)
     */
    public void removeFirstCustomer() {
        if (queue.isEmpty()) {
            customerInService = null;
        } else {
            customerInService = queue.remove();
        }
    }

    /**
     * Remove the customer at the end of the queue. This is used for jockeying
     */
    public BankCustomer removeLastCustomer() {
        if (!queue.isEmpty()) {
            return queue.removeLast();
        }
        return null;
    }
}
