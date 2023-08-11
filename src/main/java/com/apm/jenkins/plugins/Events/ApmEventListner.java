package com.apm.jenkins.plugins.Events;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.CheckForNull;

import org.springframework.security.core.userdetails.UserDetails;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.Queue;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.OfflineCause;
import hudson.slaves.ComputerListener;
import jenkins.security.SecurityListener;
import hudson.model.listeners.RunListener;
import edu.umd.cs.findbugs.annotations.NonNull;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Events.interfaces.BuildEvent;
import com.apm.jenkins.plugins.Events.interfaces.ComputerEvent;
import com.apm.jenkins.plugins.Events.interfaces.SecurityEvent;
import com.apm.jenkins.plugins.Events.Collector.BuildEventCollector;
import com.apm.jenkins.plugins.Events.Collector.ComputerEventCollector;
import com.apm.jenkins.plugins.Events.Collector.SecurityEventCollector;


public class ApmEventListner {

    @Extension
    public static class Build extends RunListener<Run> {
        private static final Logger logger = Logger.getLogger(Build.class.getName());
        private BuildEvent buildCollector;

        @Override
        public void onStarted(Run run, TaskListener listener) {
            try {
                logger.fine("Start BuildListener#onStarted");
                buildCollector = new BuildEventCollector(run, listener);
                buildCollector.CollectEventData(BuildEvent.Type.STARTED);

                // item.getInQueueSince() may raise a NPE if a worker node is spinning up to run
                // the job.
                // This could be expected behavior with ec2 spot instances/ecs containers,
                // meaning no waiting
                // queue times if the plugin is spinning up an instance/container for one/first
                // job.
                Queue queue = Queue.getInstance();
                Queue.Item item = queue.getItem(run.getQueueId());
                try {
                    long waitingMs = (APMUtil.currentTimeMillis() - item.getInQueueSince());
                    logger.info("Job waiting time: " + TimeUnit.MILLISECONDS.toSeconds(waitingMs));
                } catch (RuntimeException e) {
                    logger.warning("Unable to compute 'waiting' metric. " +
                            "item.getInQueueSince() unavailable, possibly due to worker instance provisioning");
                }

                logger.info("End BuildListener#onStarted");
            } catch (Exception e) {
                logger.severe("Failed to process build start");
                e.printStackTrace();
            }
        }

        /**
         * This function will send job status to client
         */
        @Override
        public void onCompleted(Run run, @Nonnull TaskListener listener) {
            try {
                logger.fine("Start BuildListener#onCompleted");
                buildCollector = new BuildEventCollector(run, listener);
                buildCollector.CollectEventData(BuildEvent.Type.COMPLETED);
                logger.info("End BuildListener#onCompleted");
            } catch (Exception e) {
                logger.severe("Failed to process build completion");
                e.printStackTrace();
            }
        }
    }

    /**
     * This class registers an {@link ComputerListener} to trigger events and
     * calculate metrics:
     * - When a computer gets offline, the
     * {@link #onOffline(Computer, OfflineCause)} method will be invoked.
     * - When a computer gets temporarily online, the
     * {@link #onTemporarilyOnline(Computer)} method will be invoked.
     * - When a computer gets temporarily offline, the
     * {@link #onTemporarilyOffline(Computer, OfflineCause)} method will be invoked.
     * - When a computer failed to launch, the
     * {@link #onLaunchFailure(Computer, TaskListener)} method will be invoked.
     */
    @Extension
    public static class Node extends ComputerListener {
        ComputerEvent eventCollector;
        private static final Logger logger = Logger.getLogger(Node.class.getName());

