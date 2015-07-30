package com.tecacet.simulator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClockTest {

    @Test
    public void testClockDouble() {
        Clock clock = new Clock(10.0);
        assertEquals(10.0, clock.getTime(),0.001);
        clock.setTime(20.0);
        assertEquals(20.0, clock.getTime(),0.001);
    }

}
