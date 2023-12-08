package io.jenkins.plugins.maplelabs.Events;

import java.util.logging.Logger;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import hudson.Extension;
import jenkins.security.SecurityListener;

import io.jenkins.plugins.maplelabs.Events.interfaces.ISecurityEvent;
import io.jenkins.plugins.maplelabs.Utils;
import io.jenkins.plugins.maplelabs.Events.Collector.SecurityEventCollectorImpl;

@Extension
public class SecurityEventListener extends SecurityListener {
    ISecurityEvent eventCollector;
    private static final Logger logger = Logger.getLogger(SecurityEventListener.class.getName());

    /**
     * This function will hit when user is created
     * 
     * @param username user name
     */
    @Override
    protected void userCreated(@NonNull String username) {
        try {
            logger.info("Start SceurityListener#userCreated");
            eventCollector = new SecurityEventCollectorImpl();
            eventCollector.collectEventData(username, ISecurityEvent.Type.USER_CREATED);
            Utils.sendEvent(eventCollector);
            logger.info("End SceurityListener#userCreated");
        } catch (Exception e) {
            logger.severe("Failed to process User creation : "+e.toString());
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
            eventCollector = new SecurityEventCollectorImpl();
            eventCollector.collectEventData(details);
            Utils.sendEvent(eventCollector);
            logger.info("End SceurityListener#authenticated2");
        } catch (Exception e) {
            logger.severe("Failed to process User authenticated : "+e.toString());
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
            eventCollector = new SecurityEventCollectorImpl();
            eventCollector.collectEventData(username, ISecurityEvent.Type.FAILEDTOAUTHENTICATE);
            Utils.sendEvent(eventCollector);
            logger.info("End SceurityListener#failedToAuthenticate");
        } catch (Exception e) {
            logger.severe("Failed to process User failed to auth : "+e.toString());
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
            eventCollector = new SecurityEventCollectorImpl();
            eventCollector.collectEventData(username, ISecurityEvent.Type.LOGGEDIN);
            Utils.sendEvent(eventCollector);
            logger.info("End SceurityListener#loggedIn");
        } catch (Exception e) {
            logger.severe("Failed to process User login : "+e.toString());
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
            eventCollector = new SecurityEventCollectorImpl();
            eventCollector.collectEventData(username, ISecurityEvent.Type.FAILEDTOLOGIN);
            Utils.sendEvent(eventCollector);
            logger.info("End SceurityListener#failedToLogIn");
        } catch (Exception e) {
            logger.severe("Failed to process User failed login : "+e.toString());
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
            eventCollector = new SecurityEventCollectorImpl();
            eventCollector.collectEventData(username, ISecurityEvent.Type.LOGGEDOUT);
            Utils.sendEvent(eventCollector);
            logger.info("End SceurityListener#loggedOut");
        } catch (Exception e) {
            logger.severe("Failed to process User log out : "+e.toString());
        }
    }
}
