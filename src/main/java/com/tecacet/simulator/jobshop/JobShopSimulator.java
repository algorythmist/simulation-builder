package com.tecacet.simulator.jobshop;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecacet.simulator.EventQueue;
import com.tecacet.simulator.SimulationException;
import com.tecacet.util.QueueListener;

/**
 * A manufacturing system consists of FIVE work stations and each station <b>
 * The stations 1,2,3,4,5 consist of 3,2,4,3,1 identical machines </b>. In
 * effect, the system is a network of five multi-server queues. Assume jobs
 * arrive with IID exponential inter-arrivals of mean 0.25 hour. There are
 * <b>three types of jobs 1,2,3 with probabilities 0.3,0.5,0.2 </b>. 1,2,3 job
 * types require 4,3,5 tasks to be done and each task must be done at a
 * specified station and in a prescribed order. The routings for the different
 * jobs are:
 * 
 * <b>Job Type </b> <b>Work stations in routing </b> <b>Mean service times </b>
 * 1 3,1,2,5 .50,.60,.85,.50 2 4,1,3 1.10,.80,.75 3 2,5,1,4,3
 * 1.20,.25,.70,.90,1.00
 * 
 * If a job arrives at a particular station and finds all machines in that
 * station busy, the job joins a single FIFO queue at the station. <b>The time
 * to perform a task at a particular is an independent 2-Erlang rv </b> whose
 * mean depends on the job type and the station.
 * 
 * Assuming no loss of continuity between successive days, we simulate the
 * system for <b>365 eight hour days </b> end estimate the <b>expected overall
 * delay </b>. We use the true job-type probabilities as weights in computing
 * the later quantity. In addition we estimate the <b>expected average delay in
 * queue for each station </b>.
 * 
 * NOTES: If X iw an independent 2-Erlang rv with mean r, then X = Y_1 + Y_2
 * where Y_i ~ exp with mean r/2.
 */

public class JobShopSimulator {

    private final Logger logger = LoggerFactory.getLogger(JobShopSimulator.class);

    static final String INPUT_FILE = "jobshop.ini";

    protected EventQueue eventQueue = new EventQueue();
    protected RandomDataGenerator generator = null;
    
    protected int iteration = 0;
    protected double currentTime = 0.0;

    // params
    protected int jobTypes = 3;
    protected double maxTime;
    protected long seed;
    protected double meanInterarrivalTime;
    protected int stations;

    protected SummaryStatistics[] completionTimes;
    protected DelayAccumulator[] delayAccumulators; // one for each station

    
    @SuppressWarnings("serial")
    class DelayAccumulator extends SummaryStatistics implements QueueListener<Job> {
        public DelayAccumulator() {
            super();
        }

        public void itemInserted(Job j) {
        }

        public void itemRemoved(Job j) {
            addValue(currentTime - j.getStartTime());
        }
    }

    public JobShopSimulator() throws SimulationException {
        readInputParameters(INPUT_FILE);
        generator = new RandomDataGenerator(); // TODO seed
        completionTimes = new SummaryStatistics[jobTypes];
        for (int i = 0; i < jobTypes; i++) {
            completionTimes[i] = new SummaryStatistics();
        }
        delayAccumulators = new DelayAccumulator[stations];
        for (int i = 0; i < stations; i++) {
            delayAccumulators[i] = new DelayAccumulator();
        }
    }

    private void readInputParameters(String sFilename) throws SimulationException {
        try {
            PropertiesConfiguration parameters = new PropertiesConfiguration(sFilename);
            seed = parameters.getLong("SEED", 0);
            meanInterarrivalTime = parameters.getDouble("MEAN_INTERARRIVAL");
            maxTime = parameters.getInt("FINAL_TIME", 1000);
            stations = parameters.getInt("STATIONS", 5);
        } catch (ConfigurationException e) {
            throw new SimulationException("Failed to read parameters");
        }
    }

