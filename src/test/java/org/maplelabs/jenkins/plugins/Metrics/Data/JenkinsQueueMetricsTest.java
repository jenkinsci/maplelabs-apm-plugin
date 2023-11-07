package org.maplelabs.jenkins.plugins.Metrics.Data;

import java.util.HashMap;
import jenkins.model.Jenkins;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;



public class JenkinsQueueMetricsTest {
    @Rule
	public JenkinsRule jenkinsRule = new JenkinsRule();

	@Test
	public void testCollectMetrics(){
	Jenkins jenkins = jenkinsRule.getInstance();
	QueueMetricsImpl jenkinsQueueMetricsImpl = new QueueMetricsImpl();
	HashMap<String, Object> jenkinsQueueMetrics = jenkinsQueueMetricsImpl.collectMetrics(jenkins);
	Assert.assertTrue(jenkinsQueueMetrics.get("queue_stuck") != null);
	Assert.assertTrue(jenkinsQueueMetrics.get("queue_size") != null);
	Assert.assertTrue(jenkinsQueueMetrics.get("queue_pending") != null);
	Assert.assertTrue(jenkinsQueueMetrics.get("queue_blocked") != null);
	Assert.assertTrue(jenkinsQueueMetrics.get("jobs_aborted") != null);
	Assert.assertTrue(jenkinsQueueMetrics.get("jobs_started") != null);
	Assert.assertTrue(jenkinsQueueMetrics.get("queue_buildable") != null);
	Assert.assertTrue(jenkinsQueueMetrics.get("jobs_completed") != null);
	}



}
