package com.apm.jenkins.plugins.events;
import java.util.HashMap;

import com.apm.jenkins.plugins.interfaces.APMEvent;

public abstract class AbstractAPMEvent implements APMEvent {
	
    private Long date;
    private String text;
    private String host;
	private String title;
    private String nodeName;
    private String jenkinsUrl;
    private APMEvent.Priority priority;
    private APMEvent.AlertType alertType;
    private HashMap<String, Object> sfTags;

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

	@Override
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
        this.host = host;
    }

	@Override
	public String getJenkinsUrl() {
		return jenkinsUrl;
	}
	
	public void setJenkinsUrl(String jenkinsUrl) {
		this.jenkinsUrl = jenkinsUrl;
	}

	@Override
	public Priority getPriority() {
		return priority;
	}
	
	public void setPriority(APMEvent.Priority priority) {
        this.priority = priority;
    }
	
	@Override
	public HashMap<String, Object> getSnappyflowTags() {
		return sfTags;
	}
	
	public void setSnappyflowTags(HashMap<String, Object> tags) {
		this.sfTags = tags;
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
	
	protected String getLocationDetails(){
		String hostMsg = "Host: unknown";
		String instanceMsg = "Jenkins URL: unknown";
		if(host != null && !host.isEmpty() && !"unknown".equals(host)){
			hostMsg = "Host: " + host;
		}
		if(jenkinsUrl != null && !jenkinsUrl.isEmpty() && !"unknown".equals(jenkinsUrl)){
			instanceMsg = "Jenkins URL: [instance](" + jenkinsUrl + ")";
		}
		return hostMsg + ", " + instanceMsg;
	}

}
