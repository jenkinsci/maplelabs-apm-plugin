package com.apm.jenkins.plugins.events;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.DataModel.*;


public abstract class AbstractAPMBuildEvent extends AbstractAPMEvent {

    protected BuildData buildData;

    private static final float MINUTE = 60;
    private static final float HOUR = 3600;

    public AbstractAPMBuildEvent(BuildData buildData) {
        this.buildData = buildData;
        setHost(buildData.getHostname("unknown"));
        setJenkinsUrl(buildData.getJenkinsUrl("unknown"));
        // setAggregationKey(buildData.getJobName("unknown"));
        setDate(buildData.getEndTime(APMUtil.currentTimeMillis()) / 1000);
        setSnappyflowTags(APMUtil.getSnappyflowTags("BuildEvent"));
    }

    protected String getFormattedDuration() {
        Long duration = buildData.getDuration(null);
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
