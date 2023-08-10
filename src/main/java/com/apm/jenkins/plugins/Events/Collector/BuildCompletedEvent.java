package com.apm.jenkins.plugins.Events.Collector;

import hudson.model.Result;
import com.apm.jenkins.plugins.DataModel.BuildData;

public class BuildCompletedEvent  extends AbstractAPMBuildEvent{

    public BuildCompletedEvent(BuildData buildData) {
        super(buildData);

		String userId = buildData.getUserId();
		String jobName = buildData.getJobName("unknown");
		String buildUrl = buildData.getBuildUrl("unknown");
        String buildResult = buildData.getResult("UNKNOWN");
        int buildNumber = buildData.getBuildNumber(-1);
		
        String title = "Job " + jobName + " build #" + buildNumber + " " + buildResult.toLowerCase() + " on " + super.getHost();
        setTitle(title);

        String text = "\n[Job " + jobName +" User " + userId + " build #" + buildNumber + " Parent "+buildData.getParentName()+"](" + buildUrl +
                ") finished with status " + buildResult.toLowerCase() + " " + getFormattedDuration() +
                "\n";
        setText(text);
        if (Result.SUCCESS.toString().equals(buildResult)) {
            setPriority(Priority.LOW);
            setAlertType(AlertType.SUCCESS);
        } else if (Result.FAILURE.toString().equals(buildResult)) {
            setPriority(Priority.NORMAL);
            setAlertType(AlertType.ERROR);
        } else {
            setPriority(Priority.NORMAL);
            setAlertType(AlertType.WARNING);
        }
    }
    
}
