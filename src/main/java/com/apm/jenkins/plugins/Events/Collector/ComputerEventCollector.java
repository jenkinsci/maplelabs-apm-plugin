package com.apm.jenkins.plugins.Events.Collector;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Events.DataModel.AbstractEvent;
import com.apm.jenkins.plugins.Events.interfaces.ComputerEvent;

import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;

public class ComputerEventCollector extends AbstractEvent implements ComputerEvent {

    /**
     * This function will called when temp offline node is backs online
     * @param computer
     * @return true if request processed
     */
    @Override
    public boolean collectEvent(Computer computer) {
        String nodeName = APMUtil.getNodeName(computer);
        String title = "Jenkins node " + nodeName + "back online";
        setText(title);
        setTitle(title);
        setPriority(Priority.LOW);
        setAlert(AlertType.SUCCESS);
        return sendEvent();
    }

    /**
     * This function will called when node is offline/ temp offline
     * @param computer
     * @param cause
     * @return true if request processed
     */
    @Override
    public boolean collectEvent(Computer computer, OfflineCause cause, Type type) {
        String nodeName = APMUtil.getNodeName(computer);
        String title = "Jenkins node " + nodeName + " is ";
        switch (type) {
            case TEMPORARILYOFFLINE:
                title+="temporarily offline";
                setText(title);
                setTitle(title);
                setPriority(Priority.NORMAL);
                setAlert(AlertType.WARNING);
                break;
            case OFFLINE:
                title+=" offline";
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
        return sendEvent();
    }

    /**
     * This function will called when node is failed to launch
     * @param computer
     * @return true if request processed
     */
    @Override
    public boolean collectEvent(Computer computer, TaskListener taskListener) {
        String nodeName = APMUtil.getNodeName(computer);
        String title = "Jenkins node " + nodeName + " is" + " failed to launch";
        setTitle(title);
        setTitle(title);
        setPriority(Priority.HIGH);
        setAlert(AlertType.ERROR);
        return sendEvent();
    }

}
