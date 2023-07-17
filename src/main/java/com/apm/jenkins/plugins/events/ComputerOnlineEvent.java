package com.apm.jenkins.plugins.events;

import java.util.Set;
import java.util.HashMap;
import java.util.Map.Entry;

import hudson.model.Computer;
import hudson.slaves.OfflineCause;

import com.apm.jenkins.plugins.APMUtil;

public class ComputerOnlineEvent extends AbstractAPMSimpleEvent {

    public ComputerOnlineEvent(Computer computer, OfflineCause cause, HashMap<String, Set<String>> tags, boolean isTemporarily) {
        
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
        HashMap<String, Object> snappyTag = new HashMap();
        for ( Entry<String, Set<String>> element : tags.entrySet()) {
           snappyTag.put(element.getKey(),  element.getValue()) ;
        }
        setSnappyflowTags(snappyTag);
    }
}