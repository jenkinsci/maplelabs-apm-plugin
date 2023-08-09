package com.apm.jenkins.plugins.interfaces.Events;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityEvent extends APMEvent {
    public static enum Type{
        USER_CREATED,
        AUTHENTICATED,
        FAILEDTOAUTHENTICATE,
        LOGGEDIN,
        FAILEDTOLOGIN,
        LOGGEDOUT;
    }
    public boolean collectEvent(UserDetails details);
    public boolean collectEvent(String name, Type type);
}
