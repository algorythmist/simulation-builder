package com.tecacet.simulator.jobshop;

import com.tecacet.util.ObservableQueue;

public class Station {
    /*
     * queued jobs
     */
    protected ObservableQueue<Job> queue = new ObservableQueue<Job>();
    protected Server[] servers; /* servers at this station */
    protected int id;

    public Station(int iID, int iServers) {
        id = iID;
        servers = new Server[iServers];
        for (int i = 0; i < iServers; i++) {
            servers[i] = new Server(this);
        }
    }

    public boolean addJob(Job job) {
        boolean bServiced = false;
        for (int m = 0; m < servers.length; m++) {
            if (servers[m].isIdle()) {
                addJobToServer(job, m);
                bServiced = true;
                break;
            }
        } // end for each machine
        if (false == bServiced) {
            queue.queue(job);
        }
        return bServiced;
    }

    public Job removeJob(Job job) {
        Server server = servers[job.server];
        server.removeJob();
        if (!queue.isEmpty()) {
            Job nextJob = (Job) queue.getNext();
            addJobToServer(nextJob, job.server);
            return nextJob;
        } else {
            return null;
        }
    }

    private void addJobToServer(Job job, int serverIndex) {
        Server server = servers[serverIndex];
        server.addJob(job);
        job.server = serverIndex;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
