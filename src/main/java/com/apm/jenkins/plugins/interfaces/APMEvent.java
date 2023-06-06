package com.apm.jenkins.plugins.interfaces;

import java.util.HashMap;

/**
 * Interface for APM events.
 */
public interface APMEvent {

	public static final String DEFAULT_EVENT_TYPE = "default";
	public static final String SECURITY_EVENT_TYPE = "security";
	public static final String SYSTEM_EVENT_TYPE = "system";

	public static enum AlertType {
		ERROR,
		WARNING,
		INFO,
		SUCCESS;

		private AlertType() {
		}		
	}

	public static enum Priority {
		LOW,
		NORMAL;

		private Priority() {
		}		
	}	

	public String getTitle();

	public String getText();

	public String getHost();

	public String getJenkinsUrl();

	public Priority getPriority();

	public AlertType getAlertType();
	
	public HashMap<String, Object> getSnappyflowTags();

	public String getNodeName();

	public Long getDate();

	// public Map<String, Set<String>> getTags();

}

