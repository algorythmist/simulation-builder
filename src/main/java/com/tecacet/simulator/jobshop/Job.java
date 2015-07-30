package com.tecacet.simulator.jobshop;

class Job {
    private static int CURRENT_ID = 0;
    private int id; /* surrogate key */
    int type; /* one of three event types */
    double probability; /* probability of occurring */
    Task[] tasks; /* Tasks for this job */

    int currentTask = 0; /* current (not completed) task */
    int server; /* pointer to server handing this job */
    double startTime; /* time job was requested */

    public Job() {
        CURRENT_ID++;
        id = CURRENT_ID;
    }

    public Task getCurrentTask() {
        return tasks[currentTask];
    }

    public int getNumberOfTasks() {
        return tasks.length;
    }

    public int getNumberOfCompletedTasks() {
        return currentTask;
    }

    public double getStartTime() {
        return startTime;
    }

    public int getType() {
        return type;
    }

    public String toString() {
        return (id + " (type = " + type + ")");
    }
}
