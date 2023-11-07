package org.maplelabs.jenkins.plugins.Events.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface ISecurityEvent extends IEvent {
    public String EVENT = "SecurityEvent";

    public static enum Type {
        LOGGEDIN,
        LOGGEDOUT,
        USER_CREATED,
        AUTHENTICATED,
        FAILEDTOLOGIN,
        FAILEDTOAUTHENTICATE,
    }

    public void collectEventData(UserDetails details);

    public void collectEventData(String name, Type type);
}
