package com.apm.jenkins.plugins.Events.interfaces;

import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;

public interface ComputerEvent extends Event {
    public String EVENT="SystemEvent";
    public static enum Type{
        OFFLINE,
        LAUNCHFAILURE,
        TEMPORARILYONLINE,
        TEMPORARILYOFFLINE,
    }
    public boolean CollectEventData(Computer computer);
    public boolean CollectEventData(Computer computer, TaskListener taskListener);
    public boolean CollectEventData(Computer computer, OfflineCause cause, Type type);
}
