package com.tecacet.simulator.server;

import java.text.NumberFormat;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecacet.simulator.SimulationEnvironment;
import com.tecacet.simulator.SimulationEvent;
import com.tecacet.simulator.SimulationException;
import com.tecacet.simulator.StatisticsRegistry;
import com.tecacet.simulator.StochasticSystem;
import com.tecacet.simulator.Terminator;
import com.tecacet.util.PropertiesLoader;

public class ServerSystem implements StochasticSystem<ServerState>, Terminator<ServerState> {

    public static final String CPU_UTILIZATION = "CPU_UTILIZATION";
    public static final String NUMBER_IN_QUEUE = "NUMBER_IN_QUEUE";
    public static final String RESPONSE_TIME_ACCUMULATOR = "RESPONSE_TIME";

    static final String PARAM_FILE = "server.properties";

    // parameters
    private int totalJobs = 1000;
    private double quantum = 0.1;
    private double swapTime = 0.015;
    private double meanJobTime = 0.8;
    private double meanInterarrival = 25;
    private int terminals = 2;

    private int jobsCompleted;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServerSystem() {
        readInputParameters();
    }

    private void readInputParameters() {
        PropertiesLoader loader = new PropertiesLoader(PARAM_FILE);
        loader.readInputParameters(this);
    }

    public ServerState getInitialState(SimulationEnvironment<ServerState> environment) {
        return new ServerState();
    }

    public void initialize(SimulationEnvironment<ServerState> env) {
        jobsCompleted = 0;
        // schedule an arrival for each terminal
        for (int terminal = 0; terminal < terminals; terminal++) {
            scheduleArrival(0.0, terminal, env);
        }
    }

    public boolean simulationEnded(SimulationEnvironment<ServerState> env) {
        return jobsCompleted >= totalJobs;
    }

    public ServerState getNextState(SimulationEvent e, SimulationEnvironment<ServerState> env)
            throws SimulationException {
        StatisticsRegistry registry = env.getAccumulatorRegistry();
        SummaryStatistics numberInQueue = registry.getTimeAwareStatistics(NUMBER_IN_QUEUE);
        SummaryStatistics cpuUtilization = registry.getTimeAwareStatistics(CPU_UTILIZATION);

        ServerEvent event = (ServerEvent) e;
        ServerState serverState = env.getCurrentState();

        numberInQueue.addValue(serverState.getQueueSize());
        if (!serverState.isServerIdle()) {
            cpuUtilization.addValue(1.0);
        } else {
            cpuUtilization.addValue(0.0);
        }

        switch (event.getType()) {

        case ServerEvent.ARRIVAL:
            logger.debug("New arrival of job " + event.getJob());
            // schedule the next arrival at the terminal
            scheduleArrival(e.getTime(), event.getJob().getTerminal(), env);
            if (serverState.isServerIdle()) {
                serverState.setCurrentJobIndex(0); // get working on the new
                // job
                // schedule the end of cpu run
                scheduleCPU(event, event.getJob(), env);
            }
            serverState.addJob(event.getJob());
            break;

        case ServerEvent.CPU_DONE:
            // schedule the next job
            nextJob(event, serverState, env);
            // if the server is working on something, schedule completion
            if (!serverState.isServerIdle()) {
                scheduleCPU(event, serverState.getCurrentJob(), env);
            }

            break;
        }

        return serverState;
    }

    private void nextJob(ServerEvent event, ServerState s, SimulationEnvironment<ServerState> env) {
        SummaryStatistics responseTime = env.getAccumulatorRegistry().getStatistics(RESPONSE_TIME_ACCUMULATOR);
        Job currentJob = s.getCurrentJob();
        currentJob.update(quantum);
        if (currentJob.getTimeLeft() <= 0.0) {
            // remove completed job
            Job oldJob = s.removeJob();
            logger.debug("Job " + oldJob + " completed.");
            double response = event.getTime() - oldJob.getStartTime();
            responseTime.addValue(response);
            jobsCompleted++;
        }
        s.nextJob();
    }

    private void scheduleArrival(double dTime, int iTerminal, SimulationEnvironment<ServerState> env) {
        // determine job duration
        double duration = env.getRandomGenerator().nextExponential(meanJobTime);
        double arrivalTime = dTime + env.getRandomGenerator().nextExponential(meanInterarrival);
        env.addEvent(new ServerEvent(ServerEvent.ARRIVAL, arrivalTime, new Job(arrivalTime, duration, iTerminal)));
    }

    private void scheduleCPU(ServerEvent event, Job currentJob, SimulationEnvironment<ServerState> env) {
        double nextCPU = event.getTime() + swapTime;
        if (null == currentJob) {
            logger.error("Found empty job queue.");
            return;
        }
        if (currentJob.getTimeLeft() <= quantum) {
            nextCPU += currentJob.getTimeLeft();
        } else {
            nextCPU += quantum;
        }
        env.addEvent(new ServerEvent(ServerEvent.CPU_DONE, nextCPU));
    }

    protected void showResults(SimulationEnvironment<ServerState> env) {
        StatisticsRegistry registry = env.getAccumulatorRegistry();
        SummaryStatistics numberInQueue = registry.getTimeAwareStatistics(NUMBER_IN_QUEUE);
        SummaryStatistics cpuUtilization = registry.getTimeAwareStatistics(CPU_UTILIZATION);
        NumberFormat format = NumberFormat.getInstance();
        System.out.println(terminals + "        "
                + format.format(registry.getStatistics(RESPONSE_TIME_ACCUMULATOR).getMean()) + "         "
                + format.format(numberInQueue.getMean()) + "        " + format.format(cpuUtilization.getMean()));
    }

    public int getTerminals() {
        return terminals;
    }

    public void setTerminals(int terminals) {
        this.terminals = terminals;
    }

    public int getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(int totalJobs) {
        this.totalJobs = totalJobs;
    }

    public double getQuantum() {
        return quantum;
    }

    public void setQuantum(double quantum) {
        this.quantum = quantum;
    }

    public double getSwapTime() {
        return swapTime;
    }

    public void setSwapTime(double swapTime) {
        this.swapTime = swapTime;
    }

    public double getMeanJobTime() {
        return meanJobTime;
    }

    public void setMeanJobTime(double meanJobTime) {
        this.meanJobTime = meanJobTime;
    }

    public double getMeanInterarrival() {
        return meanInterarrival;
    }

    public void setMeanInterarrival(double meanInterarrival) {
        this.meanInterarrival = meanInterarrival;
    }

}