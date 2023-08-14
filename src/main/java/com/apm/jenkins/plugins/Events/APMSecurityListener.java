package com.apm.jenkins.plugins.Events;

import java.util.logging.Logger;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import hudson.Extension;
import jenkins.security.SecurityListener;

import com.apm.jenkins.plugins.Events.interfaces.SecurityEvent;
import com.apm.jenkins.plugins.Events.Collector.SecurityEventCollector;

@Extension
public class APMSecurityListener extends SecurityListener {
    SecurityEvent eventCollector;
    private static final Logger logger = Logger.getLogger(APMSecurityListener.class.getName());

    /**
     * This function will hit when user is created
     * 
     * @param username user name
     */
    @Override
    protected void userCreated(@NonNull String username) {
        try {
            logger.info("Start SceurityListener#userCreated");
            eventCollector = new SecurityEventCollector();
            eventCollector.collectEventData(username, SecurityEvent.Type.USER_CREATED);
            logger.info("End SceurityListener#userCreated");
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
            eventCollector.collectEventData(details);
            logger.info("End SceurityListener#authenticated2");
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
            eventCollector.collectEventData(username, SecurityEvent.Type.FAILEDTOAUTHENTICATE);
            logger.info("End SceurityListener#failedToAuthenticate");
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
            eventCollector.collectEventData(username, SecurityEvent.Type.LOGGEDIN);
            logger.info("End SceurityListener#loggedIn");
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
            eventCollector.collectEventData(username, SecurityEvent.Type.FAILEDTOLOGIN);
            logger.info("End SceurityListener#failedToLogIn");
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
            eventCollector.collectEventData(username, SecurityEvent.Type.LOGGEDOUT);
            logger.info("End SceurityListener#loggedOut");
        } catch (Exception e) {
            logger.severe("Failed to process User log out");
            e.printStackTrace();
        }
    }
}
