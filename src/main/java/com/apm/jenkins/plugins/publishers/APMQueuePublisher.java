package com.apm.jenkins.plugins.publishers;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jenkinsci.plugins.workflow.support.steps.ExecutorStepExecution;

import hudson.Extension;
import hudson.model.FreeStyleProject;
import hudson.model.PeriodicWork;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.Queue.Task;

@Extension
public class APMQueuePublisher extends PeriodicWork{
	
    private static final Logger logger = Logger.getLogger(APMQueuePublisher.class.getName());

    private static final long RECURRENCE_PERIOD = TimeUnit.MINUTES.toMillis(1);
    private final Queue queue = Queue.getInstance();

	@Override
	public long getRecurrencePeriod() {
		return RECURRENCE_PERIOD;
	}

	@Override
	protected void doRun() throws Exception {
        try {
            logger.fine("doRun called: Computing queue metrics");

            //TODO Get APM Client Instance

            long size = 0;
            long buildable = 0;
            long pending = 0;
            long stuck = 0;
            long blocked = 0;
            final Queue.Item[] items = queue.getItems();
            for (Queue.Item item : items) {
                String job_name;
                Task task = item.task;
                if (task instanceof FreeStyleProject) {
                    job_name = task.getFullDisplayName();
                } else if (task instanceof ExecutorStepExecution.PlaceholderTask) {
                    Run<?, ?> run = ((ExecutorStepExecution.PlaceholderTask) task).runForDisplay();
                    if (run != null) {
                        job_name = run.getParent().getFullName();
                    } else {
                        job_name = "unknown";
                    }
                } else {
                    job_name = "unknown";
                }
                                        
                boolean isStuck = false;
                boolean isBuildable = false;
                boolean isBlocked = false;
                boolean isPending = false;
                
                size++;
                if(item.isStuck()){
                    isStuck = true;
                    stuck++;
                }
                if (item.isBuildable()){
                    isBuildable = true;
                    buildable++;
                }
                if(item.isBlocked()){
                    isBlocked = true;
                    blocked++;
                }
                if(queue.isPending(task)){
                    isPending = true;
                    pending++;
                }
                
                //TODO: can use below tag names
                logger.info("--------------------------");
                logger.info("job_name=" + job_name);
                logger.info("jenkins.queue.job.in_queue=" + 1);
                logger.info("jenkins.queue.job.buildable=" + isBuildable);
                logger.info("jenkins.queue.job.pending=" + isPending);
                logger.info("jenkins.queue.job.stuck=" + isStuck);
                logger.info("jenkins.queue.job.blocked=" + isBlocked);
                logger.info("--------------------------");
            }

            //TODO: can use below tag names
            logger.info("--------------------------");
            logger.info("jenkins.queue.size=" + size);
            logger.info("jenkins.queue.buildable=" + buildable);
            logger.info("jenkins.queue.pending=" + pending);
            logger.info("jenkins.queue.stuck=" + stuck);
            logger.info("jenkins.queue.blocked=" + blocked);
            logger.info("--------------------------");

        } catch (Exception e) {
        	logger.severe("Failed to compute and send queue metrics, due to:" + e);
        }
    }

}
