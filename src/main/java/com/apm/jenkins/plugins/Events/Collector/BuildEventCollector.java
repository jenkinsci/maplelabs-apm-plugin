package com.apm.jenkins.plugins.Events.Collector;

import java.io.IOException;
import java.util.logging.Logger;

import hudson.model.Run;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.plugins.git.util.BuildData;

import com.apm.jenkins.plugins.Events.interfaces.BuildEvent;
import com.apm.jenkins.plugins.Events.DataModel.AbstractBuild;

public class BuildEventCollector extends AbstractBuild implements BuildEvent {
    private static final Logger logger = Logger.getLogger(BuildData.class.getName());

    /**
     * Create Build details
     * @param run
     * @param listener
     */
    public BuildEventCollector(Run run, TaskListener listener) throws IOException, InterruptedException {
        super(run, listener);
    }

    /**
     * This function will handle Start and completion of a job
     * @param type
     */
    @Override
    public boolean collectEvent(Type type) {
        setEventType(EVENT);
        String userId, jobName, buildUrl, buildNumber, buildResult, title, text;
        switch (type) {
            case STARTED:
                userId = "";//buildData.getUserId();
                jobName = getJobName("unknown");
                buildUrl = getBuildUrl("unknown");
                buildNumber = getBuildNumber("unknown");
            
                title = "Job " + jobName + " build #" + buildNumber + " started on " + getHost();
                setTitle(title);

                text = "\nUser " + userId + " started the job " + jobName + " build #" +
                        buildNumber + "(" + buildUrl + ") " + "\n";
                setText(text);

                setPriority(Priority.LOW);
                setAlert(AlertType.INFO);
            break;
            case COMPLETED:
                userId = "";//buildData.getUserId();
                jobName = getJobName("unknown");
                buildUrl = getBuildUrl("unknown");
                buildResult = getResult("unknown");
                buildNumber = getBuildNumber("unknown");
                
                title = "Job " + jobName + " build #" + buildNumber + " " + buildResult.toLowerCase() + " on " + super.getHost();
                setTitle(title);
        
                text = "\n[Job " + jobName +" User " + userId + " build #" + buildNumber + " Parent "+getParentName()+"](" + buildUrl +
                        ") finished with status " + buildResult.toLowerCase() + " " + BuildEvent.getFormattedDuration(getDuration(0l)) +
                        "\n";
                setText(text);
                if (Result.SUCCESS.toString().equals(buildResult)) {
                    setPriority(Priority.LOW);
                    setAlert(AlertType.SUCCESS);
                } else if (Result.FAILURE.toString().equals(buildResult)) {
                    setPriority(Priority.NORMAL);
                    setAlert(AlertType.ERROR);
                } else {
                    setPriority(Priority.NORMAL);
                    setAlert(AlertType.WARNING);
            }
            break;
            default:
        }

        return sendEvent();
    }
    
}
