package com.tecacet.simulator.jobshop;

class Task {
    int station; /* Station to service this task */
    double meanServiceTime; /* Expected time to complete task */

    public Task(int station, double meanServiceTime) {
        this.station = station;
        this.meanServiceTime = meanServiceTime;
    }
}
