package com.apm.jenkins.plugins.Client;

import java.util.logging.Logger;

import net.sf.json.JSONObject;


import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.TagsUtil;
import com.apm.jenkins.plugins.interfaces.APMClient;
import com.apm.jenkins.plugins.interfaces.Events.APMEvent;

/**
 * This class is used to collect all methods that has to do with transmitting
 * data to APM.
 */

public class APMHttpClient implements APMClient {

    private static APMHttpClient instance = null;
    // Used to determine if the instance failed last validation last time, so
    // we do not keep retrying to create the instance and logging the same error
    private static boolean failedLastValidation = false;

    private static final Logger logger = Logger.getLogger(APMHttpClient.class.getName());
        
    public final static boolean enableValidations = true;

    public static APMClient getInstance(){
        // If the configuration has not changed, return the current instance without validation
        // since we've already validated and/or errored about the data

        APMHttpClient newInstance = new APMHttpClient();
        if (instance != null && instance.equals(newInstance)) {
            if (APMHttpClient.failedLastValidation) {
                return null;
            }
            return instance;
        }
        if (enableValidations) {
            synchronized (APMHttpClient.class) {
                APMHttpClient.instance = newInstance;
                try {
                    //newInstance.validateConfiguration();
                    APMHttpClient.failedLastValidation = false;
                } catch(IllegalArgumentException e){
                    logger.severe(e.getMessage());
                    APMHttpClient.failedLastValidation = true;
                    return null;
                }
            }
        }
        return newInstance;
    }

  
        
    public boolean postEvent(APMEvent event) {
        logger.fine("Sending event");
        // if(this.isDefaultIntakeConnectionBroken()){
        //     logger.severe("Your client is not initialized properly");
        //     return false;
        // }

        try {
            JSONObject payload = new JSONObject();
            payload.put("text", event.getText());
            payload.put("host", event.getHost());
            payload.put("title", event.getTitle());
            payload.put("date_happened", event.getDate());            
            payload.put("priority", event.getPriority().name().toLowerCase());
            payload.put("alert_type", event.getAlertType().name().toLowerCase());            
            
            logger.info(String.format("payload: %s", payload.toString()));            
            return true;
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to send event");
            return false;
        }
    }    
       
}