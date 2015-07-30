package com.tecacet.simulator.server;

import java.util.ArrayList;
import java.util.List;

public class ServerState {

	private List<Job> jobQueue = new ArrayList<Job>();
	private int currentJobIndex = -1;

	public ServerState() {
	}

	public boolean isServerIdle() {
		return jobQueue.isEmpty();
	}

	public Job getCurrentJob() {
		if (isServerIdle()) {
			return null;
		} else {
			return (Job) jobQueue.get(currentJobIndex);
		}
	}

	public int getQueueSize() {
		return jobQueue.size();
	}

	public Job removeJob() {
		Job job = jobQueue.remove(currentJobIndex);
		setCurrentJobIndex(currentJobIndex - 1);
		return job;
	}

	public Job nextJob() {
		if (getQueueSize() > 0) {
			// work on the next job
			setCurrentJobIndex((currentJobIndex + 1) % jobQueue.size());
			return jobQueue.get(currentJobIndex);
		} else {
			setCurrentJobIndex(-1);
			return null;
		}
	}

	public void addJob(Job job) {
		jobQueue.add(job);
	}

	void setCurrentJobIndex(int currentJobIndex) {
		this.currentJobIndex = currentJobIndex;
	}

}
