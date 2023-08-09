package com.apm.jenkins.plugins.interfaces;

import com.apm.jenkins.plugins.interfaces.Events.APMEvent;

//Add any other client Type if required in future.
public interface APMClient {
   /**
     * Sends an event to the APM API, including the event payload.
     *
     * @param event - a APMEvent object
     * @return a boolean to signify the success or failure of the HTTP POST request.
     */
     public boolean postEvent(APMEvent event);    
}
