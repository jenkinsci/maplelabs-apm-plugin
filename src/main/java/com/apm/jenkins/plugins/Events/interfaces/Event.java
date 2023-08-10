package com.apm.jenkins.plugins.Events.interfaces;

public interface Event {

	public static final String NODE_EVENT_TYPE = "node";
	public static final String BUILD_EVENT_TYPE = "build";
	public static final String SECURITY_EVENT_TYPE = "security";

	public static enum AlertType {
		ERROR,
		WARNING,
		INFO,
		SUCCESS;		
	}

	public static enum Priority {
		LOW,
		NORMAL,
		HIGH;
	}

	public Long getDate();
	public String getText();
	public String getHost();
	public String getTitle();
	public String getNodeName();
	public Priority getPriority();
	public AlertType getAlertType();
}

