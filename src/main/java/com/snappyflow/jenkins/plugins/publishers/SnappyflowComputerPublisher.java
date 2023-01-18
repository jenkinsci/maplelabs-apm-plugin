package com.snappyflow.jenkins.plugins.publishers;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.PeriodicWork;
import jenkins.model.Jenkins;

@Extension
public class SnappyflowComputerPublisher extends PeriodicWork {
	
    private static final Logger logger = Logger.getLogger(SnappyflowComputerPublisher.class.getName());

    private static final long RECURRENCE_PERIOD = TimeUnit.SECONDS.toMillis(30);

    @Override
    public long getRecurrencePeriod() {
        return RECURRENCE_PERIOD;
    }

    @Override
    protected void doRun() throws Exception {
        try {
            logger.fine("doRun called: Computing Node metrics");

            long nodeCount = 0;
            long nodeOffline = 0;
            long nodeOnline = 0;
            Jenkins jenkins = Jenkins.getInstanceOrNull();
            Computer[] computers = new Computer[0];
            if(jenkins != null){
                computers = jenkins.getComputers();
            } else {
            	logger.fine("Instance is null and couldn't retrieve computers.");
            }
            
            for (Computer computer : computers) {
                nodeCount++;
                if (computer.isOffline()) {
                    nodeOffline++;
                    
                }   
                if (computer.isOnline()) {
                    nodeOnline++;
                    
                }
                int executorCount = computer.countExecutors();
                int inUse = computer.countBusy();
                int free = computer.countIdle();
                
                logger.info("---------Executors--------");
                logger.info("executorCount=" + executorCount);
                logger.info("inUse=" + inUse);
                logger.info("free=" + free);
                logger.info("--------------------------");
            }
            logger.info("----------Node------------");
            logger.info("nodeCount=" + nodeCount);
            logger.info("nodeOffline=" + nodeOffline);
            logger.info("nodeOnline=" + nodeOnline);
            logger.info("--------------------------");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}
