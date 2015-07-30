package com.tecacet.simulator;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

public class TimeAwareStatisticsTest {

    @Test
    public void testGetTimeAverage() {
        Clock clock = new Clock(100);
        TimeAwareStatistics accumulator = new TimeAwareStatistics(clock);
        accumulator.addValue(1.0);
        clock.setTime(300);
        accumulator.addValue(2.0);
        assertEquals((1.00 * 100 + 2.00 * 200)/300, accumulator.getMean(),0.0001);
    }
    
    @Test
    public void testHistory() {
        Clock clock = new Clock(100);
        TimeAwareStatistics accumulator = new TimeAwareStatistics(clock);
        accumulator.addValue(1.0);
        clock.setTime(300);
        accumulator.addValue(2.0);
        Map<Double,Double> history = accumulator.getHistory();
        Iterator<Double> i = history.keySet().iterator();
        double time = i.next();
        assertEquals(100.0, time,0.001);
        assertEquals(1.0, history.get(time),0.001);
        time = i.next();
        assertEquals(300.0, time,0.001);
        assertEquals(2.0, history.get(time),0.001);
    }

}
