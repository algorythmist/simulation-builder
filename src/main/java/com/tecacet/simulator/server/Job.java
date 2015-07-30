package com.tecacet.simulator.server;

import java.text.NumberFormat;

class Job {
    private static int NEXT_ID = 0;
    private int id;
    private double startTime;
    private double timeLeft;
    private int terminal;

    public Job(double startTime, double duration, int iTerminal) {
        ++NEXT_ID;
        id = NEXT_ID;
        this.startTime = startTime;
        this.timeLeft = duration;
        this.terminal = iTerminal;
    }

    public Job(Job job) {
        id = job.id;
        startTime = job.startTime;
        timeLeft = job.timeLeft;
        terminal = job.terminal;
    }

    public int getID() {
        return id;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getTimeLeft() {
        return timeLeft;
    }

    public int getTerminal() {
        return terminal;
    }

    /** Decrease the time left for this job by an increment */
    public void update(double increment) {
        timeLeft -= increment;
    }

    public String toString() {
        NumberFormat zFormat = NumberFormat.getInstance();
        return "id = " + id + "  at terminal " + terminal + " started at " + zFormat.format(startTime)
                + " and time remaining is " + zFormat.format(timeLeft);
    }
}
