package com.apm.jenkins.plugins.events;

import java.util.HashMap;

public class UserAuthenticationEvent extends AbstractAPMSimpleEvent{

    public final static String LOGIN = "login";
    public final static String LOGOUT = "logout";
    public final static String USER_CREATED = "userCreation";
    public final static String AUTHENTICATION = "authentication";


    public UserAuthenticationEvent(HashMap<String, Object> tags) {
        String action = (String)tags.get("action");
        String username = tags.get("username") != null? (String)tags.get("username") : "anonymous";

        switch (action) {
            case LOGIN:
                action = "logged in.";
                break;
            case LOGOUT:
                action = "logged out.";
                break;
            case AUTHENTICATION:
                action = "authenticated.";
                break;
            case USER_CREATED:
                action = "created a user.";
                break;
            default: action += "ed.";
        }

        if(AUTHENTICATION == action && !(boolean)tags.get("isFailed")) {
            setPriority(Priority.NORMAL);
            setAlertType(AlertType.ERROR);
        } else{
            setPriority(Priority.LOW);
            setAlertType(AlertType.SUCCESS);
        }
        tags.put("event_type", SECURITY_EVENT_TYPE);
        setSnappyflowTags(tags);

        String title = "User " + username + " " + action;
        setTitle(title);

        String text = "%%% \nUser " + username + " " + action +
                "\n" + super.getLocationDetails() + " \n%%%";
        setText(text);
    }
}
