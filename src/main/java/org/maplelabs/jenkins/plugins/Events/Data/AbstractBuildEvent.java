package org.maplelabs.jenkins.plugins.Events.Data;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.maplelabs.jenkins.plugins.Utils;

import hudson.EnvVars;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.Cause;
import hudson.model.Result;
import hudson.model.CauseAction;
import hudson.model.TaskListener;
import hudson.util.LogTaskListener;

public abstract class AbstractBuildEvent extends AbstractEvent {
    private Long endTime;
    private Long duration;
    private String result;
    private String userId;
    private Long startTime;
    private String jobName;
    private String buildUrl;
    private String hostname;
    private String parentName;
    private String buildNumber;

    private static final Logger logger = Logger.getLogger(AbstractBuildEvent.class.getName());

    protected AbstractBuildEvent(Run run, TaskListener listener) throws IOException, InterruptedException {
        if (run == null)
            return;

        EnvVars envVars;
        if (listener != null)
            envVars = run.getEnvironment(listener);
        else
            envVars = run.getEnvironment(new LogTaskListener(logger, Level.INFO));

        setTime(run);

        // Set Result
        Result result = run.getResult();
        if (result != null) {
            setResult(result.toString());
        }

        // Set UserId
        setUserId(getUserId(run));

        // Set Build Number
        setBuildNumber(Integer.toString(run.getNumber()));

        // Set Hostname
        setHostname(Utils.getHostName(envVars));

        // build url
        setBuildUrl(envVars.get("BUILD_URL"));

        // Set Job Name
        Job parentJob = run.getParent();
        setParentName(parentJob.getName());
        setJobName(parentJob.getFullName());

        setHost(Utils.getHostName(envVars));
        setDate(Utils.getCurrentTimeInMillis() / 1000);
    }

    protected String getJobName(String value) {
        return Utils.getValue(jobName, value);
    }

    protected void setJobName(String jobName) {
        this.jobName = jobName;
    }

    protected String getResult() {
        return result;
    }

    protected String getResult(String value) {
        return Utils.getValue(result, value);
    }

    protected void setResult(String result) {
        this.result = result;
    }

    protected String getHostName() {
        return hostname;
    }

    protected String getHostName(String value) {
        return Utils.getValue(hostname, value);
    }

    protected void setHostname(String hostname) {
        this.hostname = hostname;
    }

    protected String getBuildUrl() {
        return buildUrl;
    }

    protected String getBuildUrl(String value) {
        return Utils.getValue(buildUrl, value);
    }

    protected void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    protected String getBuildNumber() {
        return buildNumber;
    }

    protected String getBuildNumber(String value) {
        return Utils.getValue(buildNumber, value);
    }

    protected void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    protected Long getDuration() {
        return duration;
    }

    protected Long getDuration(Long value) {
        return Utils.getValue(duration, value);
    }

    protected void setDuration(Long duration) {
        this.duration = duration;
    }

    protected Long getEndTime() {
        return endTime;
    }

    protected Long getEndTime(Long value) {
        return Utils.getValue(endTime, value);
    }

    protected void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    protected Long getStartTime() {
        return startTime;
    }

    protected Long getStartTime(Long value) {
        return Utils.getValue(startTime, value);
    }

    protected void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    protected String getUserId() {
        return userId;
    }

    protected void setUserId(String userId) {
        this.userId = userId;
    }

    private String getUserId(Run run) {
        String userName;
        List<CauseAction> actions = null;
        actions = run.getActions(CauseAction.class);
        if (actions != null) {
            for (CauseAction action : actions) {
                if (action != null && action.getCauses() != null) {
                    for (Cause cause : action.getCauses()) {
                        userName = getUserId(cause);
                        if (userName != null) {
                            return userName;
                        }
                    }
                }
            }
        }

        if (run.getParent().getClass().getName().equals("hudson.maven.MavenModule")) {
            return "maven";
        }
        return "anonymous";
    }

    private String getUserId(Cause cause) {
        if (cause instanceof Cause.UserIdCause) {
            String userName = ((Cause.UserIdCause) cause).getUserId();
            if (userName != null) {
                return userName;
            }
        } else if (cause instanceof Cause.UpstreamCause) {
            for (Cause upstreamCause : ((Cause.UpstreamCause) cause).getUpstreamCauses()) {
                String username = getUserId(upstreamCause);
                if (username != null) {
                    return username;
                }
            }
        }
        return null;
    }

    protected void setParentName(String parentName) {
        this.parentName = parentName;
    }

    protected String getParentName() {
        return parentName;
    }

    protected String getParentName(String defaultValue) {
        return Utils.getValue(parentName, defaultValue);
    }

    /**
     * This function will set start, duration, end time
     * 
     * @param run
     */
    protected void setTime(Run run) {
        long startTimeInMs = run.getStartTimeInMillis();
        setStartTime(startTimeInMs);

        long durationInMs = run.getDuration();
        setDuration(durationInMs);

        if (durationInMs != 0 && startTimeInMs != 0) {
            Long endTimeInMs = startTimeInMs + durationInMs;
            setEndTime(endTimeInMs);
        }
    }
}
