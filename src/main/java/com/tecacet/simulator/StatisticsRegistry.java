package com.tecacet.simulator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * A repository where accumulators can be registered and retrieved by name
 */
public class StatisticsRegistry {

    private Map<String, SummaryStatistics> accumulators = new HashMap<>();
    private Clock clock;

    public StatisticsRegistry(Clock clock) {
        this.clock = clock;
    }

    public SummaryStatistics getStatistics(String name) {
        SummaryStatistics accumulator = accumulators.get(name);
        if (accumulator == null) {
            accumulator = new SummaryStatistics();
            accumulators.put(name, accumulator);
        }
        return accumulator;
    }

    public SummaryStatistics getTimeAwareStatistics(String name) {
        SummaryStatistics accumulator = accumulators.get(name);
        if (accumulator == null) {
            accumulator = new TimeAwareStatistics(clock);
            accumulators.put(name, accumulator);
        }
        return accumulator;
    }

    public void clearAll() {
        accumulators.values().forEach(SummaryStatistics::clear);
    }

}
