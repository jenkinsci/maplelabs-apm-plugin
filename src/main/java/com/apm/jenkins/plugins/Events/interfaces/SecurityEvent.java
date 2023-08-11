package com.apm.jenkins.plugins.Events.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityEvent extends Event {
    public String EVENT="SecurityEvent";
    public static enum Type{
        LOGGEDIN,
        LOGGEDOUT,
        USER_CREATED,
        AUTHENTICATED,
        FAILEDTOLOGIN,
        FAILEDTOAUTHENTICATE,
    }
    public boolean collectEvent(UserDetails details);
    public boolean collectEvent(String name, Type type);
}
