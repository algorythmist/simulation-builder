package com.tecacet.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

class TestEvent extends SimulationEvent {
    String name;

    TestEvent(String name, double time) {
        super(0, time);
        this.name = name;
    }

    public double getTime() {
        return time;
    }
}

public class EventQueueTest {
    

    @Test
    public void testGetNextEvent() throws SimulationException {

        EventQueue queue = new EventQueue();

        queue.insert(new TestEvent("1", 1.5));
        queue.insert(new TestEvent("2", 0.5));
        queue.insert(new TestEvent("3", 2.3));
        queue.insert(new TestEvent("4", 2.1));

        assertEquals(4, queue.size());

        TestEvent event = (TestEvent) queue.getNextEvent();
        assertEquals("2", event.name);
        event = (TestEvent) queue.getNextEvent();
        assertEquals("1", event.name);
        event = (TestEvent) queue.getNextEvent();
        assertEquals("4", event.name);
        event = (TestEvent) queue.getNextEvent();
        assertEquals("3", event.name);

        assertTrue(queue.isEmpty());

        try {
            event = (TestEvent) queue.getNextEvent();
            fail("queue is empty but no exception thrown");
        } catch (SimulationException se) {
            // good catch!
        }
    }
}
