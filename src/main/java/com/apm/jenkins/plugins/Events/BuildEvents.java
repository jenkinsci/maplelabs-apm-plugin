package com.apm.jenkins.plugins.Events;

import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.Queue;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

import com.apm.jenkins.plugins.Utils;
import com.apm.jenkins.plugins.Events.interfaces.IBuildEvent;
import com.apm.jenkins.plugins.Events.Collector.BuildEventCollectorImpl;

@Extension
public class BuildEvents extends RunListener<Run> {
    private IBuildEvent buildCollector;
    private static final Logger logger = Logger.getLogger(BuildEvents.class.getName());

    @Override
    public void onStarted(Run run, TaskListener listener) {
        try {
            logger.info("Start BuildListener#onStarted");
            buildCollector = new BuildEventCollectorImpl(run, listener);
            buildCollector.collectEventData(IBuildEvent.Type.STARTED);
            Utils.sendEvent(buildCollector);
            // waiting time
            Queue queue = Queue.getInstance();
            Queue.Item item = queue.getItem(run.getQueueId());
            try {
                long waitingMs = (Utils.getCurrentTimeInMillis() - item.getInQueueSince());
                logger.info("Job waiting time: " + TimeUnit.MILLISECONDS.toSeconds(waitingMs));
            } catch (RuntimeException e) {
                logger.warning("Unable to compute 'waiting' metric. " +
                        "item.getInQueueSince() unavailable, possibly due to worker instance provisioning");
            }

            logger.info("End BuildListener#onStarted");
        } catch (Exception e) {
            logger.severe("Failed to process build start : "+e.toString());
        }
    }

    /**
     * This function will send job status to client
     */
    @Override
    public void onCompleted(Run run, @Nonnull TaskListener listener) {
        try {
            logger.info("Start BuildListener#onCompleted");
            buildCollector = new BuildEventCollectorImpl(run, listener);
            buildCollector.collectEventData(IBuildEvent.Type.COMPLETED);
            Utils.sendEvent(buildCollector);
            logger.info("End BuildListener#onCompleted");
        } catch (Exception e) {
            logger.severe("Failed to process build completion : "+e.toString());
        }
    }
}
