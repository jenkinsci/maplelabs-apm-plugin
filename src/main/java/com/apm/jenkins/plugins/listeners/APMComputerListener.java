package com.apm.jenkins.plugins.listeners;

import java.util.Set;
import java.util.HashMap;
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
import com.apm.jenkins.plugins.events.*;
import com.apm.jenkins.plugins.Client.ClientBase;
import com.apm.jenkins.plugins.interfaces.APMEvent;
import com.apm.jenkins.plugins.interfaces.APMClient;


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

    private static final Logger logger = Logger.getLogger(APMComputerListener.class.getName());
    
    @Override
    public void onOffline(@Nonnull Computer computer, @CheckForNull OfflineCause cause) {
        try {            
            logger.info("Start APMComputerListener#onOffline");

            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            if (client == null) return;           
                      
            // Send event
            APMEvent event = new ComputerOfflineEvent(computer, cause, false);
            client.postEvent(event);

            logger.info("End APMComputerListener#onOffline");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process computer offline event");
        }
    }
    
    @Override
    public void onTemporarilyOffline(Computer computer, OfflineCause cause) {
        try {
            logger.fine("Start APMComputerListener#onTemporarilyOffline");
            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            if (client == null) return;
                        
            // Send event
            APMEvent event = new ComputerOfflineEvent(computer, cause, true);
            client.postEvent(event);
            
            logger.fine("End APMComputerListener#onTemporarilyOffline");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process computer temporarily offline event");
        }
    }

    
    @Override
    public void onTemporarilyOnline(Computer computer) {
         try {
            logger.fine("Start APMComputerListener#onTemporarilyOnline");

            APMClient client = ClientBase.getClient();
            if (client == null) return;
            // Send event
             APMEvent event = new ComputerOnlineEvent(computer, null, (HashMap<String, Set<String>>) APMUtil.getComputerTags(computer), true);
            client.postEvent(event);

            logger.fine("End APMComputerListener#onTemporarilyOnline");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process computer temporarily online event");
        }
    }

    @Override
    public void onLaunchFailure(Computer computer, TaskListener taskListener) throws IOException, InterruptedException {
        try {
            logger.fine("Start APMComputerListener#onLaunchFailure");

             APMClient client = ClientBase.getClient();
             if (client == null) return;
            // // Send event
            APMEvent event = new ComputerLaunchFailedEvent(computer, taskListener, APMUtil.getComputerTags(computer));
            client.postEvent(event);

            logger.fine("End APMComputerListener#onLaunchFailure");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process launch failure");
        }
    }
}