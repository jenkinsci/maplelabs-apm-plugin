package com.apm.jenkins.plugins.Events.Collector;

import com.apm.jenkins.plugins.Events.Data.AbstractEvent;
import com.apm.jenkins.plugins.Events.interfaces.IComputerEvent;

import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;

public class ComputerEventCollectorImpl extends AbstractEvent implements IComputerEvent {

    /**
     * This function will called when temp offline node is backs online
     * 
     * @param computer
     * @return true if request processed
     */
    @Override
    public void collectEventData(Computer computer) {
        setEventType(EVENT);
        String nodeName = getNodeName(computer);
        String title = "Jenkins node " + nodeName + " back online";
        setText(title);
        setTitle(title);
        setPriority(Priority.LOW);
        setAlert(AlertType.SUCCESS);
    }

    /**
     * This function will called when node is offline/ temp offline
     * 
     * @param computer
     * @param cause
     * @return true if request processed
     */
    @Override
    public void collectEventData(Computer computer, OfflineCause cause, Type type) {
        setEventType(EVENT);
        String nodeName = getNodeName(computer);
        String title = "Jenkins node " + nodeName + " is ";
        switch (type) {
            case TEMPORARILYOFFLINE:
                title += "temporarily offline";
                setText(title);
                setTitle(title);
                setPriority(Priority.NORMAL);
                setAlert(AlertType.WARNING);
                break;
            case OFFLINE:
                title += " offline";
                setText(title);
                setTitle(title);
                setPriority(Priority.NORMAL);
                setAlert(AlertType.WARNING);
            default:
                title = "Jenkins node " + nodeName + " had some actions";
                setText(title);
                setTitle(title);
                setPriority(Priority.HIGH);
                setAlert(AlertType.WARNING);
        }
    }

    /**
     * This function will called when node is failed to launch
     * 
     * @param computer
     * @return true if request processed
     */
    @Override
    public void collectEventData(Computer computer, TaskListener taskListener) {
        setEventType(EVENT);
        String nodeName = getNodeName(computer);
        String title = "Jenkins node " + nodeName + " is" + " failed to launch";
        setTitle(title);
        setTitle(title);
        setPriority(Priority.HIGH);
        setAlert(AlertType.ERROR);
    }

}
