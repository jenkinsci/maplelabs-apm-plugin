package com.apm.jenkins.plugins.listeners;

import java.util.HashMap;
import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.Extension;
import jenkins.security.SecurityListener;

import org.springframework.security.core.userdetails.UserDetails;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.ClientBase;
import com.apm.jenkins.plugins.interfaces.APMClient;

/**
 * This class will trigger api event when security activity happened
 * Security activities: user created, Logined, failed to login, loggedOut, loggedOut
 */
@Extension
public class APMSecurityListner extends SecurityListener{
    private final static APMClient client = ClientBase.getClient();
    private final static HashMap<String, Object>  securityDict = APMUtil.getSnappyflowTags("securityStats");

    /**
     * This function will hit snappyflow when user is created
     * @param username user name
     */
    @Override
    protected void userCreated(@NonNull String username) {
        securityDict.put("username", username);
        securityDict.put("isFailed", false);
        securityDict.put("action", "userCreation");
		client.postSnappyflowMetric(securityDict, "metric");
    }

    /**
     * This function will hit snappyflow when user authenticated
     * @param details user details
     */
    @Override
    protected void authenticated2(@NonNull UserDetails details) {
        securityDict.put("isFailed", false);
        securityDict.put("action", "authentication"); 
        securityDict.put("isEnabled", details.isEnabled());
        securityDict.put("username", details.getUsername());
        securityDict.put("isAccountLocked", !details.isAccountNonLocked());
        securityDict.put("isAccountExpired", !details.isAccountNonExpired());
        securityDict.put("isCredentialsExpired", !details.isCredentialsNonExpired());
        client.postSnappyflowMetric(securityDict, "metric");
    }

    /**
     * This function will hit snappyflow when user failed to authenticate
     * @param username user name
     */
    @Override
    protected void failedToAuthenticate(@NonNull String username){
        securityDict.put("username", username);
        securityDict.put("isFailed", true);
        securityDict.put("action", "authentication");
        client.postSnappyflowMetric(securityDict, "metric");
    }

    /**
     * This function will hit snappyflow when user logged in
     * @param username user name
     */
    @Override
    protected void loggedIn(@NonNull String username){
        securityDict.put("username", username);
        securityDict.put("isFailed", false);
        securityDict.put("action", "login");
        client.postSnappyflowMetric(securityDict, "metric");
    }

    /**
     * This function will hit snappyflow when user failed to login
     * @param username user name
     */
    @Override
    protected void failedToLogIn(@NonNull String username){
        securityDict.put("username", username);
        securityDict.put("isFailed", true);
        securityDict.put("action", "login");
        client.postSnappyflowMetric(securityDict, "metric");
    }

    /**
     * This function will hit snappyflow when user loggedOut
     * @param username user name
     */
    @Override
    protected void loggedOut(@NonNull String username){
        securityDict.put("username", username);
        securityDict.put("isFailed", false);
        securityDict.put("action", "logout");
        client.postSnappyflowMetric(securityDict, "metric");
    }

}
