package com.apm.jenkins.plugins;

import java.io.IOException;
import java.util.logging.Logger;

import net.sf.json.JSONObject;
import javax.servlet.ServletException;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.interceptor.RequirePOST;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;

@Extension
public class APMGlobalConfiguration extends GlobalConfiguration {

	private static final String ES = "SnappyflowES";
	private static final String KAFKA = "SnappyflowKafka";
	private static final String DISPLAY_NAME = "Maplelabs APM Plugin";
	private static final Logger logger = Logger.getLogger(APMGlobalConfiguration.class.getName());	
	
	private String hostname = null;
	private String targetAppName = null;
	private String targetDestination = ES;
	private String targetProjectName = null;
	private String targetInstanceName = null;

	// ES
	private String targetESHost = null;
	private String targetESPort = null;
	private String targetESProtocol = null;
	private String targetESUserName = null;
	private String targetESPassword = null;
	private String targetProfileName = null;

	// Kafka
	private String targetKafkaPath = null;
	private String targetKafkaToken = null;
	private String targetKafkaTopic = null;

	private String targetKafkaHost = null;
	private String targetKafkaPort = null;
	private String targetKafkaProtocol = null;

	private String targetUserName = null;
	private String targetPassword = null;

    	
	@DataBoundConstructor
	public APMGlobalConfiguration() {
		load(); // Load the persisted global configuration
		String hostnameEnvVar = System.getenv("APM_JENKINS_PLUGIN_HOSTNAME");
		if(StringUtils.isNotBlank(hostnameEnvVar)){
			this.hostname = hostnameEnvVar;
		}
	}
	
	
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }
        
    @RequirePOST
    public FormValidation doTestConnection(
            @QueryParameter("targetProjectName") final String targetProjectName,
            @QueryParameter("targetAppName") final String targetAppName,
            @QueryParameter("targetInstanceName") final String targetInstanceName)
            throws IOException, ServletException {

    	// TODO Write logic here to send request to APM API server and validate the API Key
    	// For testing, added null checks
    	if ( StringUtils.isNotBlank(targetProjectName) &&
    			StringUtils.isNotBlank(targetAppName) && StringUtils.isNotBlank(targetInstanceName)) {
    		return FormValidation.ok("API key is valid!");
    	} else {
    		return FormValidation.error("API key seems to be invalid. Check API Key/ project name/ app name/ instance name fields.");
    	}  	
    }

	/**
     * Getter function for the hostname global configuration.
     *
     * @return a String containing the hostname global configuration. */
     
	 public String getHostname() {
        return hostname;
    }
      	
    public String getTargetDestination() {
		return targetDestination;
	}
    
	public void setTargetDestination(String destination) {
		targetDestination = destination;
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

	public void setTargetESHost(String host) {
		this.targetESHost = host;
	}

	public String getTargetESHost(){
		return targetESHost;
	}

	public void setTargetESPort(String port) {
		this.targetESPort = port;
	}

	public String getTargetESPort(){
		return targetESPort;
	}

	public void setTargetESProtocol(String protocol) {
		this.targetESProtocol = protocol;
	}

	public String getTargetESProtocol(){
		return targetESProtocol;
	}

	public void setTargetESUserName(String userName) {
		this.targetESUserName = userName;
	}

	public String getTargetESUserName(){
		return targetESUserName;
	}

	public void setTargetESPassword(String password) {
		this.targetESPassword = password;
	}

	public String getTargetESPassword(){
		return targetESPassword;
	}

	public void setTargetProfileName(String profileName) {
		this.targetProfileName = profileName;
	}

	public String getTargetProfileName(){
		return targetProfileName;
	}

	public void setTargetKafkaPath(String path) {
		this.targetKafkaPath = path;
	}

	public String getTargetKafkaPath(){
		return targetKafkaPath;
	}

	public void setTargetKafkaToken(String token) {
		this.targetKafkaToken = token;
	}

	public String getTargetKafkaToken(){
		return targetKafkaToken;
	}

	public void setTargetKafkaTopic(String topic) {
		this.targetKafkaTopic = topic;
	}

	public String getTargetKafkaTopic(){
		return targetKafkaTopic;
	}

	public void setTargetKafkaHost(String host) {
		this.targetKafkaHost = host;
	}

	public String getTargetKafkaHost(){
		return targetKafkaHost;
	}

	public void setTargetKafkaPort(String port) {
		this.targetKafkaPort = port;
	}

	public String getTargetKafkaPort(){
		return targetKafkaPort;
	}

	public void setTargetKafkaProtocol(String protocol) {
		this.targetKafkaProtocol = protocol;
	}

	public String getTargetKafkaProtocol(){
		return targetKafkaProtocol;
	}

	public void setTargetPassword(String password) {
		this.targetPassword = password;
	}

	public String getTargetPassword(){
		return targetPassword;
	}

	public void setTargetUserName(String userName) {
		this.targetUserName = userName;
	}

	public String getTargetUserName(){
		return targetUserName;
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
		setTargetAppName(formData.getString("targetAppName"));
		setTargetProjectName(formData.getString("targetProjectName"));
		setTargetInstanceName(formData.getString("targetInstanceName"));
		if(formData.containsKey("targetDestination")) {
				setTargetDestination(formData.getString("targetDestination"));
				switch(getTargetDestination()) {
					case ES:
						setTargetESPort(formData.getString("targetESPort"));
						setTargetESHost(formData.getString("targetESHost"));
						setTargetESProtocol(formData.getString("targetESProtocol"));
						setTargetESUserName(formData.getString("targetESUserName"));
						setTargetESPassword(formData.getString("targetESPassword"));
						setTargetProfileName(formData.getString("targetProfileName"));

						setTargetKafkaPort(null);
						setTargetKafkaHost(null);
						setTargetKafkaPath(null);
						setTargetKafkaToken(null);
						setTargetKafkaTopic(null);
						setTargetKafkaProtocol(null);

						setTargetUserName(null);
						setTargetPassword(null);
					 break;
					case KAFKA:
						setTargetKafkaPort(formData.getString("targetKafkaPort"));
						setTargetKafkaHost(formData.getString("targetKafkaHost"));
						setTargetKafkaPath(formData.getString("targetKafkaPath"));
						setTargetKafkaToken(formData.getString("targetKafkaToken"));
						setTargetKafkaTopic(formData.getString("targetKafkaTopic"));
						setTargetKafkaProtocol(formData.getString("targetKafkaProtocol"));

						setTargetESPort(null);
						setTargetESHost(null);
						setTargetESProtocol(null);
						setTargetESUserName(null);
						setTargetESPassword(null);
						setTargetProfileName(null);

						setTargetUserName(null);
						setTargetPassword(null);
					 break;
					case "Other":
						setTargetUserName(formData.getString("targetUserName"));
						setTargetPassword(formData.getString("targetPassword"));

						setTargetKafkaPort(null);
						setTargetKafkaHost(null);
						setTargetKafkaPath(null);
						setTargetKafkaToken(null);
						setTargetKafkaTopic(null);
						setTargetKafkaProtocol(null);

						setTargetESPort(null);
						setTargetESHost(null);
						setTargetESProtocol(null);
						setTargetESUserName(null);
						setTargetESPassword(null);
						setTargetProfileName(null);
					break;
					default:
						setTargetKafkaPort(null);
						setTargetKafkaHost(null);
						setTargetKafkaPath(null);
						setTargetKafkaToken(null);
						setTargetKafkaTopic(null);
						setTargetKafkaProtocol(null);

						setTargetESPort(null);
						setTargetESHost(null);
						setTargetESProtocol(null);
						setTargetESUserName(null);
						setTargetESPassword(null);
						setTargetProfileName(null);

						setTargetUserName(null);
						setTargetPassword(null);
				}
		}
		// Persist global configuration information
		save();
		return true;
	}
}
