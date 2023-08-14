package com.apm.jenkins.plugins.Events;

import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.Queue;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Events.interfaces.BuildEvent;
import com.apm.jenkins.plugins.Events.Collector.BuildEventCollector;

@Extension
public class APMBuildListener extends RunListener<Run> {
    private BuildEvent buildCollector;
    private static final Logger logger = Logger.getLogger(APMBuildListener.class.getName());

    @Override
    public void onStarted(Run run, TaskListener listener) {
        try {
            logger.info("Start BuildListener#onStarted");
            buildCollector = new BuildEventCollector(run, listener);
            buildCollector.collectEventData(BuildEvent.Type.STARTED);

            // waiting time
            Queue queue = Queue.getInstance();
            Queue.Item item = queue.getItem(run.getQueueId());
            try {
                long waitingMs = (APMUtil.currentTimeMillis() - item.getInQueueSince());
                logger.info("Job waiting time: " + TimeUnit.MILLISECONDS.toSeconds(waitingMs));
            } catch (RuntimeException e) {
                logger.warning("Unable to compute 'waiting' metric. " +
                        "item.getInQueueSince() unavailable, possibly due to worker instance provisioning");
            }

            logger.info("End BuildListener#onStarted");
        } catch (Exception e) {
            logger.severe("Failed to process build start");
            e.printStackTrace();
        }
    }

    /**
     * This function will send job status to client
     */
    @Override
    public void onCompleted(Run run, @Nonnull TaskListener listener) {
        try {
            logger.info("Start BuildListener#onCompleted");
            buildCollector = new BuildEventCollector(run, listener);
            buildCollector.collectEventData(BuildEvent.Type.COMPLETED);
            logger.info("End BuildListener#onCompleted");
        } catch (Exception e) {
            logger.severe("Failed to process build completion");
            e.printStackTrace();
        }
    }
}