        @Override
        public void onOffline(@Nonnull Computer computer, @CheckForNull OfflineCause cause) {
            try {
                logger.info("Start ComputerListener#onOffline");
                eventCollector = new ComputerEventCollector();
                eventCollector.CollectEventData(computer, cause, ComputerEvent.Type.OFFLINE);
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
                eventCollector.CollectEventData(computer, cause, ComputerEvent.Type.TEMPORARILYOFFLINE);
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
                eventCollector.CollectEventData(computer);
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
                eventCollector.CollectEventData(computer, taskListener);
                logger.info("End ComputerListener#onLaunchFailure");
            } catch (Exception e) {
                logger.severe("Failed to process launch failure");
                e.printStackTrace();
            }
        }
    }

    /**
     * This class will trigger api event when security activity happened
     * Security activities: user created, Logined, failed to login, loggedOut
     */
    @Extension
    public static class Sceurity extends SecurityListener {
        SecurityEvent eventCollector;
        private static final Logger logger = Logger.getLogger(Sceurity.class.getName());
        /**
         * This function will hit  when user is created
         * 
         * @param username user name
         */
        @Override
        protected void userCreated(@NonNull String username) {
            try {
                logger.info("Start SceurityListener#userCreated");
                eventCollector = new SecurityEventCollector();
                eventCollector.CollectEventData(username, SecurityEvent.Type.USER_CREATED);
                logger.info("Start SceurityListener#userCreated");
            } catch (Exception e) {
                logger.severe("Failed to process User creation");
                e.printStackTrace();
            }
        }

        /**
         * This function will hit when user authenticated
         * 
         * @param details user details
         */
        @Override
        protected void authenticated2(@NonNull UserDetails details) {
            try {
                logger.info("Start SceurityListener#authenticated2");
                eventCollector = new SecurityEventCollector();
                eventCollector.CollectEventData(details);
                logger.info("Start SceurityListener#authenticated2");
            } catch (Exception e) {
                 logger.severe("Failed to process User authenticated");
                e.printStackTrace();
            }
        }

        /**
         * This function will hit when user failed to authenticate
         * 
         * @param username user name
         */
        @Override
        protected void failedToAuthenticate(@NonNull String username) {
            try {
                logger.info("Start SceurityListener#failedToAuthenticate");
                eventCollector = new SecurityEventCollector();
                eventCollector.CollectEventData(username, SecurityEvent.Type.FAILEDTOAUTHENTICATE);
                logger.info("Start SceurityListener#failedToAuthenticate");
            } catch (Exception e) {
                 logger.severe("Failed to process User failed to auth");
                e.printStackTrace();
            }
        }

        /**
         * This function will hit when user logged in
         * 
         * @param username user name
         */
        @Override
        protected void loggedIn(@NonNull String username) {
            try {
                logger.info("Start SceurityListener#loggedIn");
                eventCollector = new SecurityEventCollector();
                eventCollector.CollectEventData(username, SecurityEvent.Type.LOGGEDIN);
                logger.info("Start SceurityListener#loggedIn");
            } catch (Exception e) {
                 logger.severe("Failed to process User login");
                e.printStackTrace();
            }
        }

        /**
         * This function will hit when user failed to login
         * 
         * @param username user name
         */
        @Override
        protected void failedToLogIn(@NonNull String username) {
            try {
                logger.info("Start SceurityListener#failedToLogIn");
                eventCollector = new SecurityEventCollector();
                eventCollector.CollectEventData(username, SecurityEvent.Type.FAILEDTOLOGIN);
                logger.info("Start SceurityListener#failedToLogIn");
            } catch (Exception e) {
                 logger.severe("Failed to process User failed login");
                e.printStackTrace();
            }
        }

        /**
         * This function will hit when user loggedOut
         * 
         * @param username user name
         */
        @Override
        protected void loggedOut(@NonNull String username) {
            try {
                logger.info("Start SceurityListener#loggedOut");
                eventCollector = new SecurityEventCollector();
                eventCollector.CollectEventData(username, SecurityEvent.Type.LOGGEDOUT);
                logger.info("Start SceurityListener#loggedOut");
            } catch (Exception e) {
                 logger.severe("Failed to process User log out");
                e.printStackTrace();
            }
        }
    }
}
