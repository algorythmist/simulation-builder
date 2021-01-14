package com.tecacet.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;

public class StatisticsRegistryTest {

    @Test
    public void testGetAccumulator() {
        StatisticsRegistry registry = new StatisticsRegistry(new InternalClock());
        SummaryStatistics statistics = registry.getStatistics("test");
        assertNotNull(statistics);
    }

    @Test
    public void testGetTimeAccumulator() {
        StatisticsRegistry registry = new StatisticsRegistry(new InternalClock());
        SummaryStatistics statistics = registry.getTimeAwareStatistics("test");
        assertEquals(TimeAwareStatistics.class, statistics.getClass());

    }

}
