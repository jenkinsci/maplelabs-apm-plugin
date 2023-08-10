package com.apm.jenkins.plugins.Events.interfaces;

public interface Event {

	public static enum AlertType {
		INFO,
		ERROR,
		WARNING,
		SUCCESS;		
	}

	public static enum Priority {
		LOW,
		HIGH,
		NORMAL;
	}

	public Long getDate();
	public String getText();
	public String getHost();
	public String getTitle();
	public String getNodeName();
	public Priority getPriority();
	public AlertType getAlertType();
}

