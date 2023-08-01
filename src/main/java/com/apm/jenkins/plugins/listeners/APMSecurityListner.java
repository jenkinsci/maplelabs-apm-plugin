package com.apm.jenkins.plugins.listeners;

import java.util.HashMap;
import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.Extension;
import jenkins.security.SecurityListener;
import org.springframework.security.core.userdetails.UserDetails;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.ClientBase;
import com.apm.jenkins.plugins.interfaces.APMEvent;
import com.apm.jenkins.plugins.interfaces.APMClient;
import com.apm.jenkins.plugins.events.UserAuthenticationEvent;

/**
 * This class will trigger api event when security activity happened
 * Security activities: user created, Logined, failed to login, loggedOut
 */
@Extension
public class APMSecurityListner extends SecurityListener{
    private HashMap<String, Object>  securityDict;

    /**
     * This function will hit snappyflow when user is created
     * @param username user name
     */
    @Override
    protected void userCreated(@NonNull String username) {
        APMClient client = ClientBase.getClient();
        if (client == null) return;
        securityDict =  APMUtil.getSnappyflowTags("securityStats");
        securityDict.put("username", username);
        securityDict.put("isFailed", false);
        securityDict.put("action", UserAuthenticationEvent.USER_CREATED);
        APMEvent event = new UserAuthenticationEvent(securityDict);
        client.postEvent(event);
    }

    /**
     * This function will hit snappyflow when user authenticated
     * @param details user details
     */
    @Override
    protected void authenticated2(@NonNull UserDetails details) {
        APMClient client = ClientBase.getClient();
        if (client == null) return;
        securityDict =  APMUtil.getSnappyflowTags("securityStats");
        securityDict.put("isFailed", false);
        securityDict.put("isEnabled", details.isEnabled());
        securityDict.put("username", details.getUsername());
        securityDict.put("action", UserAuthenticationEvent.AUTHENTICATION); 
        securityDict.put("isAccountLocked", !details.isAccountNonLocked());
        securityDict.put("isAccountExpired", !details.isAccountNonExpired());
        securityDict.put("isCredentialsExpired", !details.isCredentialsNonExpired());
        APMEvent event = new UserAuthenticationEvent(securityDict);
        client.postEvent(event);
    }

    /**
     * This function will hit snappyflow when user failed to authenticate
     * @param username user name
     */
    @Override
    protected void failedToAuthenticate(@NonNull String username){
        APMClient client = ClientBase.getClient();
        if (client == null) return;
        securityDict =  APMUtil.getSnappyflowTags("securityStats");
        securityDict.put("username", username);
        securityDict.put("isFailed", true);
        securityDict.put("action", UserAuthenticationEvent.AUTHENTICATION);
        APMEvent event = new UserAuthenticationEvent(securityDict);
        client.postEvent(event);
    }

    /**
     * This function will hit snappyflow when user logged in
     * @param username user name
     */
    @Override
    protected void loggedIn(@NonNull String username){
        APMClient client = ClientBase.getClient();
        if (client == null) return;
        securityDict.put("username", username);
        securityDict.put("isFailed", false);
        securityDict.put("action", UserAuthenticationEvent.LOGIN);
        APMEvent event = new UserAuthenticationEvent(securityDict);
        client.postEvent(event);
    }

    /**
     * This function will hit snappyflow when user failed to login
     * @param username user name
     */
    @Override
    protected void failedToLogIn(@NonNull String username){
        APMClient client = ClientBase.getClient();
        if (client == null) return;
        securityDict.put("username", username);
        securityDict.put("isFailed", true);
        securityDict.put("action", UserAuthenticationEvent.LOGIN);
        APMEvent event = new UserAuthenticationEvent(securityDict);
        client.postEvent(event);
    }

    /**
     * This function will hit snappyflow when user loggedOut
     * @param username user name
     */
    @Override
    protected void loggedOut(@NonNull String username){
        APMClient client = ClientBase.getClient();
        if (client == null) return;
        securityDict.put("username", username);
        securityDict.put("isFailed", false);
        securityDict.put("action", UserAuthenticationEvent.LOGOUT);
        APMEvent event = new UserAuthenticationEvent(securityDict);
        client.postEvent(event);
    }

}
