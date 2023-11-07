package org.maplelabs.jenkins.plugins.Metrics;

import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Computer;
import jenkins.model.Jenkins;
import hudson.model.PeriodicWork;
import hudson.model.AsyncPeriodicWork;
import hudson.model.TaskListener;
import java.io.IOException;
import org.kohsuke.stapler.DataBoundConstructor;

import org.maplelabs.jenkins.plugins.Utils;
import org.maplelabs.jenkins.plugins.Metrics.Data.NodeMetricsImpl;
import org.maplelabs.jenkins.plugins.Metrics.Data.QueueMetricsImpl;
import org.maplelabs.jenkins.plugins.Metrics.Data.JenkinsMetricsImpl;
import org.maplelabs.jenkins.plugins.Metrics.interfaces.IPublishMetrics;


@Extension
public class MetricsCollector extends AsyncPeriodicWork {
    private static final IPublishMetrics nodeMetrics = new NodeMetricsImpl();
    private static final IPublishMetrics queueMetrics = new QueueMetricsImpl();
    private static final IPublishMetrics jenkinsMetrics = new JenkinsMetricsImpl();
    private static final Logger logger = Logger.getLogger(MetricsCollector.class.getName());
    
    @DataBoundConstructor
    public MetricsCollector() {
        super("Metrics_Collector");       
    }

    @Override
    public long getRecurrencePeriod() {
        // Convert the collection interval from minutes to milliseconds
        int recurrancePeriodInMillis =  (Utils.getGlobalDescriptor().getReportingInterval()) * 60 * 1000;
        return recurrancePeriodInMillis;        

    }   

    @Override        
    // protected void execute(TaskListener listener) throws IOException, InterruptedException {
    protected void execute(TaskListener listener) {        
        if (Utils.getGlobalDescriptor().getIsMetricEnabled()) {
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
                logger.severe("Failed to compute and send Jenkins metrics: " + e.toString());
            }
        } else {
            logger.fine("Metric reporting is not enabled");
        }        
    }
}
