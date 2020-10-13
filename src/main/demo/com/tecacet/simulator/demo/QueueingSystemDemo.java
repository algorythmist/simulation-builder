package com.tecacet.simulator.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.tecacet.simulator.Clock;
import com.tecacet.simulator.SimulationEnvironment;
import com.tecacet.simulator.SimulationException;
import com.tecacet.simulator.Simulator;
import com.tecacet.simulator.Terminator;
import com.tecacet.simulator.TimeAwareStatistics;
import com.tecacet.simulator.queue.QueueState;
import com.tecacet.simulator.queue.QueueingSystem;
import com.tecacet.util.JFreeChartUtil;

public class QueueingSystemDemo extends JPanel implements ActionListener {

    private JFormattedTextField arrivalRate;
    private JFormattedTextField serviceRate;
    private JFormattedTextField numberOfServers;
    private JFormattedTextField lengthOfSimulation;
    private ChartPanel chartPanel;

    public QueueingSystemDemo() {
        arrivalRate = new JFormattedTextField(NumberFormat.getNumberInstance());
        arrivalRate.setColumns(5);
        arrivalRate.setValue(0.8);
        serviceRate = new JFormattedTextField(NumberFormat.getNumberInstance());
        serviceRate.setColumns(5);
        serviceRate.setValue(1.0);
        numberOfServers = new JFormattedTextField(NumberFormat.INTEGER_FIELD);
        numberOfServers.setColumns(3);
        numberOfServers.setValue(2);
        lengthOfSimulation = new JFormattedTextField(NumberFormat.getNumberInstance());
        lengthOfSimulation.setColumns(4);
        lengthOfSimulation.setValue(50.0);
        JButton run = new JButton("Run Simulation");
        run.addActionListener(this);
        XYDataset collection = JFreeChartUtil.getTimeSeriesCollection("Number in queue", new TimeAwareStatistics(new Clock()));
        JFreeChart chart = ChartFactory.createXYLineChart("Queue Size", "time", "queue size", collection,
                PlotOrientation.VERTICAL, true, false, false);

        chartPanel = new ChartPanel(chart);
        Box box = new Box(BoxLayout.Y_AXIS);
        JPanel options = new JPanel();
        options.add(new JLabel("Arrival Rate"));
        options.add(arrivalRate);
        options.add(new JLabel("Service Rate"));
        options.add(serviceRate);
        options.add(new JLabel("Number of servers"));
        options.add(numberOfServers);
        options.add(new JLabel("Time to simulation end"));
        options.add(lengthOfSimulation);
        options.add(run);
        box.add(options);
        box.add(chartPanel);
        this.add(box);
    }

    public static void main(String[] args) {
        JPanel panel = new QueueingSystemDemo();
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(panel);
        f.pack();
        f.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        Double ar = (Double) arrivalRate.getValue();
        if (ar == null) {
            return;
        }
        Double sr = (Double) serviceRate.getValue();
        if (sr == null) {
            return;
        }
        final Number time = (Number) lengthOfSimulation.getValue();
        ExponentialDistribution arrival = new ExponentialDistribution(ar);
        ExponentialDistribution service = new ExponentialDistribution(sr);
        QueueingSystem system = new QueueingSystem(arrival, service, (Integer)numberOfServers.getValue());

        Simulator<QueueState> simulator = new Simulator<>(system,
                environment -> environment.getCurrentTime() >= time.doubleValue());
        try {
            simulator.runSimulation();
        } catch (SimulationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TimeAwareStatistics numberInQueue = (TimeAwareStatistics) simulator.getAccumulatorRegistry()
                .getTimeAwareStatistics(QueueingSystem.INQUEUE_AREA_ACCUMULATOR);
        TimeAwareStatistics serverBusy = (TimeAwareStatistics) 
            simulator.getAccumulatorRegistry().getStatistics(QueueingSystem.SERVER_AREA_ACCUMULATOR);
        XYSeries queue = JFreeChartUtil.getXYSeries("Number in queue", numberInQueue);
        XYSeries server = JFreeChartUtil.getXYSeries("Server Busy", serverBusy);
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        seriesCollection.addSeries(queue);
        //seriesCollection.addSeries(server);
        
        
        JFreeChart chart = ChartFactory.createXYLineChart("Queue Size", "time", "queue size", seriesCollection,
                PlotOrientation.VERTICAL, true, false, false);
        //chart.getPlot().setBackgroundPaint(Color.WHITE);
        chartPanel.setChart(chart);
    }

}
