package com.apm.jenkins.plugins.Events;

import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.Extension;
import jenkins.security.SecurityListener;
import org.springframework.security.core.userdetails.UserDetails;

import com.apm.jenkins.plugins.Events.interfaces.SecurityEvent;
import com.apm.jenkins.plugins.Events.Collector.SecurityEventCollector;
/**
 * This class will trigger api event when security activity happened
 * Security activities: user created, Logined, failed to login, loggedOut
 */
@Extension
public class APMSecurityListner extends SecurityListener{
    // private HashMap<String, Object>  securityDict;
    SecurityEvent eventCollector;
    /**
     * This function will hit snappyflow when user is created
     * @param username user name
     */
    @Override
    protected void userCreated(@NonNull String username) {
        eventCollector = new SecurityEventCollector();
        eventCollector.CollectEventData(username, SecurityEvent.Type.USER_CREATED);
    }

    /**
     * This function will hit snappyflow when user authenticated
     * @param details user details
     */
    @Override
    protected void authenticated2(@NonNull UserDetails details) {
        eventCollector = new SecurityEventCollector();
        eventCollector.CollectEventData(details);
    }

    /**
     * This function will hit snappyflow when user failed to authenticate
     * @param username user name
     */
    @Override
    protected void failedToAuthenticate(@NonNull String username){
        eventCollector = new SecurityEventCollector();
        eventCollector.CollectEventData(username, SecurityEvent.Type.FAILEDTOAUTHENTICATE);
    }

    /**
     * This function will hit snappyflow when user logged in
     * @param username user name
     */
    @Override
    protected void loggedIn(@NonNull String username){
        eventCollector = new SecurityEventCollector();
        eventCollector.CollectEventData(username, SecurityEvent.Type.LOGGEDIN);
    }

    /**
     * This function will hit snappyflow when user failed to login
     * @param username user name
     */
    @Override
    protected void failedToLogIn(@NonNull String username){
        eventCollector = new SecurityEventCollector();
        eventCollector.CollectEventData(username, SecurityEvent.Type.FAILEDTOLOGIN);
    }

    /**
     * This function will hit snappyflow when user loggedOut
     * @param username user name
     */
    @Override
    protected void loggedOut(@NonNull String username){
        eventCollector = new SecurityEventCollector();
        eventCollector.CollectEventData(username, SecurityEvent.Type.LOGGEDOUT);
    }

}
