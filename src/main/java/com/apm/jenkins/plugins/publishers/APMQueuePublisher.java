package com.apm.jenkins.plugins.publishers;

import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Queue;
import jenkins.model.Jenkins;
import hudson.model.PeriodicWork;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.ClientBase;
import com.apm.jenkins.plugins.interfaces.APMClient;
import com.apm.jenkins.plugins.metrics.QueueMetrics;

@Extension
public class APMQueuePublisher extends PeriodicWork{
	
    private static final QueueMetrics queueMetrics = new QueueMetrics();
    private static final Logger logger = Logger.getLogger(APMQueuePublisher.class.getName());

	@Override
	public long getRecurrencePeriod() {
		return  APMUtil.publisherTime;
	}

	@Override
	protected void doRun() throws Exception {
        try {
            logger.fine("doRun called: Computing queue and job metrics");

            APMClient client = ClientBase.getClient();
            if (client == null) return;
            queueMetrics.setDetails(Jenkins.get());
            client.postMetric(queueMetrics.getDetails(), "metric");
        } catch (Exception e) {
        	logger.severe("Failed to compute and send queue metrics, due to:" + e);
        }
    }

}
