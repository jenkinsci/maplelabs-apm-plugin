package com.apm.jenkins.plugins.DataModel;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.User;
import hudson.model.Cause;
import hudson.model.Result;
import hudson.tasks.Mailer;
import hudson.model.CauseAction;
import hudson.model.TaskListener;
import com.apm.jenkins.plugins.*;
import hudson.model.ParameterValue;
import hudson.util.LogTaskListener;
import hudson.triggers.TimerTrigger;
import hudson.model.ParametersAction;
import hudson.model.TextParameterValue;
import hudson.model.StringParameterValue;
import hudson.model.BooleanParameterValue;
import org.apache.commons.lang.StringUtils;
import com.cloudbees.plugins.credentials.CredentialsParameterValue;

public class BuildData implements Serializable {

    private static final long serialVersionUID = 1L;
    private static transient final Logger LOGGER = Logger.getLogger(BuildData.class.getName());

    private String buildId;
    private String jobName;
    private String buildUrl;
    private String nodeName;
    private String buildTag;
    private String javaHome;
    private String workspace;
    private String parentName;
    private String jenkinsUrl;
    private String buildNumber;
    private String charsetName;
    private String baseJobName;
    private String executorNumber;
    private Map<String, String> buildParameters = new HashMap<>();

    private String result;
    private String userId;
    private String hostname;
    private String userEmail;
    private boolean isCompleted;
    private Map<String, Set<String>> tags;

    private Long endTime;
    private Long duration;
    private Long startTime;
    private Long millisInQueue;
    private Long propagatedMillisInQueue;

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

        // Populate instance using environment variables.
        populateEnvVariables(envVars);
        // Populate instance using run instance
        // Set StartTime, EndTime and Duration
        long startTimeInMs = run.getStartTimeInMillis();
        setStartTime(startTimeInMs);
        long durationInMs = run.getDuration();
        if (durationInMs == 0 && startTimeInMs != 0) {
            durationInMs = System.currentTimeMillis() - startTimeInMs;
        }
        setDuration(durationInMs);
        if (durationInMs != 0 && startTimeInMs != 0) {
            Long endTimeInMs = startTimeInMs + durationInMs;
            setEndTime(endTimeInMs);
        }

        // Set Jenkins Url
        setJenkinsUrl(APMUtil.getJenkinsUrl());
        // Set UserId
        setUserId(getUserId(run));
        // Set UserEmail
        if(StringUtils.isEmpty(getUserEmail(""))){
            setUserEmail(getUserEmailByUserId(getUserId()));
        }

        // Set Result and completed status
        Result result = run.getResult();
        if (result != null)
        {
        	setResult(result.toString());
           	setCompleted(result.completeBuild);
        }

        // Set Build Number
        setBuildNumber(String.valueOf(run.getNumber()));
        // Set Hostname
        setHostname(APMUtil.getHostname(envVars));
        // Save charset canonical name
        setCharset(run.getCharset());

        // Set Job Name
        String baseJobName = null;
        try {
            baseJobName = run.getParent().getParent().getFullName();
            if (baseJobName.length() == 0) {
                baseJobName = run.getParent().getName();
            }
        } catch(RuntimeException e){
            //noop
        }
        parentName = run.getParent().getName();
        setBaseJobName(normalizeJobName(baseJobName));
        String jobNameWithConfiguration = null;
        try {
            jobNameWithConfiguration = run.getParent().getFullName();
        } catch(RuntimeException e){
            //noop
        }
        setJobName(normalizeJobName(jobNameWithConfiguration));

        // Set Jenkins Url
        String jenkinsUrl = APMUtil.getJenkinsUrl();
        if("unknown".equals(jenkinsUrl) && envVars != null && envVars.get("JENKINS_URL") != null
                && !envVars.get("JENKINS_URL").isEmpty()) {
            jenkinsUrl = envVars.get("JENKINS_URL");
        }
        setJenkinsUrl(jenkinsUrl);
        
