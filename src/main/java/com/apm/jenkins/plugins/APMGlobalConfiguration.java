package com.apm.jenkins.plugins;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;

import com.apm.jenkins.plugins.interfaces.APMClient;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;

@Extension
public class APMGlobalConfiguration extends GlobalConfiguration {
	
	private static final String DISPLAY_NAME = "APM Plugin";
	
	private static final String DEFAULT_REPORT_WITH_VALUE = APMClient.ClientType.HTTP.name();
    private static final String DEFAULT_TARGET_API_URL_VALUE = "https://api.snappyflow.com/api/";
    private static final String DEFAULT_TARGET_DESTINATION_VALUE = "Snappyflow";
    private static final String DEFAULT_TARGET_LOG_INTAKE_URL_VALUE = null;
    private static final String DEFAULT_TARGET_WEBHOOK_INTAKE_URL_VALUE = null;
    private static final String DEFAULT_TARGET_HOST_VALUE = "localhost";
    private static final Integer DEFAULT_TARGET_PORT_VALUE = 8125;
    private static final Integer DEFAULT_TRACE_COLLECTION_PORT_VALUE = 8126;
    private static final String DEFAULT_CI_INSTANCE_NAME = "jenkins";
    private static final Integer DEFAULT_TARGET_LOG_COLLECTION_PORT_VALUE = null;
    private static final boolean DEFAULT_EMIT_SECURITY_EVENTS_VALUE = true;
    private static final boolean DEFAULT_EMIT_SYSTEM_EVENTS_VALUE = true;
    
    private static final String REPORT_WITH_PROPERTY = "APM_JENKINS_PLUGIN_REPORT_WITH";
    private static final String HOSTNAME_PROPERTY = "APM_JENKINS_PLUGIN_HOSTNAME";
    private static final String GLOBAL_TAGS_PROPERTY = "APM_JENKINS_PLUGIN_GLOBAL_TAGS";
    private static final String EMIT_SECURITY_EVENTS_PROPERTY = "APM_JENKINS_PLUGIN_EMIT_SECURITY_EVENTS";
    private static final String EMIT_SYSTEM_EVENTS_PROPERTY = "APM_JENKINS_PLUGIN_EMIT_SYSTEM_EVENTS";
      
	private static final Logger logger = Logger.getLogger(APMGlobalConfiguration.class.getName());	
		
	private String targetApiKey = null;
	private String targetProjectName = null;
	private String targetAppName = null;
	private String targetInstanceName = null;
	private String hostname = null;
	private String globalTags = null;
	
	private String metricsReceiverUrl = null;
	private String reportWith = DEFAULT_REPORT_WITH_VALUE;
	private String targetApiURL = DEFAULT_TARGET_API_URL_VALUE;
	private String targetLogIntakeURL = DEFAULT_TARGET_LOG_INTAKE_URL_VALUE;
	private String targetWebhookIntakeURL = DEFAULT_TARGET_WEBHOOK_INTAKE_URL_VALUE;
	// private String targetCredentialsApiKey = null;
	private String usedApiKey = null;
	private String targetHost = DEFAULT_TARGET_HOST_VALUE;
    private Integer targetPort = DEFAULT_TARGET_PORT_VALUE;
    private Integer targetLogCollectionPort = DEFAULT_TARGET_LOG_COLLECTION_PORT_VALUE;
    private Integer targetTraceCollectionPort = DEFAULT_TRACE_COLLECTION_PORT_VALUE;
    private String traceServiceName = DEFAULT_CI_INSTANCE_NAME;
    private boolean emitSecurityEvents = DEFAULT_EMIT_SECURITY_EVENTS_VALUE;
    private boolean emitSystemEvents = DEFAULT_EMIT_SYSTEM_EVENTS_VALUE;
    private String targetDestination = DEFAULT_TARGET_DESTINATION_VALUE;
    	
	@DataBoundConstructor
	public APMGlobalConfiguration() {
		load(); // Load the persisted global configuration
		loadEnvVariables(); // Load environment variables after as they should take precedence.
	}
	
