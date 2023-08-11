package com.apm.jenkins.plugins.Events.Collector;

import org.springframework.security.core.userdetails.UserDetails;

import com.apm.jenkins.plugins.Events.DataModel.AbstractEvent;
import com.apm.jenkins.plugins.Events.interfaces.SecurityEvent;

public class SecurityEventCollector extends AbstractEvent implements SecurityEvent {

    /**
     * This function will called when a user is authenticated
     * @param details
     * @return true if request processed
     */
    @Override
    public boolean CollectEventData(UserDetails details) {
        setEventType(EVENT);
        String title = "User "+details.getUsername()+" authenticated";
        setText(title);
        setTitle(title);
        setAlert(AlertType.INFO);
        setPriority(Priority.NORMAL);
        return sendEvent();
    }

    /**
     * This function will called when a user created/ failed to login/ login/ logout
     * @param details
     * @return true if request processed
     */
    @Override
    public boolean CollectEventData(String name, SecurityEvent.Type type) {
        setEventType(EVENT);
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
        }
        setText(title);
        setTitle(title);

        return sendEvent();
    }
}
