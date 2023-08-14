package com.apm.jenkins.plugins.Metrics;

import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Computer;
import jenkins.model.Jenkins;
import hudson.model.PeriodicWork;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Metrics.DataModel.NodeMetrics;
import com.apm.jenkins.plugins.Metrics.DataModel.QueueMetrics;
import com.apm.jenkins.plugins.Metrics.DataModel.JenkinsMetrics;
import com.apm.jenkins.plugins.Metrics.interfaces.PublishMetrics;

@Extension
public class APMMetricsCollector extends PeriodicWork {
    int i = 0;
    private static final PublishMetrics nodeMetrics = new NodeMetrics();
    private static final PublishMetrics queueMetrics = new QueueMetrics();
    private static final PublishMetrics jenkinsMetrics = new JenkinsMetrics();
    private static final Logger logger = Logger.getLogger(APMMetricsCollector.class.getName());

    @Override
    public long getRecurrencePeriod() {
        return APMUtil.publisherTime;
    }

    @Override
    protected void doRun() {
        if(APMUtil.getAPMGlobalDescriptor().getIsMetricEnabled()) {
            try {
                logger.info("Start Computing metrics");
                Jenkins instance = Jenkins.getInstanceOrNull();
    
                if (instance != null) {
                    queueMetrics.sendMetrics(instance);
                    jenkinsMetrics.sendMetrics(instance);
                    Computer[] computers = instance.getComputers();
                    if (computers != null)
                        nodeMetrics.sendMetrics(computers);
                    else
                        logger.warning("couldn't retrieve computers");
                } else {
                    logger.warning("Instance is null");
                }
                logger.info("End Computing metrics");
            } catch (Exception e) {
                logger.severe("Failed to compute and send Jenkins metrics");
                e.printStackTrace();
            }
        }
    }

}
