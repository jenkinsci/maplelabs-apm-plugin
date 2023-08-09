package com.apm.jenkins.plugins.interfaces.Events;

import hudson.model.Run;
import hudson.model.TaskListener;

public interface BuildEvent extends APMEvent {
    final float MINUTE = 60;
    final float HOUR = 3600;
    public static enum Type{
        STARTED,
        COMPLETED
    }
    public boolean collectEvent(Run run, TaskListener listener, Type type);

    static String getFormattedDuration(Long duration) {
        if (duration != null) {
            String output = "(";
            String format = "%.2f";
            double d = duration.doubleValue() / 1000;
            if (d < MINUTE) {
                output = output + String.format(format, d) + " secs)";
            } else if (MINUTE <= d && d < HOUR) {
                output = output + String.format(format, d / MINUTE) + " mins)";
            } else if (HOUR <= d) {
                output = output + String.format(format, d / HOUR) + " hrs)";
            }
            return output;
        } else {
            return "";
        }
    }
}
