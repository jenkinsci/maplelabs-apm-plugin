package com.apm.jenkins.plugins.interfaces;
import java.util.HashMap;

//Add any other client Type if required in future.
public interface APMClient {
    
	public static enum ClientType {
        HTTP;
				
        private ClientType() { }
    }

    public static enum Status {
        OK(0),
        WARNING(1),
        CRITICAL(2),
        UNKNOWN(3);

        private final int val;

        private Status(int val) {
            this.val = val;
        }

        public int toValue(){
           return this.val;
        }
    }
      
    public void setDestination(String destination);

    public void setHostname(String hostname); 
   
    public boolean isDefaultIntakeConnectionBroken();
    
    public void setDefaultIntakeConnectionBroken(boolean defaultIntakeConnectionBroken);

   /**
     * Sends an event to the APM API, including the event payload.
     *
     * @param event - a APMEvent object
     * @return a boolean to signify the success or failure of the HTTP POST request.
     */
     public boolean event(APMEvent event);
   
    public boolean postSnappyflowMetric(HashMap<String, Object> metrics, String type);    
    
}
