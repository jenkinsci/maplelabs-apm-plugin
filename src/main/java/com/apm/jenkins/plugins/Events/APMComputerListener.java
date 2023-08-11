package com.apm.jenkins.plugins.Events;

import java.io.IOException;
import javax.annotation.Nonnull;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;
import hudson.slaves.ComputerListener;
import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Events.Collector.ComputerEventCollector;
import com.apm.jenkins.plugins.Events.interfaces.ComputerEvent;

/**
 * This class registers an {@link ComputerListener} to trigger events and calculate metrics:
 * - When a computer gets online, the {@link #onOnline(Computer, TaskListener)} method will be invoked.
 * - When a computer gets offline, the {@link #onOffline(Computer, OfflineCause)} method will be invoked.
 * - When a computer gets temporarily online, the {@link #onTemporarilyOnline(Computer)} method will be invoked.
 * - When a computer gets temporarily offline, the {@link #onTemporarilyOffline(Computer, OfflineCause)} method will be invoked.
 * - When a computer failed to launch, the {@link #onLaunchFailure(Computer, TaskListener)} method will be invoked.
 */
@Extension
public class APMComputerListener extends ComputerListener {
    ComputerEvent eventCollector;
    private static final Logger logger = Logger.getLogger(APMComputerListener.class.getName());
    
    @Override
    public void onOffline(@Nonnull Computer computer, @CheckForNull OfflineCause cause) {
        try {            
            logger.info("Start APMComputerListener#onOffline");
            eventCollector = new ComputerEventCollector();
            eventCollector.collectEvent(computer, cause, ComputerEvent.Type.OFFLINE);
            logger.info("End APMComputerListener#onOffline");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process computer offline event");
        }
    }
    
    @Override
    public void onTemporarilyOffline(Computer computer, OfflineCause cause) {
        try {
            logger.info("Start APMComputerListener#onTemporarilyOffline");                        
            eventCollector = new ComputerEventCollector();
            eventCollector.collectEvent(computer, cause, ComputerEvent.Type.TEMPORARILYOFFLINE);           
            logger.info("End APMComputerListener#onTemporarilyOffline");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process computer temporarily offline event");
        }
    }

    
    @Override
    public void onTemporarilyOnline(Computer computer) {
         try {
            logger.info("Start APMComputerListener#onTemporarilyOnline");
            eventCollector = new ComputerEventCollector();
            eventCollector.collectEvent(computer);
            logger.info("End APMComputerListener#onTemporarilyOnline");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process computer temporarily online event");
        }
    }

    @Override
    public void onLaunchFailure(Computer computer, TaskListener taskListener) throws IOException, InterruptedException {
        try {
            logger.info("Start APMComputerListener#onLaunchFailure");
            eventCollector = new ComputerEventCollector();
            eventCollector.collectEvent(computer, taskListener);
            logger.info("End APMComputerListener#onLaunchFailure");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process launch failure");
        }
    }
}