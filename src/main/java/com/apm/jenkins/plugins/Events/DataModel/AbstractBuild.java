package com.apm.jenkins.plugins.Events.DataModel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.apm.jenkins.plugins.APMUtil;

import hudson.EnvVars;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.util.LogTaskListener;

public abstract class AbstractBuild extends AbstractEvent {
    private Long endTime;
    private Long duration;
    private String result;
    // private String userId;
    private Long startTime;
    private String jobName;
    private String buildUrl;
    private String hostname;
    private String parentName;
    private String buildNumber;

    private static final Logger logger = Logger.getLogger(AbstractBuild.class.getName());

    protected AbstractBuild(Run run, TaskListener listener) throws IOException, InterruptedException {
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
        // setUserId(getUserId(run));

        // Set Build Number
        setBuildNumber(Integer.toString(run.getNumber()));

        // Set Hostname
        setHostname(APMUtil.getHostname(envVars));

        // build url
        setBuildUrl(envVars.get("BUILD_URL"));

        // Set Job Name
        Job parentJob = run.getParent();
        setParentName(parentJob.getName());
        setJobName(parentJob.getFullName());

        setHost(APMUtil.getHostname(envVars));
        setDate(APMUtil.currentTimeMillis() / 1000);
    }

    protected String getJobName(String value) {
        return APMUtil.getValue(jobName, value);
    }

    protected void setJobName(String jobName) {
        this.jobName = jobName;
    }

    protected String getResult() {
        return result;
    }

    protected String getResult(String value) {
        return APMUtil.getValue(result, value);
    }

    protected void setResult(String result) {
        this.result = result;
    }

    protected String getHostname() {
        return hostname;
    }

    protected String getHostname(String value) {
        return APMUtil.getValue(hostname, value);
    }

    protected void setHostname(String hostname) {
        this.hostname = hostname;
    }

    protected String getBuildUrl() {
        return buildUrl;
    }

    protected String getBuildUrl(String value) {
        return APMUtil.getValue(buildUrl, value);
    }

    protected void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    protected String getBuildNumber() {
        return buildNumber;
    }

    protected String getBuildNumber(String value) {
        return APMUtil.getValue(buildNumber, value);
    }

    protected void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    protected Long getDuration() {
        return duration;
    }

    protected Long getDuration(Long value) {
        return APMUtil.getValue(duration, value);
    }

    protected void setDuration(Long duration) {
        this.duration = duration;
    }

    protected Long getEndTime() {
        return endTime;
    }

    protected Long getEndTime(Long value) {
        return APMUtil.getValue(endTime, value);
    }

    protected void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    protected Long getStartTime() {
        return startTime;
    }

    protected Long getStartTime(Long value) {
        return APMUtil.getValue(startTime, value);
    }

    protected void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    // protected String getUserId() {
    // return userId;
    // }

    // protected void setUserId(String userId) {
    // this.userId = userId;
    // }

    // private String getUserId(Run run) {
    // String userName;
    // List<CauseAction> actions = null;
    // try {
    // actions = run.getActions(CauseAction.class);
    // }catch(RuntimeException e){
    // //noop
    // }
    // if(actions != null){
    // for (CauseAction action : actions) {
    // if (action != null && action.getCauses() != null) {
    // for (Cause cause : action.getCauses()) {
    // userName = getUserId(cause);
    // if (userName != null) {
    // return userName;
    // }
    // }
    // }
    // }
    // }

    // if (run.getParent().getClass().getName().equals("hudson.maven.MavenModule"))
    // {
    // return "maven";
    // }
    // return "anonymous";
    // }

    // private String getUserId(Cause cause){
    // if (cause instanceof Cause.UserIdCause) {
    // String userName = ((Cause.UserIdCause) cause).getUserId();
    // if (userName != null) {
    // return userName;
    // }
    // } else if (cause instanceof Cause.UpstreamCause) {
    // for (Cause upstreamCause : ((Cause.UpstreamCause) cause).getUpstreamCauses())
    // {
    // String username = getUserId(upstreamCause);
    // if (username != null) {
    // return username;
    // }
    // }
    // }
    // return null;
    // }

    protected void setParentName(String parentName) {
        this.parentName = parentName;
    }

    protected String getParentName() {
        return parentName;
    }

    protected String getParentName(String defaultValue) {
        return APMUtil.getValue(parentName, defaultValue);
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
