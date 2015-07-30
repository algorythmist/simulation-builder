package com.tecacet.simulator;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * A statistics collector that records time-weighted values. It multiplies every
 * value added by the length of time since the last update. It optionally
 * maintains a history of the values entered and the times entered
 */
public class TimeAwareStatistics extends SummaryStatistics {

    private static final long serialVersionUID = 6144305244467076301L;
    
    protected Clock clock;
    protected double lastEventTime = 0.0;
    protected boolean saveHistory = true;
    protected Map<Double, Double> history = new TreeMap<Double, Double>();

    /**
     * @param simulator
     *            the simulator that will monitor state-change events and update
     *            the accumulator
     */
    public TimeAwareStatistics(Clock c) {
        clock = c;
    }

    @Override
    public double getMean() {
        return super.getSum() / clock.getTime();
    }

    @Override
    public void addValue(double increment) {
        double interval = clock.getTime() - lastEventTime;
        super.addValue(increment * interval);
        lastEventTime = clock.getTime();
        if (saveHistory) {
            history.put(clock.getTime(), increment);
        }
    }

    public boolean isSaveHistory() {
        return saveHistory;
    }

    public void setSaveHistory(boolean saveHistory) {
        this.saveHistory = saveHistory;
    }

    public Map<Double, Double> getHistory() {
        return history;
    }

}