package com.apm.jenkins.plugins;

import net.sf.json.JSONObject;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerRequest;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;

@Extension
public class APMGlobalConfiguration extends GlobalConfiguration {

	private static final String OTHERS = "Others";
	private static final String ES = "SnappyflowES";
	private static final String KAFKA = "SnappyflowKafka";
	private static final String SNAPPYFLOW = "Snappyflow";
	private static final String DISPLAY_NAME = "Maplelabs APM Plugin";
	private static final Logger logger = Logger.getLogger(APMGlobalConfiguration.class.getName());	
	
	private String hostname = null;
	private String targetAppName = null;
	private String targetProjectName = null;
	private String targetInstanceName = null;
	private String targetDestination = SNAPPYFLOW;
	private String targetSnappyFlowDestination = ES;
	// common
	private String targetHost = null;
	private String targetPort = null;
	private String targetProtocol = null;
	// ES
	private String targetESUserName = null;
	private String targetESPassword = null;
	private String targetProfileName = null;

	// Kafka
	private String targetKafkaPath = null;
	private String targetKafkaToken = null;
	private String targetKafkaTopic = null;

	// others
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
        
	/**
     * Getter function for the hostname global configuration.
     *
     * @return a String containing the hostname global configuration. */
     
	 public String getHostname() {
        return hostname;
    }

	 public String getTargetSnappyFlowDestination() {
		return targetSnappyFlowDestination;
	}
    
	public void setTargetSnappyFlowDestination(String destination) {
		targetSnappyFlowDestination = destination;
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

	public void setTargetHost(String host) {
		this.targetHost = host;
	}

	public String getTargetHost(){
		return targetHost;
	}

	public void setTargetPort(String port) {
		this.targetPort = port;
	}

	public String getTargetPort(){
		return targetPort;
	}

	public void setTargetProtocol(String protocol) {
		this.targetProtocol = protocol;
	}

	public String getTargetProtocol(){
		return targetProtocol;
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
	 * This function will set config for ES
	 * @param username
	 * @param password
	 */
	private void setESDetails(String username, String password){
		setTargetESUserName(username);
		setTargetESPassword(password);
	}

	/**
	 * This function will set kafka details
	 * @param path
	 * @param token
	 * @param topic
	 */
	private void setKafkaDetails(String path, String token, String topic){
		setTargetKafkaPath(path);
		setTargetKafkaToken(token);
		setTargetKafkaTopic(topic);
	}

	/**
	 * This function will set others config details
	 * @param username
	 * @param password
	 */
	private void setOthersDetail(String username, String password){
		setTargetUserName(username);
		setTargetPassword(password);
	}

	// This function will set snappy config as null 
	private void setSnappyConfigNull(boolean isCommonReset) {
		if(isCommonReset) {
			setTargetAppName(null);
			setTargetProfileName(null);
			setTargetProjectName(null);
			setTargetInstanceName(null);
		}
		setESDetails(null,null);
		setKafkaDetails(null, null, null);
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
		setTargetPort(formData.getString("targetPort"));
		setTargetHost(formData.getString("targetHost"));
		setTargetProtocol(formData.getString("targetProtocol"));
		
		if(formData.containsKey("targetDestination")) {
			setTargetDestination(formData.getString("targetDestination"));
			switch(getTargetDestination()) {
				case SNAPPYFLOW:
					setTargetAppName(formData.getString("targetAppName"));
					setTargetProfileName(formData.getString("targetProfileName"));
					setTargetProjectName(formData.getString("targetProjectName"));
					setTargetInstanceName(formData.getString("targetInstanceName"));
					if(formData.containsKey("targetSnappyFlowDestination")) {
						setTargetSnappyFlowDestination(formData.getString("targetSnappyFlowDestination"));
						switch(getTargetSnappyFlowDestination()) {
							case ES:
								String pass = formData.getString("targetESPassword");
								setESDetails(
									formData.getString("targetESUserName"),
									(pass.length() == 0 ? null : pass)
								);
		
								setOthersDetail(null, null);
								setKafkaDetails(null, null, null);
							break;
							case KAFKA:
								String token = formData.getString("targetKafkaToken");
								setKafkaDetails(
									formData.getString("targetKafkaPath"),
									token.length() == 0 ? null : token,
									formData.getString("targetKafkaTopic")
								);
		
								setOthersDetail(null, null);
								setESDetails(null,null);
							break;
							default:
								setSnappyConfigNull(false);
								setOthersDetail(null, null);

						}
					} else {
						setSnappyConfigNull(false);
						setOthersDetail(null, null);
					}			
				break;
				case OTHERS:
					String pass = formData.getString("targetPassword");
					setTargetUserName(formData.getString("targetUserName"));
					setTargetPassword(pass.length() == 0 ? null : pass);
					setSnappyConfigNull(true);

				break;
				default:
					setSnappyConfigNull(true);
					setOthersDetail(null, null);
			}
		} else {
			setSnappyConfigNull(true);
			setOthersDetail(null, null);
		}
		// Persist global configuration information
		save();
		return true;
	}
}
