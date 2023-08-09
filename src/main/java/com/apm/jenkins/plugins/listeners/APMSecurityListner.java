package com.apm.jenkins.plugins.listeners;

import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.Extension;
import jenkins.security.SecurityListener;
import org.springframework.security.core.userdetails.UserDetails;

import com.apm.jenkins.plugins.events.SecurityEvent;
/**
 * This class will trigger api event when security activity happened
 * Security activities: user created, Logined, failed to login, loggedOut
 */
@Extension
public class APMSecurityListner extends SecurityListener{
    // private HashMap<String, Object>  securityDict;
    com.apm.jenkins.plugins.interfaces.Events.SecurityEvent eventCollector;
    /**
     * This function will hit snappyflow when user is created
     * @param username user name
     */
    @Override
    protected void userCreated(@NonNull String username) {
        eventCollector = new SecurityEvent();
        eventCollector.collectEvent(username, SecurityEvent.Type.USER_CREATED);
    }

    /**
     * This function will hit snappyflow when user authenticated
     * @param details user details
     */
    @Override
    protected void authenticated2(@NonNull UserDetails details) {
        eventCollector.collectEvent(details);
    }

    /**
     * This function will hit snappyflow when user failed to authenticate
     * @param username user name
     */
    @Override
    protected void failedToAuthenticate(@NonNull String username){
        eventCollector.collectEvent(username, SecurityEvent.Type.FAILEDTOAUTHENTICATE);
    }

    /**
     * This function will hit snappyflow when user logged in
     * @param username user name
     */
    @Override
    protected void loggedIn(@NonNull String username){
        eventCollector.collectEvent(username, SecurityEvent.Type.LOGGEDIN);
    }

    /**
     * This function will hit snappyflow when user failed to login
     * @param username user name
     */
    @Override
    protected void failedToLogIn(@NonNull String username){
        eventCollector.collectEvent(username, SecurityEvent.Type.FAILEDTOLOGIN);
    }

    /**
     * This function will hit snappyflow when user loggedOut
     * @param username user name
     */
    @Override
    protected void loggedOut(@NonNull String username){
        eventCollector.collectEvent(username, SecurityEvent.Type.LOGGEDOUT);
    }

}
