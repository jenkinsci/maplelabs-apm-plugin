package com.apm.jenkins.plugins.publishers;

import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.PeriodicWork;
import jenkins.model.Jenkins;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.*;
import com.apm.jenkins.plugins.interfaces.APMClient;
import com.apm.jenkins.plugins.metrics.JenkinsMetrics;

/**
 * This class registers a {@link PeriodicWork} with Jenkins to run periodically in order to enable
 * us to compute metrics related to Jenkins level metrics.
 */
@Extension
public class APMJenkinsPublisher extends PeriodicWork {

    private static final JenkinsMetrics jenkinsMetrics = new JenkinsMetrics();
    private static final Logger logger = Logger.getLogger(APMJenkinsPublisher.class.getName());

    @Override
    public long getRecurrencePeriod() {
        return  APMUtil.publisherTime;
    }

    @Override
    protected void doRun() throws Exception {
        try {
            logger.fine("doRun called: Computing Jenkins metrics");

            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            if (client == null) return;
            jenkinsMetrics.setDetails(Jenkins.getInstanceOrNull());                        
			client.postMetric(jenkinsMetrics.getDetails(), "metric");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to compute and send Jenkins metrics");
        }
    }
    
}
