package com.apm.jenkins.plugins.publishers;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.Queue;
import hudson.model.Result;
import jenkins.model.Jenkins;
import hudson.model.PeriodicWork;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.ClientBase;
import com.apm.jenkins.plugins.interfaces.APMClient;

@Extension
public class APMQueuePublisher extends PeriodicWork{
	
    private final Queue queue = Queue.getInstance();
    private static final long RECURRENCE_PERIOD = TimeUnit.MINUTES.toMillis(30);
    private static final Logger logger = Logger.getLogger(APMQueuePublisher.class.getName());

	@Override
	public long getRecurrencePeriod() {
		return RECURRENCE_PERIOD;
	}

	@Override
	protected void doRun() throws Exception {
        try {
            logger.fine("doRun called: Computing queue and job metrics");
            int stuck = 0;
            int pending = 0;
            int blocked = 0;
            int buildable = 0;
            APMClient client = ClientBase.getClient();
            final Queue.Item[] items = queue.getItems();
            HashMap<String, Object> jobStats_dict = APMUtil.getSnappyflowTags("jobStats");
            for (Queue.Item item : items) {                                       
                if(item.isStuck())  stuck++;                
                if(item.isBlocked())  blocked++;
                if (item.isBuildable()) buildable++;
                if(queue.isPending(item.task)) pending++;
           }

           jobStats_dict.put("queueStuck", stuck);
           jobStats_dict.put("queuePending", pending);
           jobStats_dict.put("queueBlocked", blocked);
           jobStats_dict.put("queueSize", items.length);
           jobStats_dict.put("queueBuildable", buildable);

            // Old Jobs
            int numJobAborted = 0;
            int numJobCompleted = 0;
            int numJobStarted = 0;
            for (Job job : Jenkins.get().getAllItems(Job.class)) {
                SortedMap<Integer,Run> builds = job.getBuildsAsMap();
                for (Run run : builds.values()) {
                    Result result = run.getResult();
                    numJobStarted++;
                    if (result == Result.SUCCESS) {
                        numJobCompleted++;
                    } else if (result == Result.ABORTED) {
                        numJobAborted++;
                    }
                }
            }
            jobStats_dict.put("num_job_aborted",numJobAborted);
            jobStats_dict.put("num_job_started",numJobStarted);
            jobStats_dict.put("num_job_completed",numJobCompleted);
            client.postSnappyflowMetric(jobStats_dict, "metric");
        } catch (Exception e) {
        	logger.severe("Failed to compute and send queue metrics, due to:" + e);
        }
    }

}
