package com.apm.jenkins.plugins;

import net.sf.json.JSONObject;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerRequest;

import com.apm.jenkins.plugins.Client.Client;
import com.apm.jenkins.plugins.Client.Snappyflow.SnappyFlowEs;
import com.apm.jenkins.plugins.Client.Snappyflow.SnappyFlowKafka;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;

@Extension
public class APMGlobalConfiguration extends GlobalConfiguration {

	private Client destinationClient;

	private static final String OTHERS = "Others";
	private static final String ES = "SnappyflowES";
	private static final String KAFKA = "SnappyflowKafka";
	private static final String SNAPPYFLOW = "Snappyflow";
	private static final String DISPLAY_NAME = "Maplelabs APM Plugin";
	private static final Logger logger = Logger.getLogger(APMGlobalConfiguration.class.getName());

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

	// enable/diable
	private boolean isEventEnabled = true;
	private boolean isMetricEnabled = true;

	@DataBoundConstructor
	public APMGlobalConfiguration() {
		load(); // Load the persisted global configuration
		init();
	}

	public final void init() {
        switch (targetDestination) {
			case SNAPPYFLOW:
				switch (targetSnappyFlowDestination) {
					case ES:
						destinationClient = new SnappyFlowEs();
						break;
					case KAFKA:
						destinationClient = new SnappyFlowKafka();
						break;
					default:
				}
				break;
			case OTHERS:
				break;
			default:
		}
    }

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
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

	public String getTargetHost() {
		return targetHost;
	}

	public void setTargetPort(String port) {
		this.targetPort = port;
	}

	public String getTargetPort() {
		return targetPort;
	}

	public void setTargetProtocol(String protocol) {
		this.targetProtocol = protocol;
	}

	public String getTargetProtocol() {
		return targetProtocol;
	}

	public void setTargetESUserName(String userName) {
		this.targetESUserName = userName;
	}

	public String getTargetESUserName() {
		return targetESUserName;
	}

	public void setTargetESPassword(String password) {
		this.targetESPassword = password;
	}

	public String getTargetESPassword() {
		return targetESPassword;
	}

	public void setTargetProfileName(String profileName) {
		this.targetProfileName = profileName;
	}

	public String getTargetProfileName() {
		return targetProfileName;
	}

	public void setTargetKafkaPath(String path) {
		this.targetKafkaPath = path;
	}

	public String getTargetKafkaPath() {
		return targetKafkaPath;
	}

	public void setTargetKafkaToken(String token) {
		this.targetKafkaToken = token;
	}

	public String getTargetKafkaToken() {
		return targetKafkaToken;
	}

	public void setTargetKafkaTopic(String topic) {
		this.targetKafkaTopic = topic;
	}

	public String getTargetKafkaTopic() {
		return targetKafkaTopic;
	}

	public void setTargetPassword(String password) {
		this.targetPassword = password;
	}

	public String getTargetPassword() {
		return targetPassword;
	}

	public void setTargetUserName(String userName) {
		this.targetUserName = userName;
	}

	public String getTargetUserName() {
		return targetUserName;
	}

	public boolean getIsEventEnabled() {
		return this.isEventEnabled;
	}

	public void setIsEventEnabled(boolean isEventEnabled) {
		this.isEventEnabled = isEventEnabled;
	}

	public boolean getIsMetricEnabled() {
		return this.isMetricEnabled;
	}

	public void setIsMetricEnabled(boolean isMetricsEnabled) {
		this.isMetricEnabled = isMetricsEnabled;
	}

	/**
	 * This function will set config for ES
	 * 
	 * @param username
	 * @param password
	 */
	private void setESDetails(String username, String password) {
		setTargetESUserName(username);
		setTargetESPassword(password);
	}

	/**
	 * This function will set kafka details
	 * 
	 * @param path
	 * @param token
	 * @param topic
	 */
	private void setKafkaDetails(String path, String token, String topic) {
		setTargetKafkaPath(path);
		setTargetKafkaToken(token);
		setTargetKafkaTopic(topic);
	}

	/**
	 * This function will set others config details
	 * 
	 * @param username
	 * @param password
	 */
	private void setOthersDetail(String username, String password) {
		setTargetUserName(username);
		setTargetPassword(password);
	}

	// This function will set snappy config as null
	private void setSnappyConfigNull(boolean isCommonReset) {
		if (isCommonReset) {
			setTargetAppName(null);
			setTargetProfileName(null);
			setTargetProjectName(null);
			setTargetInstanceName(null);
		}
		setESDetails(null, null);
		setKafkaDetails(null, null, null);
	}

	public Client getDestinationClient() {
		return this.destinationClient;
	}

	/**
	 * This function will invoke when user click apply/save in config page
	 * This function will store the snappyflow config details
	 */
	@Override
	public boolean configure(final StaplerRequest req, final JSONObject formData) {
		try {
			if (!super.configure(req, formData)) {
				return false;
			}
		} catch (FormException e) {
			e.printStackTrace();
		}

		destinationClient = null;
		setIsEventEnabled(formData.getBoolean("isEventEnabled"));
		setIsMetricEnabled(formData.getBoolean("isMetricEnabled"));
		
		setTargetPort(formData.getString("targetPort"));
		setTargetHost(formData.getString("targetHost"));
		setTargetProtocol(formData.getString("targetProtocol"));
		if (formData.containsKey("targetDestination")) {
			setTargetDestination(formData.getString("targetDestination"));
			switch (getTargetDestination()) {
				case SNAPPYFLOW:
					setTargetAppName(formData.getString("targetAppName"));
					setTargetProfileName(formData.getString("targetProfileName"));
					setTargetProjectName(formData.getString("targetProjectName"));
					setTargetInstanceName(formData.getString("targetInstanceName"));
					if (formData.containsKey("targetSnappyFlowDestination")) {
						setTargetSnappyFlowDestination(formData.getString("targetSnappyFlowDestination"));
						switch (getTargetSnappyFlowDestination()) {
							case ES:
								String pass = formData.getString("targetESPassword");
								setESDetails(
										formData.getString("targetESUserName"),
										(pass.length() == 0 ? null : pass));
								destinationClient = new SnappyFlowEs();
								setOthersDetail(null, null);
								setKafkaDetails(null, null, null);
								break;
							case KAFKA:
								String token = formData.getString("targetKafkaToken");
								setKafkaDetails(
										formData.getString("targetKafkaPath"),
										token.length() == 0 ? null : token,
										formData.getString("targetKafkaTopic"));
								destinationClient = new SnappyFlowKafka();
								setOthersDetail(null, null);
								setESDetails(null, null);
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