    void scheduleArrival(double time) throws SimulationException {
        time += generator.nextExponential(meanInterarrivalTime);
        Job job = getJobInstance();
        job.startTime = time;
        logger.debug("Scheduling arrival of job " + job);
        eventQueue.insert(new JobShopEvent(JobShopEvent.ARRIVAL, time, job));
    }

    public void scheduleDeparture(double time, Job job) throws SimulationException {
        logger.debug("Scheduling departure of job " + job);
        Task task = job.getCurrentTask();
        time += nextErlang2(task.meanServiceTime);
        eventQueue.insert(new JobShopEvent(JobShopEvent.DEPARTURE, time, job));
    }

    private double nextErlang2(double dMean) {
        return (generator.nextExponential(dMean / 2.0) + generator.nextExponential(dMean / 2.0));
    }

    /**
     * <b>Job Type </b> <b>Work stations in routing </b> <b>Mean service times
     * </b> 1 3,1,2,5 .50,.60,.85,.50 2 4,1,3 1.10,.80,.75 3 2,5,1,4,3
     * 1.20,.25,.70,.90,1.00
     */
    private Job getJobInstance() {
        Job job = new Job();
        double dRandom = generator.nextUniform(0, 1); // TODO check this
        if (dRandom < 0.3) {
            job.type = 1;
            job.probability = 0.3;
        } else if (dRandom < 0.8) {
            job.type = 2;
            job.probability = 0.5;
        } else {
            job.type = 3;
            job.probability = 0.2;
        }
        switch (job.type) {
        case 1:
            job.tasks = new Task[4];
            job.tasks[0] = new Task(2, .50);
            job.tasks[1] = new Task(0, .60);
            job.tasks[2] = new Task(1, .85);
            job.tasks[3] = new Task(4, .50);
            break;
        case 2:
            job.tasks = new Task[3];
            job.tasks[0] = new Task(3, 1.10);
            job.tasks[1] = new Task(0, .80);
            job.tasks[2] = new Task(2, .75);
            break;
        case 3:
            job.tasks = new Task[5];
            job.tasks[0] = new Task(1, 1.20);
            job.tasks[1] = new Task(4, .25);
            job.tasks[2] = new Task(0, .70);
            job.tasks[3] = new Task(3, .90);
            job.tasks[4] = new Task(2, 1.00);
            break;
        }
        return job;
    }

    protected boolean simulationEnded() {
        return (currentTime >= maxTime);
    }

    public void run() throws SimulationException {
        scheduleArrival(0.0);
        iteration = 1;
        JobShopState state = new JobShopState(this, stations);
        for (int i = 0; i < stations; i++) {
            state.stations[i].queue.addQueueListener(delayAccumulators[i]);
        }
        while (!simulationEnded()) {
            JobShopEvent zEvent = (JobShopEvent) eventQueue.getNextEvent();
            currentTime = zEvent.getTime();
            state.update(zEvent);
            iteration++;
        }
    }

    public void results() {
        for (int i = 0; i < completionTimes.length; i++) {
            logger.info("Average completion time for job type " + i + " : " + completionTimes[i].getMean());
        }
        double dTotalDelay = 0.0;
        for (int i = 0; i < delayAccumulators.length; i++) {
            logger.info("Average queue delay at station " + i + " : " + delayAccumulators[i].getMean());
            dTotalDelay += delayAccumulators[i].getMean();
        }
        logger.info("Average total delay : " + dTotalDelay + "\n");
        logger.info("Average delay for job type 1 : " + dTotalDelay * .3);
        logger.info("Average delay for job type 2 : " + dTotalDelay * .5);
        logger.info("Average delay for job type 3 : " + dTotalDelay * .2);
    }

    public static void main(String[] asArgs) throws SimulationException {
        JobShopSimulator sim = new JobShopSimulator();
        sim.run();
        sim.results();
    }
}