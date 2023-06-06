package com.apm.jenkins.plugins.listeners;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.ComputerListener;
import hudson.slaves.OfflineCause;
import com.apm.jenkins.plugins.interfaces.APMClient;
import com.apm.jenkins.plugins.interfaces.APMEvent;
import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.ClientBase;
import com.apm.jenkins.plugins.events.*;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.logging.Logger;

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

    /*
    @Override
    public void onOnline(Computer computer, TaskListener listener) throws IOException, InterruptedException {
        try {
        	
            final boolean emitSystemEvents = APMUtil.getAPMGlobalDescriptor().isEmitSystemEvents();
            if (!emitSystemEvents) {
                return;
            }
            logger.fine("Start APMComputerListener#onOnline");

            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            if(client == null){
                return;
            }

            // Get the list of tags to apply
            Map<String, Set<String>> tags = TagsUtil.merge(
                    APMUtil.getTagsFromGlobalTags(),
                    APMUtil.getComputerTags(computer));

            // Send event
            APMEvent event = new ComputerOnlineEvent(computer, listener, tags, false);
            client.event(event);

            // Submit counter
            String hostname = APMUtil.getHostname(null);
            client.incrementCounter("jenkins.computer.online", hostname, tags);

            logger.fine("End APMComputerListener#onOnline");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process computer online event");
        }
    } */

    @Override
    public void onOffline(@Nonnull Computer computer, @CheckForNull OfflineCause cause) {
        try {
            /* final boolean emitSystemEvents = APMUtil.getAPMGlobalDescriptor().isEmitSystemEvents();
            if (!emitSystemEvents) {
                return;
            } */
            logger.info("Start APMComputerListener#onOffline");

            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            if(client == null){
                return;
            }            
                      
            // Send event
            APMEvent event = new ComputerOfflineEvent(computer, cause, false);
            client.event(event);

            // Submit counter
            // String hostname = APMUtil.getHostname(null);
            // client.incrementCounter("jenkins.computer.offline", hostname, tags);

            logger.info("End APMComputerListener#onOffline");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process computer offline event");
        }
    }
    
    @Override
    public void onTemporarilyOffline(Computer computer, OfflineCause cause) {
        try {
            /* final boolean emitSystemEvents = APMUtil.getDatadogGlobalDescriptor().isEmitSystemEvents();
            if (!emitSystemEvents) {
                return;
            } */
            logger.fine("Start APMComputerListener#onTemporarilyOffline");

            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            if(client == null){
                return;
            }
                        
            // Send event
            APMEvent event = new ComputerOfflineEvent(computer, cause, true);
            client.event(event);

            // Submit counter
            // String hostname = APMUtil.getHostname(null);
            // client.incrementCounter("jenkins.computer.temporarily_offline", hostname, tags);

            logger.fine("End APMComputerListener#onTemporarilyOffline");
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to process computer temporarily offline event");
        }
    }
}