        // Build parameters
        populateBuildParameters(run);
    }

    private void populateBuildParameters(Run<?,?> run) {
        // Build parameters can be defined via Jenkins UI
        // or via Jenkinsfile (https://www.jenkins.io/doc/book/pipeline/syntax/#parameters)
        try {
            final ParametersAction parametersAction = run.getAction(ParametersAction.class);
            if(parametersAction == null){
            	return;
            }

            final List<ParameterValue> parameters = parametersAction.getAllParameters();
            if(parameters == null || parameters.isEmpty()){            	
                return;
            }

            for(final ParameterValue parameter : parameters) {
                // Only support parameters as string (only single line), boolean and credentials for the moment.
                // Credentials parameters are safe because the value will show the credential ID, not the secret itself.
                // Choice parameters are treated as string parameters internally.
                if((parameter instanceof StringParameterValue && !(parameter instanceof TextParameterValue))
                        || parameter instanceof BooleanParameterValue
                        || parameter instanceof CredentialsParameterValue) {
                    this.buildParameters.put(parameter.getName(), String.valueOf(parameter.getValue()));
                }
            }
            LOGGER.info("Filled Build Params: "+ this.buildParameters);            
        } catch (Throwable ex) {
            APMUtil.severe(LOGGER, ex, "Failed to populate Jenkins build parameters.");
        }
    }

    private void populateEnvVariables(EnvVars envVars){
        if (envVars == null) {
            return;
        }
        setBuildId(envVars.get("BUILD_ID"));
        setBuildUrl(envVars.get("BUILD_URL"));
        setNodeName(envVars.get("NODE_NAME"));
        setBuildTag(envVars.get("BUILD_TAG"));
        setExecutorNumber(envVars.get("EXECUTOR_NUMBER"));
        setJavaHome(envVars.get("JAVA_HOME"));
        setWorkspace(envVars.get("WORKSPACE"));
    }

    /**
     * Assembles a map of tags containing:
     * - Build Tags
     * - Global Job Tags set in Job Properties
     * - Global Tag set in Jenkins Global configuration
     *
     * @return a map containing all tags values
     */
    public Map<String, Set<String>> getTags() {
        Map<String, Set<String>> allTags = new HashMap<>();
        allTags = TagsUtil.merge(allTags, tags);
        allTags = TagsUtil.addTagToTags(allTags, "job", getJobName("unknown"));

        if (nodeName != null) {
            allTags = TagsUtil.addTagToTags(allTags, "node", getNodeName("unknown"));
        }
        if (result != null) {
            allTags = TagsUtil.addTagToTags(allTags, "result", getResult("UNKNOWN"));
        }
        if (userId != null) {
            allTags = TagsUtil.addTagToTags(allTags, "user_id", getUserId());
        }
        if (jenkinsUrl != null) {
            allTags = TagsUtil.addTagToTags(allTags, "jenkins_url", getJenkinsUrl("unknown"));
        }

        return allTags;
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

    public String getBaseJobName(String value) {
        return defaultIfNull(baseJobName, value);
    }

    public void setBaseJobName(String baseJobName) {
        this.baseJobName = baseJobName;
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

    public Charset getCharset() {
        if (charsetName != null) {
            // Will throw an exception if there is an issue with
            // the charset canonical name.
            return Charset.forName(charsetName);
        }
        return Charset.defaultCharset();
    }

    public void setCharset(Charset charset) {
        if (charset != null) {
            this.charsetName = charset.name();
        }
    }

    public Map<String, String> getBuildParameters() {
        return this.buildParameters;
    }

    public String getNodeName(String value) {
        return defaultIfNull(nodeName, value);
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getBuildNumber(String value) {
        return defaultIfNull(buildNumber, value);
    }

    public void setBuildNumber(String buildNumber) {
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

    public Long getMillisInQueue(Long value) {
        return defaultIfNull(millisInQueue, value);
    }

    public void setMillisInQueue(Long millisInQueue) {
        this.millisInQueue = millisInQueue;
    }

    public Long getPropagatedMillisInQueue(Long value) {
        return defaultIfNull(propagatedMillisInQueue, value);
    }

    public void setPropagatedMillisInQueue(Long propagatedMillisInQueue) {
        this.propagatedMillisInQueue = propagatedMillisInQueue;
    }

    public String getBuildId(String value) {
        return defaultIfNull(buildId, value);
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getBuildTag(String value) {
        return defaultIfNull(buildTag, value);
    }

    public void setBuildTag(String buildTag) {
        this.buildTag = buildTag;
    }

    public String getJenkinsUrl(String value) {
        return defaultIfNull(jenkinsUrl, value);
    }

    public void setJenkinsUrl(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl;
    }

    public String getExecutorNumber(String value) {
        return defaultIfNull(executorNumber, value);
    }

    public void setExecutorNumber(String executorNumber) {
        this.executorNumber = executorNumber;
    }

    public String getJavaHome(String value) {
        return defaultIfNull(javaHome, value);
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String getWorkspace(String value) {
        return defaultIfNull(workspace, value);
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
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
        if (cause instanceof TimerTrigger.TimerTriggerCause) {
            return "timer";
        } else if (cause instanceof Cause.UserIdCause) {
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

    public String getUserEmail(final String value) {
        return defaultIfNull(this.userEmail, value);
    }

    private String getUserEmailByUserId(String userId) {
        try {
            if(StringUtils.isEmpty(userId)) {
                return null;
            }

            final User user = User.getById(userId, false);
            if(user == null){
                return null;
            }

            final Mailer.UserProperty mailInfo = user.getProperty(Mailer.UserProperty.class);
            if(mailInfo != null) {
                return mailInfo.getEmailAddress();
            }

            return null;
        } catch (Throwable ex) {
            APMUtil.severe(LOGGER, ex, "Failed to obtain the user email associated with the user " + userId);
            return null;
        }
    }

    public void setUserEmail(final String userEmail) {
        this.userEmail = userEmail;
    }

    private static String normalizeJobName(String jobName) {
        if (jobName == null) {
            return null;
        }
        return jobName.replaceAll("»", "/").replaceAll(" ", "");
    }

    public String getParentName() {
        return parentName;
    }

}