package com.apm.jenkins.plugins.metrics;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.logging.Logger;

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
        stuck = -1;
        aborted = -1;
        pending = -1;
        blocked = -1;
        started = -1;
        buildable = -1;
        completed = -1;
        queueSize = -1;
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
        if(stuck < 0) stuck = 0;
        this.stuck++;
    }

    private int getAborted() {
        return this.aborted;
    }

    private void incrementAborted() {
        if(aborted < 0) aborted = 0;
        this.aborted++;
    }

    private int getPending() {
        return this.pending;
    }

    private void incrementPending() {
        if(pending < 0) pending = 0;
        this.pending++;
    }

    private int getBlocked() {
        return this.blocked;
    }

    private void incrementBlocked() {
        if(blocked < 0) blocked = 0;
        this.blocked++;
    }

    private int getStarted() {
        return this.started;
    }

    private void incrementStarted() {
        if(started < 0) started = 0;
        this.started++;
    }

    private int getBuildable() {
        return this.buildable;
    }

    private void incrementBuildable() {
        if(buildable < 0) buildable = 0;
        this.buildable++;
    }

    private int getCompleted() {
        return this.completed;
    }

    private void incrementCompleted() {
        if(completed < 0) completed = 0;
        this.completed++;
    }

    /**
     * This function will set queue details
     * @params details
     */
    @Override
    public void setDetails(Object details) {
        clear();
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
            
    }

    /**
     * This function will get queue details
     * 
     * @return
     */
    @Override
    public HashMap<String, Object> getDetails() {
        HashMap<String, Object> jobStats_dict = new HashMap<>();
        jobStats_dict.put("queueStuck", getStuck());
        jobStats_dict.put("queuePending", getPending());
        jobStats_dict.put("queueBlocked", getBlocked());
        jobStats_dict.put("queueSize", getQueueSize());
        jobStats_dict.put("queueBuildable", getBuildable());
        jobStats_dict.put("num_job_aborted", getAborted());
        jobStats_dict.put("num_job_started", getStarted());
        jobStats_dict.put("num_job_completed", getCompleted());
        return jobStats_dict;
    }
    
}
