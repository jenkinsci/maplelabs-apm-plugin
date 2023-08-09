package com.apm.jenkins.plugins.events;

import org.springframework.security.core.userdetails.UserDetails;

public class SecurityEvent extends AbstractEvent implements com.apm.jenkins.plugins.interfaces.Events.SecurityEvent {

    @Override
    public boolean collectEvent(UserDetails details) {
        // authenticated2
        String title = "User "+details.getUsername()+" authenticated";
        setText(title);
        setTitle(title);
        setAlert(AlertType.INFO);
        setPriority(Priority.NORMAL);
        return send();
    }

    @Override
    public boolean collectEvent(String name, com.apm.jenkins.plugins.interfaces.Events.SecurityEvent.Type type) {
        // creation, failedToAuthenticate, login, failedToLogIn, logout
        String title = "User "+name+" ";
        switch (type) {
            case USER_CREATED:
                title = "New user "+ name +" added";
                setAlert(AlertType.INFO);
                setPriority(Priority.NORMAL);
                break;
            case FAILEDTOAUTHENTICATE:
                title += "failed to authenticate";
                setAlert(AlertType.WARNING);
                setPriority(Priority.LOW);
                break;
            case LOGGEDIN:
                title += "logged in.";
                setAlert(AlertType.INFO);
                setPriority(Priority.LOW);
                break;
            case FAILEDTOLOGIN:
                title += "failed to login";
                setAlert(AlertType.WARNING);
                setPriority(Priority.LOW);
                break;
            case LOGGEDOUT:
                title += "logged out";
                setAlert(AlertType.INFO);
                setPriority(Priority.LOW);
                break;
            default:
                title += "some made a action";
                setAlert(AlertType.WARNING);
                setPriority(Priority.LOW);
                break;
        }
        setText(title);
        setTitle(title);

        return send();
    }
}
