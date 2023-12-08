package io.jenkins.plugins.maplelabs.Metrics.Data;

import java.util.HashMap;
import jenkins.model.Jenkins;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;



public class JenkinsNodeMetricsTest {
    @Rule
	public JenkinsRule jenkinsRule = new JenkinsRule();

	@Test
	public void testCollectMetrics(){
	Jenkins jenkins = jenkinsRule.getInstance();
	NodeMetricsImpl jenkinsNodeMetricsImpl = new NodeMetricsImpl();
	HashMap<String, Object> jenkinsNodeMetrics = jenkinsNodeMetricsImpl.collectMetrics(jenkins.getComputers());
    System.out.println(jenkinsNodeMetrics);
	Assert.assertTrue(jenkinsNodeMetrics.get("nodes_total") != null);
	Assert.assertTrue(jenkinsNodeMetrics.get("computers") != null);
	Assert.assertTrue(jenkinsNodeMetrics.get("nodes_online") != null);
	Assert.assertTrue(jenkinsNodeMetrics.get("nodes_offline") != null);
	}



}
