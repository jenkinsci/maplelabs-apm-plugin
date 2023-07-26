package com.apm.jenkins.plugins;

import java.io.IOException;
import java.util.logging.Logger;

import net.sf.json.JSONObject;
import javax.servlet.ServletException;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.interceptor.RequirePOST;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;

@Extension
public class APMGlobalConfiguration extends GlobalConfiguration {
	
	private static final String DISPLAY_NAME = "APM Plugin";
    private static final String DEFAULT_TARGET_DESTINATION_VALUE = "Snappyflow";
    private static final String HOSTNAME_PROPERTY = "APM_JENKINS_PLUGIN_HOSTNAME";
	private static final Logger logger = Logger.getLogger(APMGlobalConfiguration.class.getName());	
		
	private String hostname = null;
	private String targetApiKey = null;
	private String targetAppName = null;
	private String targetProjectName = null;
	private String targetInstanceName = null;
	private String targetDestination = DEFAULT_TARGET_DESTINATION_VALUE;

    	
	@DataBoundConstructor
	public APMGlobalConfiguration() {
		load(); // Load the persisted global configuration
		loadEnvVariables(); // Load environment variables after as they should take precedence.
	}
	
	private void loadEnvVariables(){
		String hostnameEnvVar = System.getenv(HOSTNAME_PROPERTY);
		if(StringUtils.isNotBlank(hostnameEnvVar)){
			this.hostname = hostnameEnvVar;
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

	/**
	 * This function will invoke when user click apply/save in config page
	 * This function will store the snappyflow config details
	 */
	@Override
	public boolean configure(final StaplerRequest req, final JSONObject formData){
		try {
			if(!super.configure(req, formData)){
				return false;
			}
		} catch (FormException e) {
			e.printStackTrace();
		}
		setTargetApiKey((String)formData.get("targetApiKey"));
		setTargetAppName((String)formData.get("targetAppName"));
		setTargetProjectName((String)formData.get("targetProjectName"));
		setTargetInstanceName((String)formData.get("targetInstanceName"));
		// Persist global configuration information
		save();
		return true;
	}
}
