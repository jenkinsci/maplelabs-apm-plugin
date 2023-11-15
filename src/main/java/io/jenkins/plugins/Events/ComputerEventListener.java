package io.jenkins.plugins.Events;

import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.CheckForNull;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;
import hudson.slaves.ComputerListener;

import io.jenkins.plugins.Events.interfaces.IComputerEvent;
import io.jenkins.plugins.Utils;
import io.jenkins.plugins.Events.Collector.ComputerEventCollectorImpl;

@Extension
public class ComputerEventListener extends ComputerListener {
    IComputerEvent eventCollector;
    private static final Logger logger = Logger.getLogger(ComputerEventListener.class.getName());

    @Override
    public void onOffline(@Nonnull Computer computer, @CheckForNull OfflineCause cause) {
        try {
            logger.info("Start ComputerListener#onOffline");
            eventCollector = new ComputerEventCollectorImpl();
            eventCollector.collectEventData(computer, cause, IComputerEvent.Type.OFFLINE);
            Utils.sendEvent(eventCollector);
            logger.info("End ComputerListener#onOffline");
        } catch (Exception e) {
            logger.severe("Failed to process computer offline event : "+e.toString());
        }
    }

    @Override
    public void onTemporarilyOffline(Computer computer, OfflineCause cause) {
        try {
            logger.info("Start ComputerListener#onTemporarilyOffline");
            eventCollector = new ComputerEventCollectorImpl();
            eventCollector.collectEventData(computer, cause, IComputerEvent.Type.TEMPORARILYOFFLINE);
            Utils.sendEvent(eventCollector);
            logger.info("End ComputerListener#onTemporarilyOffline");
        } catch (Exception e) {
            logger.severe("Failed to process computer temporarily offline event : "+e.toString());
        }
    }

    @Override
    public void onTemporarilyOnline(Computer computer) {
        try {
            logger.info("Start ComputerListener#onTemporarilyOnline");
            eventCollector = new ComputerEventCollectorImpl();
            eventCollector.collectEventData(computer);
            Utils.sendEvent(eventCollector);
            logger.info("End ComputerListener#onTemporarilyOnline");
        } catch (Exception e) {
            logger.severe("Failed to process computer temporarily online event : "+e.toString());
        }
    }

    @Override
    public void onLaunchFailure(Computer computer, TaskListener taskListener)
            throws IOException, InterruptedException {
        try {
            logger.info("Start ComputerListener#onLaunchFailure");
            eventCollector = new ComputerEventCollectorImpl();
            eventCollector.collectEventData(computer, taskListener);
            Utils.sendEvent(eventCollector);
            logger.info("End ComputerListener#onLaunchFailure");
        } catch (Exception e) {
            logger.severe("Failed to process launch failure : "+e.toString());
        }
    }
}