	private void loadEnvVariables(){
		String reportWithEnvVar = System.getenv(REPORT_WITH_PROPERTY);
		if(StringUtils.isNotBlank(reportWithEnvVar) &&
				(reportWithEnvVar.equals(APMClient.ClientType.HTTP.name()))){
			this.reportWith = reportWithEnvVar;
		}
		
		String hostnameEnvVar = System.getenv(HOSTNAME_PROPERTY);
		if(StringUtils.isNotBlank(hostnameEnvVar)){
			this.hostname = hostnameEnvVar;
		}

		String globalTagsEnvVar = System.getenv(GLOBAL_TAGS_PROPERTY);
		if(StringUtils.isNotBlank(globalTagsEnvVar)){
			this.globalTags = globalTagsEnvVar;
		}
		
		String emitSecurityEventsEnvVar = System.getenv(EMIT_SECURITY_EVENTS_PROPERTY);
		if(StringUtils.isNotBlank(emitSecurityEventsEnvVar)){
			this.emitSecurityEvents = Boolean.valueOf(emitSecurityEventsEnvVar);
		}
		
		String emitSystemEventsEnvVar = System.getenv(EMIT_SYSTEM_EVENTS_PROPERTY);
        if(StringUtils.isNotBlank(emitSystemEventsEnvVar)){
            this.emitSystemEvents = Boolean.valueOf(emitSystemEventsEnvVar);
        }
	}
	
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }
    
    /**
     * Getter function for the hostname global configuration.
     *
     * @return a String containing the hostname global configuration. */
     
    public String getHostname() {
        return hostname;
    }

    /**
     * Setter function for the hostname global configuration.
     *
     * @param hostname - A String containing the hostname of the Jenkins host. */
     
    @DataBoundSetter
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }
    
    /**
     * Getter function for the reportWith global configuration.
     *
     * @return a String containing the reportWith global configuration.
     */
    public String getReportWith() {
        return reportWith;
    }

    /**
     * Setter function for the reportWith global configuration.
     *
     * @param reportWith = A string containing the reportWith global configuration.
     */
    @DataBoundSetter
    public void setReportWith(String reportWith) {
        this.reportWith = reportWith;
    }
    
    /**
     * Getter function for the globalTags global configuration, containing
     * a comma-separated list of tags that should be applied everywhere.
     *
     * @return a String array containing the globalTags global configuration
     */
    public String getGlobalTags() {
        return globalTags;
    }

    /**
     * Setter function for the globalTags global configuration,
     * accepting a comma-separated string of tags.
     *
     * @param globalTags - a comma-separated list of tags.
     */
    @DataBoundSetter
    public void setGlobalTags(String globalTags) {
        this.globalTags = globalTags;
    }
        
    @RequirePOST
    public FormValidation doTestConnection(
            @QueryParameter("targetApiKey") final String targetApiKey,
            @QueryParameter("targetProjectName") final String targetProjectName,
            @QueryParameter("targetAppName") final String targetAppName,
            @QueryParameter("targetInstanceName") final String targetInstanceName)
            throws IOException, ServletException {

    	// TODO Write logic here to send request to APM API server and validate the API Key
    	// For testing, added null checks
    	if (StringUtils.isNotBlank(targetApiKey) && StringUtils.isNotBlank(targetProjectName) &&
    			StringUtils.isNotBlank(targetAppName) && StringUtils.isNotBlank(targetInstanceName)) {
    		logger.info("targetapikey" + targetApiKey);
    		return FormValidation.ok("API key is valid!");
    	} else {
    		return FormValidation.error("API key seems to be invalid. Check API Key/ project name/ app name/ instance name fields.");
    	}  	
    }
      	
    public String getTargetDestination() {
		return targetDestination;
	}

	public void setTargetDestination(String targetDestination) {
		this.targetDestination = targetDestination;
	}
    
	public String getTargetApiKey() {
		return targetApiKey;
	}

	public void setTargetApiKey(String targetApiKey) {
		this.targetApiKey = targetApiKey;
	}

	public String getTargetProjectName() {
		return targetProjectName;
	}

	public void setTargetProjectName(String targetProjectName) {
		this.targetProjectName = targetProjectName;		
	}
	
	public String getTargetAppName() {
		return targetAppName;
	}

	public void setTargetAppName(String targetAppName) {
		this.targetAppName = targetAppName;
	}
	
	public String getTargetInstanceName() {
		return targetInstanceName;
	}

	public void setTargetInstanceName(String targetInstanceName) {
		this.targetInstanceName = targetInstanceName;
	}
	
	public String getMetricsReceiverUrl() {
		return metricsReceiverUrl;
	}

	public void setMetricsReceiverUrl(String metricsReceiverUrl) {
		this.metricsReceiverUrl = metricsReceiverUrl;
	}
	
	 /**
     * @return - A {@link Boolean} indicating if the user has configured APM to emit Security related events.
     */
    public boolean isEmitSecurityEvents() {
        return emitSecurityEvents;
    }

    /**
     * Set the checkbox in the UI, used for Jenkins data binding
     *
     * @param emitSecurityEvents - The checkbox status (checked/unchecked)
     */
    @DataBoundSetter
    public void setEmitSecurityEvents(boolean emitSecurityEvents) {
        this.emitSecurityEvents = emitSecurityEvents;
    }
	
    /**
     * @return - A {@link Boolean} indicating if the user has configured Datadog to emit System related events.
     */
    public boolean isEmitSystemEvents() {
        return emitSystemEvents;
    }

    /**
     * Set the checkbox in the UI, used for Jenkins data binding
     *
     * @param emitSystemEvents - The checkbox status (checked/unchecked)
     */
    @DataBoundSetter
    public void setEmitSystemEvents(boolean emitSystemEvents) {
        this.emitSystemEvents = emitSystemEvents;
    }
    
	/**
     * Getter function for the targetApiURL global configuration.
     *
     * @return a String containing the targetApiURL global configuration.
     */
    public String getTargetApiURL() {
        return targetApiURL;
    }

    public String getTargetLogIntakeURL() {
        return targetLogIntakeURL;
    }
    
    public String getTargetWebhookIntakeURL() {
        return targetWebhookIntakeURL;
    }       
    
    public String getUsedApiKey() {
        return usedApiKey;
    }
    
    public String getTargetHost() {
        return targetHost;
    }
    
    public Integer getTargetPort() {
        return targetPort;
    }
    
    public Integer getTargetLogCollectionPort() {
        return targetLogCollectionPort;
    }
    
    public Integer getTargetTraceCollectionPort() {
        return targetTraceCollectionPort;
    }
    
    public String getCiInstanceName() {
        return this.traceServiceName;
    }  
}
