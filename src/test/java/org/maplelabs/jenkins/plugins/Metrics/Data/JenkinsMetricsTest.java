package org.maplelabs.jenkins.plugins.Metrics.Data;

import java.util.HashMap;
import jenkins.model.Jenkins;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;



public class JenkinsMetricsTest {

	@Rule
	public JenkinsRule jenkinsRule = new JenkinsRule();

	@Test
	public void testCollectMetrics(){
	Jenkins jenkins = jenkinsRule.getInstance();
	JenkinsMetricsImpl jenkinsMetricsImpl = new JenkinsMetricsImpl();
	HashMap<String, Object> jenkinsMetrics = jenkinsMetricsImpl.collectMetrics(jenkins);
	Assert.assertTrue(jenkinsMetrics.get("hostName") != null);
	Assert.assertTrue(jenkinsMetrics.get("project_total") != null);
	Assert.assertTrue(jenkinsMetrics.get("plugin_total") != null);
	Assert.assertTrue(jenkinsMetrics.get("plugins_active") != null);
	Assert.assertTrue(jenkinsMetrics.get("plugins_failed") != null);
	Assert.assertTrue(jenkinsMetrics.get("plugins_inactive") != null);
	Assert.assertTrue(jenkinsMetrics.get("plugins_updatable") != null);
	}



}

