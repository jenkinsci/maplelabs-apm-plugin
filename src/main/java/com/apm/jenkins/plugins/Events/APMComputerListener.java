package com.apm.jenkins.plugins.Events;


import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.CheckForNull;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;
import hudson.slaves.ComputerListener;

import com.apm.jenkins.plugins.Events.interfaces.ComputerEvent;
import com.apm.jenkins.plugins.Events.Collector.ComputerEventCollector;

@Extension
public class APMComputerListener extends ComputerListener {
        ComputerEvent eventCollector;
        private static final Logger logger = Logger.getLogger(APMComputerListener.class.getName());

        @Override
        public void onOffline(@Nonnull Computer computer, @CheckForNull OfflineCause cause) {
            try {
                logger.info("Start ComputerListener#onOffline");
                eventCollector = new ComputerEventCollector();
                eventCollector.collectEventData(computer, cause, ComputerEvent.Type.OFFLINE);
                logger.info("End ComputerListener#onOffline");
            } catch (Exception e) {
                logger.severe("Failed to process computer offline event");
                e.printStackTrace();
            }
        }

        @Override
        public void onTemporarilyOffline(Computer computer, OfflineCause cause) {
            try {
                logger.info("Start ComputerListener#onTemporarilyOffline");
                eventCollector = new ComputerEventCollector();
                eventCollector.collectEventData(computer, cause, ComputerEvent.Type.TEMPORARILYOFFLINE);
                logger.info("End ComputerListener#onTemporarilyOffline");
            } catch (Exception e) {
                logger.severe("Failed to process computer temporarily offline event");
                e.printStackTrace();
            }
        }

        @Override
        public void onTemporarilyOnline(Computer computer) {
            try {
                logger.info("Start ComputerListener#onTemporarilyOnline");
                eventCollector = new ComputerEventCollector();
                eventCollector.collectEventData(computer);
                logger.info("End ComputerListener#onTemporarilyOnline");
            } catch (Exception e) {
                logger.severe("Failed to process computer temporarily online event");
                e.printStackTrace();
            }
        }

        @Override
        public void onLaunchFailure(Computer computer, TaskListener taskListener)
                throws IOException, InterruptedException {
            try {
                logger.info("Start ComputerListener#onLaunchFailure");
                eventCollector = new ComputerEventCollector();
                eventCollector.collectEventData(computer, taskListener);
                logger.info("End ComputerListener#onLaunchFailure");
            } catch (Exception e) {
                logger.severe("Failed to process launch failure");
                e.printStackTrace();
            }
        }
    }
