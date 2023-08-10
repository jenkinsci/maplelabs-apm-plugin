package com.apm.jenkins.plugins.Events.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityEvent extends Event {
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
