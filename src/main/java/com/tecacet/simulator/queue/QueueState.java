package com.tecacet.simulator.queue;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * The state of a service queue is described by the state of each server and the state of each customer in the queue.
 * 
 * @author Dimitri Papaioannou
 *
 */
public class QueueState implements Serializable {

    private Deque<Customer> customerQueue;
    private Server[] servers;

    public QueueState(int serverCount) {
        this(serverCount, new LinkedList<>());
    }

    private QueueState(int serverCount, Deque<Customer> queue) {
        customerQueue = queue;
        servers = new Server[serverCount];
        for (int i = 0; i < serverCount; i++) {
            servers[i] = new Server();
        }
    }

    /**
     * Add a new customer to the queue
     */
    private void queue(Customer c) {
        customerQueue.add(c);
    }

    /**
     * Indicates if all servers are busy
     *
     */
    public boolean allBusy() {
        for (Server server : servers) {
            if (!server.isServerBusy()) {
                return false;
            }
        }
        return true;
    }

    /**
     * A customer enters the system
     * @param customer the customer
     * @return the server where the customer was accepted, -1 if queued
     */
    public int accept(Customer customer) {
        for (int i = 0; i < servers.length; i++) {
            Server server = servers[i];
            if (!server.isServerBusy()) {
                server.accept(customer);
                return i;
            }
        }
        queue(customer);
        return -1;
    }

    /** customer service completed */
    public Customer departure(int server) {
        Customer customer = servers[server].departure();
        if (!customerQueue.isEmpty()) {
            servers[server].accept(customerQueue.pop());
        }
        return customer;
    }

    public Customer getCustomerInService(int serverIndex) {
        return servers[serverIndex].getCustomerInService();
    }

    public boolean isServerBusy(int server) {
        return servers[server].isServerBusy();
    }

    /**
     * The number of customers waiting in queue
     *
     */
    public int getQueueSize() {
        return customerQueue.size();
    }

    /**
     * The number of customers waiting in queue and being serviced
     *
     */
    public int getNumberInSystem() {
        int number = customerQueue.size();
        for (Server server : servers) {
            if (server.isServerBusy()) {
                number += 1;
            }
        }
        return number;
    }

    @Override
    public QueueState clone() {
        return SerializationUtils.clone(this);
    }
}
