package com.tecacet.simulator.queue;

import static org.junit.Assert.*;

import org.junit.Test;

public class QueueStateTest {

    @Test
    public void testAll() {
        QueueState state = new QueueState(2);
        assertFalse(state.allBusy());

        Customer customer1 = new Customer(1.0);
        assertEquals(0, state.accept(customer1));

        Customer customer2 = new Customer(2.0);
        assertEquals(1, state.accept(customer2));

        assertEquals(customer1, state.getCustomerInService(0));

        assertEquals(2, state.getNumberInSystem());
        assertEquals(0, state.getQueueSize());

        assertTrue(state.isServerBusy(0));
        assertTrue(state.allBusy());

        assertEquals(customer2, state.departure(1));

        assertFalse(state.allBusy());
    }

}
