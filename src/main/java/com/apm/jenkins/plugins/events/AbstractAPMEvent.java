package com.apm.jenkins.plugins.events;

import com.apm.jenkins.plugins.interfaces.Events.APMEvent;

public abstract class AbstractAPMEvent implements APMEvent {
	
    private Long date;
    private String text;
    private String host;
	private String title;
    private String nodeName;
    private String jenkinsUrl;
    private APMEvent.Priority priority;
    private APMEvent.AlertType alertType;

	@Override
	public String getTitle() {		
		return title;
	}
	
	public void setTitle(String title) {
        this.title = title;
    }

	@Override
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
        this.text = text;
    }
	
	@Override
	public String getNodeName() {
		return nodeName;
	}
	
	public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

	// need
	@Override
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
        this.host = host;
    }

	// @Override
	// public String getJenkinsUrl() {
	// 	return jenkinsUrl;
	// }
	// need
	// public void setJenkinsUrl(String jenkinsUrl) {
	// 	this.jenkinsUrl = jenkinsUrl;
	// }

	@Override
	public Priority getPriority() {
		return priority;
	}
	
	public void setPriority(APMEvent.Priority priority) {
        this.priority = priority;
    }

	@Override
	public APMEvent.AlertType getAlertType() {
		return alertType;
	}
	
	public void setAlertType(APMEvent.AlertType alertType) {
        this.alertType = alertType;
    }

	@Override
	public Long getDate() {
		return date;
	}
	
	public void setDate(Long date) {
		this.date = date;
	}
}
