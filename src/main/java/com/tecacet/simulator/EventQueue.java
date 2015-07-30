package com.tecacet.simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * An ordered queue of events that provides access to the most recent event.
 * 
 * It is not a queue in the FIFO sense because any new events added 
 * are ordered based on their time.
 * 
 */
public class EventQueue {
    /**
     * Compares events based on time of occurrence
     */
    class EventComparator implements Comparator<SimulationEvent> {
        public int compare(SimulationEvent event1, SimulationEvent event2) {
            if (event1 == null)
                throw new IllegalArgumentException("The first parameter is null");
            if (event2 == null)
                throw new IllegalArgumentException("The second parameter is null");

            double diff = event1.getTime() - event2.getTime();
            return diff > 0 ? 1 : (diff < 0 ? -1 : 0);
        }
    }

    protected EventComparator comparator = new EventComparator();
    protected PriorityQueue<SimulationEvent> list = new PriorityQueue<SimulationEvent>(100, comparator);
    protected List<SimulationEvent> pastEvents = null;

    public EventQueue() {
        this(false);
    }

    public EventQueue(boolean trackPastEvents) {
        if (trackPastEvents) {
            pastEvents = new ArrayList<SimulationEvent>();
        }
    }

    public void insert(SimulationEvent event) {
       list.add(event);
    }

    /**
     * Get the next time-ordered event from the queue. The event is removed from
     * the queue
     * 
     * @return
     * @throws SimulationException
     */
    public SimulationEvent getNextEvent() throws SimulationException {
        if (isEmpty()) {
            throw new SimulationException("The event queue is empty.");
        }

        SimulationEvent event = list.remove();
        if (null != pastEvents) {
            pastEvents.add(event);
        }
        return event;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public void removeAll() {
        list.clear();
    }

    public List<SimulationEvent> getPastEvents() {
        return pastEvents;
    }
}
