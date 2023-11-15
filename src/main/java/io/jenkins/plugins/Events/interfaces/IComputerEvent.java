package io.jenkins.plugins.Events.interfaces;

import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;

public interface IComputerEvent extends IEvent {
    public String EVENT = "SystemEvent";

    public static enum Type {
        OFFLINE,
        LAUNCHFAILURE,
        TEMPORARILYONLINE,
        TEMPORARILYOFFLINE,
    }

    public void collectEventData(Computer computer);

    public void collectEventData(Computer computer, TaskListener taskListener);

    public void collectEventData(Computer computer, OfflineCause cause, Type type);
}
