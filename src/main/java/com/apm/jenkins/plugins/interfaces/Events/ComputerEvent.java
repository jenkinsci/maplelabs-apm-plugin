package com.apm.jenkins.plugins.interfaces.Events;

import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;

public interface ComputerEvent extends APMEvent {
    public static enum Type{
        OFFLINE,
        TEMPORARILYOFFLINE,
        TEMPORARILYONLINE,
        LAUNCHFAILURE
    }
    public boolean collectEvent(Computer computer, Type type);
    public boolean collectEvent(Computer computer, OfflineCause cause, Type type);
    public boolean collectEvent(Computer computer, TaskListener taskListener, Type type);
}
