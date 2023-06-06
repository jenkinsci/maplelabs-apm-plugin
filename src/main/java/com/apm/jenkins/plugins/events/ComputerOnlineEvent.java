package com.apm.jenkins.plugins.events;

import hudson.model.Computer;
import hudson.slaves.OfflineCause;
import com.apm.jenkins.plugins.APMUtil;

import java.util.HashMap;


public class ComputerOnlineEvent extends AbstractAPMSimpleEvent {

    public ComputerOnlineEvent(Computer computer, OfflineCause cause, HashMap<String, Object> tags, boolean isTemporarily) {
        
        String nodeName = APMUtil.getNodeName(computer);
        
        String title = "Jenkins node " + nodeName + " is" + (isTemporarily? " temporarily ": " ") + "online";
        setTitle(title);

        // TODO: Add more info about the case in the event in message.
        String text = "%%% \nJenkins node " + nodeName + " is" + (isTemporarily? " temporarily ": " ") + "online." +
                "\n" + super.getLocationDetails() + " \n%%%";
        setText(text);

        setPriority(Priority.LOW);
        setAlertType(AlertType.SUCCESS);
        
        //Snappyflow Specific
        setSnappyflowTags(tags);
    }
}