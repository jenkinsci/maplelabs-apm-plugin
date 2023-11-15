package io.jenkins.plugins;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import io.jenkins.plugins.Events.Collector.ComputerEventCollectorImpl;
import io.jenkins.plugins.Metrics.Data.QueueMetricsImpl;
import io.jenkins.plugins.Metrics.interfaces.IPublishMetrics;

import hudson.model.Computer;
import jenkins.model.Jenkins;

public class TestUtils {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testSendEvent(){
        ComputerEventCollectorImpl eventCollector = new ComputerEventCollectorImpl();
        eventCollector.collectEventData(Computer.currentComputer());
        Assert.assertNotNull(Utils.sendEvent(eventCollector));
    }

    @Test
    public void testSendMetrics(){
        IPublishMetrics queueMetrics = new QueueMetricsImpl();
        Jenkins instance = jenkinsRule.getInstance();
        Object sendMetrics = Utils.sendMetrics(queueMetrics.collectMetrics(instance));
        Assert.assertNotNull(sendMetrics);
    }
}
