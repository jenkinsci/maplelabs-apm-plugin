package com.apm.jenkins.plugins.Events;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.DataModel.BuildData;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.Queue;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

@Extension
public class APMBuildListener extends RunListener<Run> {
	private static final Logger logger = Logger.getLogger(APMBuildListener.class.getName());
	
	@Override
    public void onStarted(Run run, TaskListener listener) {
		logger.info("Inside onStarted method");
		
		try {
            logger.fine("Start APMBuildListener#onStarted");

            // Get APM Client Instance
            // APMClient client = new APMHttpClient();

            // Collect Build Data
            BuildData buildData;
            try {
                buildData = new BuildData(run, listener);
                logger.info("build : "+buildData);
            } catch (IOException | InterruptedException e) {
                APMUtil.severe(logger, e, "Failed to parse started build data");
                return;
            }

            // Send an event
            // APMEvent event = new BuildStartedEvent(buildData);
            // client.postEvent(event);

            // Send a metric
            // item.getInQueueSince() may raise a NPE if a worker node is spinning up to run the job.
            // This could be expected behavior with ec2 spot instances/ecs containers, meaning no waiting
            // queue times if the plugin is spinning up an instance/container for one/first job.
            //     Queue queue = getQueue();
            //     Queue.Item item = queue.getItem(run.getQueueId());
            //     // Map<String,String> buildParams = buildData.getBuildParameters();
            //     try {
            //     	long waitingMs = (APMUtil.currentTimeMillis() - item.getInQueueSince());
            //     	logger.info("Job waiting time: "+TimeUnit.MILLISECONDS.toSeconds(waitingMs));                
            //    } catch (RuntimeException e) {
            //         logger.warning("Unable to compute 'waiting' metric. " +
            //                 "item.getInQueueSince() unavailable, possibly due to worker instance provisioning");
            //     } 

            logger.info("End APMBuildListener#onStarted");
            // Submit counter
            // client.incrementCounter("jenkins.job.started", hostname, tags); */            
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process build start");
        }
	}
	
    /**
     * This function will send job status to client
     */
    @Override
    public void onCompleted(Run run, @Nonnull TaskListener listener) {
        logger.info("Inside onCompleted method");
        try {
            logger.fine("Start APMBuildListener#onCompleted");

            // Get APM Client Instance
            // APMClient client = new APMHttpClient();

            // Collect Build Data
            BuildData buildData;
            try {
                buildData = new BuildData(run, listener);
            } catch (IOException | InterruptedException e) {
                APMUtil.severe(logger, e, "Failed to parse completed build data");
                return;
            }

            // Send an event
            // APMEvent event = new BuildCompletedEvent(buildData);
            // client.postEvent(event);
            // logger.info("Event end : "+event);

            // Send a metric
            logger.fine(String.format("[%s]: Duration: %s", buildData.getJobName(null),
                    toTimeString(buildData.getDuration(0L))));

            logger.info("End APMBuildListener#onCompleted");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process build completion");
        }
    }
    
    /**
     * This function will return time in mins and sec string format
     * @param millis
     * @return
     */
    private static String toTimeString(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long seconds = totalSeconds - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%d min, %d sec", minutes, seconds);
    }

    public Queue getQueue() {
        return Queue.getInstance();
    }

}
