package com.apm.jenkins.plugins;

import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Computer;
import jenkins.model.Jenkins;
import hudson.model.PeriodicWork;

import com.apm.jenkins.plugins.Metrics.DataModel.NodeMetrics;
import com.apm.jenkins.plugins.Metrics.DataModel.QueueMetrics;
import com.apm.jenkins.plugins.Metrics.DataModel.JenkinsMetrics;
import com.apm.jenkins.plugins.Metrics.interfaces.PublishMetrics;

@Extension
public class APMMetricsCollector extends PeriodicWork {
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
        try {
            logger.info("Computing Jenkins metrics");
            Jenkins instance = Jenkins.getInstanceOrNull();

            if (instance != null) {
                queueMetrics.sendMetrics(instance);
                jenkinsMetrics.sendMetrics(instance);
                Computer[] computers = instance.getComputers();
                if (computers != null) 
                    nodeMetrics.sendMetrics(computers);

            } else {
                logger.info("Instance is null and couldn't retrieve computers.");
            }
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to compute and send Jenkins metrics");
        }
    }

}
