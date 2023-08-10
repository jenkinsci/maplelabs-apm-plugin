package com.apm.jenkins.plugins.Events.interfaces;

import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;

public interface ComputerEvent extends Event {
    public static enum Type{
        OFFLINE,
        LAUNCHFAILURE,
        TEMPORARILYONLINE,
        TEMPORARILYOFFLINE,
    }
    public boolean collectEvent(Computer computer);
    public boolean collectEvent(Computer computer, TaskListener taskListener);
    public boolean collectEvent(Computer computer, OfflineCause cause, Type type);
}
