package com.apm.jenkins.plugins.events;

import java.util.Map;
import java.util.Set;

import hudson.model.Computer;
import hudson.model.TaskListener;

import com.apm.jenkins.plugins.APMUtil;

public class ComputerLaunchFailedEvent  extends AbstractAPMSimpleEvent {

    public ComputerLaunchFailedEvent(Computer computer, TaskListener listener, Map<String, Set<String>> tags) {
        
        String nodeName = APMUtil.getNodeName(computer);
        
        String title = "Jenkins node " + nodeName + " is" +  " failed to launch";
        setTitle(title);

        String text = "Jenkins node " + nodeName + " is" +  " failed to launch" +
                "\n";
        setText(text);

        setPriority(Priority.NORMAL);
        setAlertType(AlertType.WARNING);    
    }
}