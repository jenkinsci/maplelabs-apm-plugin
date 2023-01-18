package com.snappyflow.jenkins.plugins;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;

import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;

@Extension
public class SnappyflowGlobalConfiguration extends GlobalConfiguration {

	private static final Logger logger = Logger.getLogger(SnappyflowGlobalConfiguration.class.getName());
	private static final String DISPLAY_NAME = "Snappyflow Plugin";
	
	
	private Secret targetApiKey = null;
	private String targetApiUrl = null;
	private String metricsReceiverUrl = null;
	
	@DataBoundConstructor
	public SnappyflowGlobalConfiguration() {
		load();
	}
	
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }
    
    @RequirePOST
    public FormValidation doTestConnection(
            @QueryParameter("targetApiKey") final String targetApiKey,
            @QueryParameter("targetApiUrl") final String targetApiUrl)
            throws IOException, ServletException {

    	// TODO Write logic here to send request to Snappyflow API server and validate the API Key
    	// For testing, added null checks
        if (StringUtils.isNotBlank(targetApiKey) && StringUtils.isNotBlank(targetApiUrl)) {
            return FormValidation.ok("API key is valid!");
        } else {
            return FormValidation.error("API key seems to be invalid. Check API URL/Key fields.");
        }
    }
	
	public Secret getTargetApiKey() {
		return targetApiKey;
	}

	public void setTargetApiKey(Secret targetApiKey) {
		this.targetApiKey = targetApiKey;
	}

	public String getTargetApiUrl() {
		return targetApiUrl;
	}

	public void setTargetApiUrl(String targetApiUrl) {
		this.targetApiUrl = targetApiUrl;
	}

	public String getMetricsReceiverUrl() {
		return metricsReceiverUrl;
	}

	public void setMetricsReceiverUrl(String metricsReceiverUrl) {
		this.metricsReceiverUrl = metricsReceiverUrl;
	}

}
