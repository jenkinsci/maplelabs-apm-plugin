package com.apm.jenkins.plugins.listeners;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.ClientBase;
import com.apm.jenkins.plugins.DataModel.BuildData;
import com.apm.jenkins.plugins.events.BuildStartedEvent;
import com.apm.jenkins.plugins.interfaces.APMClient;
import com.apm.jenkins.plugins.interfaces.APMEvent;

import hudson.Extension;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

@Extension
public class APMBuildListener extends RunListener<Run> {
	
	private static final Logger logger = Logger.getLogger(APMBuildListener.class.getName());
	
	@Override
    public void onInitialize(Run run) {
		logger.info("Inside onInitialize method");
	}
	
	@Override
    public void onStarted(Run run, TaskListener listener) {
		logger.info("Inside onStarted method");
		
		try {
            // Process only if job is NOT in excluded and is in included
            if (!APMUtil.isJobTracked(run.getParent().getFullName())) {
                return;
            }
            logger.fine("Start APMBuildListener#onStarted");

            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            if (client == null) {
                return;
            }

            // Collect Build Data
            BuildData buildData;
            try {
                buildData = new BuildData(run, listener);
            } catch (IOException | InterruptedException e) {
                APMUtil.severe(logger, e, "Failed to parse started build data");
                return;
            }

            // Send an event
            APMEvent event = new BuildStartedEvent(buildData);
            client.event(event);

            // Send a metric
            // item.getInQueueSince() may raise a NPE if a worker node is spinning up to run the job.
            // This could be expected behavior with ec2 spot instances/ecs containers, meaning no waiting
            // queue times if the plugin is spinning up an instance/container for one/first job.
            Queue queue = getQueue();
            Queue.Item item = queue.getItem(run.getQueueId());
            Map<String, Set<String>> tags = buildData.getTags();
            Map<String,String> buildParams = buildData.getBuildParameters();
            try {
            	long waitingMs = (APMUtil.currentTimeMillis() - item.getInQueueSince());
            	logger.info("Job waiting time: "+TimeUnit.MILLISECONDS.toSeconds(waitingMs));
            	logger.info("Build Tags:"+tags);
            	
                logger.info("Build Params:"+buildParams);              
                
           } catch (RuntimeException e) {
                logger.warning("Unable to compute 'waiting' metric. " +
                        "item.getInQueueSince() unavailable, possibly due to worker instance provisioning");
            } 

            logger.info("End APMBuildListener#onStarted");
            // Submit counter
            // client.incrementCounter("jenkins.job.started", hostname, tags); */            
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process build start");
        }
	}
	
	@Override
    public void onCompleted(Run run, @Nonnull TaskListener listener) {
		logger.info("Inside onCompleted method");
	}
	
	@Override
    public void onFinalized(Run run) {
		logger.info("Inside onFinalized method");
	}
	
    @Override
    public void onDeleted(Run run) {
    	logger.info("Inside onDeleted method");
    }
    
    public Queue getQueue() {
        return Queue.getInstance();
    }

}
