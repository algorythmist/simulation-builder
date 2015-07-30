package com.tecacet.util;

import java.util.Iterator;
import java.util.Map;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.tecacet.simulator.TimeAwareStatistics;

public class JFreeChartUtil {

    public static XYSeriesCollection getTimeSeriesCollection(String name, TimeAwareStatistics statistics) {
        XYSeries series = getXYSeries(name, statistics);
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);
        return collection;
    }

    public static XYSeries getXYSeries(String name, TimeAwareStatistics statistics) {
        XYSeries series = new XYSeries(name);
        Map<Double, Double> history = statistics.getHistory();
        for (Iterator<Double> i = history.keySet().iterator(); i.hasNext();) {
            double time = i.next();
            Double value = history.get(time);
            series.add(time, value);
        }
        return series;
    }
}
