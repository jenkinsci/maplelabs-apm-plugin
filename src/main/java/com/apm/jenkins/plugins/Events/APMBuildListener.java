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
        private static final Logger logger = Logger.getLogger(APMBuildListener.class.getName());
        private BuildEvent buildCollector;

        @Override
        public void onStarted(Run run, TaskListener listener) {
            try {
                logger.fine("Start BuildListener#onStarted");
                buildCollector = new BuildEventCollector(run, listener);
                buildCollector.collectEventData(BuildEvent.Type.STARTED);

                // item.getInQueueSince() may raise a NPE if a worker node is spinning up to run
                // the job.
                // This could be expected behavior with ec2 spot instances/ecs containers,
                // meaning no waiting
                // queue times if the plugin is spinning up an instance/container for one/first
                // job.
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
                logger.fine("Start BuildListener#onCompleted");
                buildCollector = new BuildEventCollector(run, listener);
                buildCollector.collectEventData(BuildEvent.Type.COMPLETED);
                logger.info("End BuildListener#onCompleted");
            } catch (Exception e) {
                logger.severe("Failed to process build completion");
                e.printStackTrace();
            }
        }
    }
