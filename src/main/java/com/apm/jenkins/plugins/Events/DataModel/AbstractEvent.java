package com.apm.jenkins.plugins.Events.DataModel;

import java.util.HashMap;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.Snappyflow.SnappyFlow;
import com.apm.jenkins.plugins.Events.interfaces.Event;

public abstract class AbstractEvent implements Event {

    private Long date;
    private String host;
    private String text;
    private String title;
    private String event;
    private AlertType alert;
    private String nodeName;
    private Priority priority;

    public AbstractEvent() {
        setHost(APMUtil.getHostname(null));
        setDate(APMUtil.currentTimeMillis() / 1000);
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AlertType getAlert() {
        return this.alert;
    }

    public void setAlert(AlertType alert) {
        this.alert = alert;
    }
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setEventType(String event) {
        this.event = event;
    }

    @Override
    public Long getDate() {
        return date;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getTitle() {
       return title;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public AlertType getAlertType() {
        return alert;
    }

    @Override
    public String getEventType() {
        return event;
    }

    /**
     * This function will assembel details and call client
     * @return
     */
    protected boolean sendEvent(){
        HashMap<String, Object> payload = SnappyFlow.getSnappyflowTags(getEventType());
        payload.put("text", getText());
        payload.put("host", getHost());
        payload.put("title", getTitle());
        payload.put("date_happened", getDate());            
        payload.put("priority", getPriority().name().toLowerCase());
        payload.put("alert_type", getAlertType().name().toLowerCase());
        return APMUtil.getAPMGlobalDescriptor().getCommunicationClient().transmit(payload);
    }    
}
