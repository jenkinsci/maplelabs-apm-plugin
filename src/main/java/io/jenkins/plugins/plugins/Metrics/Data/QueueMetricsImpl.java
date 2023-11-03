package io.jenkins.plugins.maplelabs.Metrics.Data;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.logging.Logger;

import io.jenkins.plugins.maplelabs.Utils;
import io.jenkins.plugins.maplelabs.Client.IClient;
import io.jenkins.plugins.maplelabs.Client.Snappyflow.SnappyFlow;
import io.jenkins.plugins.maplelabs.Metrics.interfaces.IPublishMetrics;

import hudson.model.Job;
import hudson.model.Run;
import hudson.model.Queue;
import hudson.model.Result;
import jenkins.model.Jenkins;

public class QueueMetricsImpl implements IPublishMetrics {

    private int stuck;
    private int aborted;
    private int pending;
    private int blocked;
    private int started;
    private int buildable;
    private int completed;
    private int queueSize;
    private static final Logger logger = Logger.getLogger(QueueMetricsImpl.class.getName());

    private void clear() {
        stuck = 0;
        aborted = 0;
        pending = 0;
        blocked = 0;
        started = 0;
        buildable = 0;
        completed = 0;
        queueSize = 0;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int size) {
        queueSize = size;
    }

    public int getStuck() {
        return this.stuck;
    }

    public void incrementStuck() {
        this.stuck++;
    }

    public int getAborted() {
        return this.aborted;
    }

    public void incrementAborted() {
        this.aborted++;
    }

    public int getPending() {
        return this.pending;
    }

    public void incrementPending() {
        this.pending++;
    }

    public int getBlocked() {
        return this.blocked;
    }

    public void incrementBlocked() {
        this.blocked++;
    }

    public int getStarted() {
        return this.started;
    }

    public void incrementStarted() {
        this.started++;
    }

    public int getBuildable() {
        return this.buildable;
    }

    public void incrementBuildable() {
        this.buildable++;
    }

    public int getCompleted() {
        return this.completed;
    }

    public void incrementCompleted() {
        this.completed++;
    }

    /**
     * This function will set queue details and send details to client
     * 
     * @params details
     */
    @Override
    public HashMap<String, Object> collectMetrics(Object details) {
        clear();
        Jenkins instance = (Jenkins) details;
        if (instance == null) {
            logger.severe("No Jenkins instance");
            return null;
        }
        Queue queue = instance.getQueue();
        final Queue.Item[] items = queue.getItems();
        for (Queue.Item item : items) {
            if (item.isStuck())
                incrementStuck();
            ;
            if (item.isBlocked())
                incrementBlocked();
            ;
            if (item.isBuildable())
                incrementBuildable();
            ;
            if (queue.isPending(item.task))
                incrementPending();
            ;
        }

        setQueueSize(items.length);

        // Old Jobs
        for (Job job : instance.getAllItems(Job.class)) {
            SortedMap<Integer, Run> builds = job.getBuildsAsMap();
            for (Run run : builds.values()) {
                Result result = run.getResult();
                incrementStarted();
                if (result == Result.SUCCESS) {
                    incrementCompleted();
                } else if (result == Result.ABORTED) {
                    incrementAborted();
                    ;
                }
            }
        }
        HashMap<String, Object> jobDetails = SnappyFlow.getSnappyflowTags("jobMetrics");
        jobDetails.put("queue_stuck", getStuck());
        jobDetails.put("queue_size", getQueueSize());
        jobDetails.put("queue_pending", getPending());
        jobDetails.put("queue_blocked", getBlocked());
        jobDetails.put("jobs_aborted", getAborted());
        jobDetails.put("jobs_started", getStarted());
        jobDetails.put("queue_buildable", getBuildable());
        jobDetails.put("jobs_completed", getCompleted());

        return jobDetails;
    }

}
