package com.apm.jenkins.plugins.Metrics;

import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Computer;
import jenkins.model.Jenkins;
import hudson.model.PeriodicWork;

import com.apm.jenkins.plugins.Utils;
import com.apm.jenkins.plugins.Metrics.DataModel.NodeMetricsImpl;
import com.apm.jenkins.plugins.Metrics.DataModel.QueueMetricsImpl;
import com.apm.jenkins.plugins.Metrics.DataModel.JenkinsMetricsImpl;
import com.apm.jenkins.plugins.Metrics.interfaces.IPublishMetrics;

@Extension
public class MetricsCollector extends PeriodicWork {
    private static final IPublishMetrics nodeMetrics = new NodeMetricsImpl();
    private static final IPublishMetrics queueMetrics = new QueueMetricsImpl();
    private static final IPublishMetrics jenkinsMetrics = new JenkinsMetricsImpl();
    private static final Logger logger = Logger.getLogger(MetricsCollector.class.getName());

    @Override
    public long getRecurrencePeriod() {
        return Utils.publisherTime;
    }

    @Override
    protected void doRun() {
        if(Utils.getGlobalDescriptor().getIsMetricEnabled()) {
            try {
                logger.info("Start Computing metrics");
                Jenkins instance = Jenkins.getInstanceOrNull();
    
                if (instance != null) {
                    Utils.sendMetrics(queueMetrics.collectMetrics(instance));
                    
                    Utils.sendMetrics(jenkinsMetrics.collectMetrics(instance));
                    Computer[] computers = instance.getComputers();
                    if (computers != null)
                        Utils.sendMetrics(nodeMetrics.collectMetrics(computers));
                    else
                        logger.warning("couldn't retrieve computers");
                } else {
                    logger.warning("Instance is null");
                }
                logger.info("End Computing metrics");
            } catch (Exception e) {
                logger.severe("Failed to compute and send Jenkins metrics : "+e.toString());
            }
        }
    }

}
