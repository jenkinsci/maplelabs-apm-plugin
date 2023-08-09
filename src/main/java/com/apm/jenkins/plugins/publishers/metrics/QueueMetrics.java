package com.apm.jenkins.plugins.publishers.metrics;

import java.util.HashMap;
import java.util.SortedMap;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.Communication;
import com.apm.jenkins.plugins.interfaces.StatDetails;

import hudson.model.Job;
import hudson.model.Run;
import hudson.model.Queue;
import hudson.model.Result;
import jenkins.model.Jenkins;

public class QueueMetrics implements StatDetails {

    private int stuck;
    private int aborted;
    private int pending;
    private int blocked;
    private int started;
    private int buildable;
    private int completed;
    private int queueSize;

    private void clear(){
        stuck = 0;
        aborted = 0;
        pending = 0;
        blocked = 0;
        started = 0;
        buildable = 0;
        completed = 0;
        queueSize = 0;
    }

    private int getQueueSize() {
        return queueSize;
    }

    private void setQueueSize(int size) {
        queueSize = size;
    }
    private int getStuck() {
        return this.stuck;
    }

    private void incrementStuck() {
        this.stuck++;
    }

    private int getAborted() {
        return this.aborted;
    }

    private void incrementAborted() {
        this.aborted++;
    }

    private int getPending() {
        return this.pending;
    }

    private void incrementPending() {
        this.pending++;
    }

    private int getBlocked() {
        return this.blocked;
    }

    private void incrementBlocked() {
        this.blocked++;
    }

    private int getStarted() {
        return this.started;
    }

    private void incrementStarted() {
        this.started++;
    }

    private int getBuildable() {
        return this.buildable;
    }

    private void incrementBuildable() {
        this.buildable++;
    }

    private int getCompleted() {
        return this.completed;
    }

    private void incrementCompleted() {
        this.completed++;
    }

    /**
     * This function will set queue details and send details to client
     * @params details
     */
    @Override
    public void sendDetails(Object details) {
        Jenkins instance = (Jenkins)details;
        if(instance == null) return;
        Queue queue = instance.getQueue();
        final Queue.Item[] items = queue.getItems();
            for (Queue.Item item : items) {                                       
                if(item.isStuck())  incrementStuck();;                
                if(item.isBlocked())  incrementBlocked();;
                if (item.isBuildable()) incrementBuildable();;
                if(queue.isPending(item.task)) incrementPending();;
           }

           setQueueSize(items.length);

           // Old Jobs
            for (Job job : instance.getAllItems(Job.class)) {
                SortedMap<Integer,Run> builds = job.getBuildsAsMap();
                for (Run run : builds.values()) {
                    Result result = run.getResult();
                    incrementStarted();
                    if (result == Result.SUCCESS) {
                        incrementCompleted();
                    } else if (result == Result.ABORTED) {
                        incrementAborted();;
                    }
                }
            }
        HashMap<String, Object> jobDetails = APMUtil.getSnappyflowTags("jobStat");
        jobDetails.put("queueStuck", getStuck());
        jobDetails.put("queueSize", getQueueSize());
        jobDetails.put("queuePending", getPending());
        jobDetails.put("queueBlocked", getBlocked());
        jobDetails.put("num_job_aborted", getAborted());
        jobDetails.put("num_job_started", getStarted());
        jobDetails.put("queueBuildable", getBuildable());
        jobDetails.put("num_job_completed", getCompleted());

        Communication communicationClient = APMUtil.getAPMGlobalDescriptor().getCommunicationClient();
        if(communicationClient != null) {
            communicationClient.transmit(jobDetails);
            clear();
        }
    }
    
}
