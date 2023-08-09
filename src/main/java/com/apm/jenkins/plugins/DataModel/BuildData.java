package com.apm.jenkins.plugins.DataModel;


import java.util.List;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.Cause;
import hudson.model.Result;
import hudson.model.CauseAction;
import hudson.model.TaskListener;
import com.apm.jenkins.plugins.*;
import hudson.util.LogTaskListener;

public class BuildData  {

    private Long endTime;
    private Long duration;
    private String result;
    private String userId; 
    private Long startTime;
    private String jobName; 
    private String buildUrl; 
    private String hostname;
    private int buildNumber; 
    private String parentName;
    private boolean isCompleted;

    private static transient final Logger LOGGER = Logger.getLogger(BuildData.class.getName());

    public BuildData(Run run, TaskListener listener) throws IOException, InterruptedException {
        if (run == null) {
            return;
        }
        EnvVars envVars;
        if(listener != null){
            envVars = run.getEnvironment(listener);
        }else{
            envVars = run.getEnvironment(new LogTaskListener(LOGGER, Level.INFO));
        }

        setBuildUrl(envVars.get("BUILD_URL"));

        // Populate instance using run instance
        // Set StartTime, EndTime and Duration
        long startTimeInMs = run.getStartTimeInMillis();
        setStartTime(startTimeInMs);

        long durationInMs = run.getDuration();
        setDuration(durationInMs);

        if (durationInMs != 0 && startTimeInMs != 0) {
            Long endTimeInMs = startTimeInMs + durationInMs;
            setEndTime(endTimeInMs);
        }

        // Set UserId
        setUserId(getUserId(run));

        // Set Result and completed status
        if (run.getResult() != null)
        {
            Result result = run.getResult();
        	setResult(result.toString());
           	setCompleted(result.completeBuild);
        }

        // Set Build Number
        setBuildNumber(run.getNumber());
        // Set Hostname
        setHostname(APMUtil.getHostname(envVars));

        // Set Job Name
        parentName = run.getParent().getName();

        String baseJobName = run.getParent().getParent().getFullName();
        if (baseJobName.length() == 0) {
            baseJobName = parentName;
        }
        
        setJobName(run.getParent().getFullName());
    }


    private <A> A defaultIfNull(A value, A defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    
    public String getJobName(String value) {
        return defaultIfNull(jobName, value);
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getResult(String value) {
        return defaultIfNull(result, value);
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }


    public String getHostname(String value) {
        return defaultIfNull(hostname, value);
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getBuildUrl(String value) {
        return defaultIfNull(buildUrl, value);
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    
    public int getBuildNumber(int value) {
        return defaultIfNull(buildNumber, value);
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public Long getDuration(Long value) {
        return defaultIfNull(duration, value);
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getEndTime(Long value) {
        return defaultIfNull(endTime, value);
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getStartTime(Long value) {
        return defaultIfNull(startTime, value);
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getUserId() {
    return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String getUserId(Run run) {
        String userName;
        List<CauseAction> actions = null;
        try {
            actions = run.getActions(CauseAction.class);
        }catch(RuntimeException e){
            //noop
        }
        if(actions != null){
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

    private String getUserId(Cause cause){
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

    public String getParentName() {
        return parentName;
    }

}