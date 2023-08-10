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
		logger.info("Inside onStarted method");
		
		try {
            logger.fine("Start APMBuildListener#onStarted");
            buildCollector = new BuildEventCollector(run, listener);
            buildCollector.collectEvent(BuildEvent.Type.STARTED);
            
            // item.getInQueueSince() may raise a NPE if a worker node is spinning up to run the job.
            // This could be expected behavior with ec2 spot instances/ecs containers, meaning no waiting
            // queue times if the plugin is spinning up an instance/container for one/first job.
                Queue queue = Queue.getInstance();
                Queue.Item item = queue.getItem(run.getQueueId());
                try {
                	long waitingMs = (APMUtil.currentTimeMillis() - item.getInQueueSince());
                	logger.info("Job waiting time: "+TimeUnit.MILLISECONDS.toSeconds(waitingMs));                
               } catch (RuntimeException e) {
                    logger.warning("Unable to compute 'waiting' metric. " +
                            "item.getInQueueSince() unavailable, possibly due to worker instance provisioning");
                } 

            logger.info("End APMBuildListener#onStarted");         
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
            buildCollector = new BuildEventCollector(run, listener);
            buildCollector.collectEvent(BuildEvent.Type.COMPLETED);
            logger.info("End APMBuildListener#onCompleted");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process build completion");
        }
    }
    
}
