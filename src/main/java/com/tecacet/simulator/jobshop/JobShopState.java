package com.tecacet.simulator.jobshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tecacet.simulator.SimulationException;

class JobShopState {

    private final Logger logger = LoggerFactory.getLogger(JobShopState.class);

    private final JobShopSimulator simulator;
    Station[] stations;

    /**
     * <b>The stations 1,2,3,4,5 consist of 3,2,4,3,1 identical machines </b>.
     */
    public JobShopState(JobShopSimulator simulator, int iStations) {

        this.simulator = simulator;

        stations = new Station[iStations];
        stations[0] = new Station(0, 3);
        stations[1] = new Station(1, 2);
        stations[2] = new Station(2, 4);
        stations[3] = new Station(3, 3);
        stations[4] = new Station(4, 1);
    }

    public void update(JobShopEvent zEvent) throws SimulationException {

        switch (zEvent.getType()) {

        case JobShopEvent.ARRIVAL:
            handleArrival(zEvent);
            break;
        case JobShopEvent.DEPARTURE:
            handleDeparture(zEvent);
            break;
        }
    } // update

    private void handleArrival(JobShopEvent zEvent) throws SimulationException {
        Job zJob = zEvent.getJob();
        Task zTask = zJob.getCurrentTask();
        Station zStation = stations[zTask.station]; // task determines
        // initial station

        logger.debug("Handling arrival of job " + zJob + " at station " + zTask.station);

        this.simulator.scheduleArrival(zEvent.getTime());

        if (zStation.addJob(zJob)) {
            // if serviced, schedule departure
            this.simulator.scheduleDeparture(zEvent.getTime(), zJob);
            // delay is 0
            this.simulator.delayAccumulators[zTask.station].addValue(0.0);
        }
    } // handleArrival

    private void handleDeparture(JobShopEvent zEvent) throws SimulationException {
        Job zJob = zEvent.getJob();
        Task zTask = zJob.getCurrentTask();
        Station zOriginalStation = stations[zTask.station];

        logger.debug("Handling departure of job " + zJob + " from station " + zTask.station);

        Job zNextJob = zOriginalStation.removeJob(zJob);
        if (null != zNextJob) {
            // if next job serviced, schedule departure
            logger.info("Station " + zOriginalStation.id + " starting work on job " + zNextJob);
            this.simulator.scheduleDeparture(zEvent.getTime(), zNextJob);
        }

        if (++zJob.currentTask == zJob.tasks.length) {
            // Job Completed
            double dJobTime = zEvent.getTime() - zJob.getStartTime();
            this.simulator.completionTimes[zJob.getType() - 1].addValue(dJobTime);
            logger.debug("Job " + zJob + " completed in " + dJobTime + " hours");
        } else {
            // more tasks
            zTask = zJob.getCurrentTask();
            logger.debug("Routing job " + zJob + " to station " + zTask.station);
            Station zNextStation = stations[zTask.station];
            if (zNextStation.addJob(zJob)) {
                // if serviced, schedule departure
                this.simulator.scheduleDeparture(zEvent.getTime(), zJob);
                // delay is 0
                this.simulator.delayAccumulators[zTask.station].addValue(0.0);
            }
        }
    }
}