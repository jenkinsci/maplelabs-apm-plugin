package com.apm.jenkins.plugins.Events.Collector;

import com.apm.jenkins.plugins.Events.interfaces.Event;

public abstract class AbstractAPMEvent implements Event {
	
    private Long date;
    private String text;
    private String host;
	private String title;
    private String nodeName;
    private Priority priority;
    private AlertType alertType;

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

	@Override
	public Priority getPriority() {
		return priority;
	}
	
	public void setPriority(Priority priority) {
        this.priority = priority;
    }

	@Override
	public AlertType getAlertType() {
		return alertType;
	}
	
	public void setAlertType(AlertType alertType) {
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
