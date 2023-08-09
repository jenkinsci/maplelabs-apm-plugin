package com.apm.jenkins.plugins.events;

import java.util.Set;
import java.util.HashMap;

import hudson.model.Computer;
import hudson.slaves.OfflineCause;

import com.apm.jenkins.plugins.APMUtil;

public class ComputerOnlineEvent extends AbstractAPMSimpleEvent {

    public ComputerOnlineEvent(Computer computer, OfflineCause cause, HashMap<String, Set<String>> tags, boolean isTemporarily) {
        
        String nodeName = APMUtil.getNodeName(computer);
        
        String title = "Jenkins node " + nodeName + " is" + (isTemporarily? " temporarily ": " ") + "online";
        setTitle(title);

        String text = "\nJenkins node " + nodeName + " is" + (isTemporarily? " temporarily ": " ") + "online." +
                "\n";
        setText(text);

        setPriority(Priority.LOW);
        setAlertType(AlertType.SUCCESS);
    }
}