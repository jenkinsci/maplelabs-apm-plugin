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

    /**
     * Increment a counter for the given metrics.
     * NOTE: To submit all counters you need to execute the flushCounters method.
     * This is to aggregate counters and submit them in batch to Datadog in order to minimize network traffic.
     * @param name     - metric name
     * @param hostname - metric hostname
     * @param tags     - metric tags
     * @return a boolean to signify the success or failure of increment submission.
     */
    // public boolean incrementCounter(String name, String hostname, Map<String, Set<String>> tags);

    /**
     * Submit all your counters as rate with 10 seconds intervals.
     */
    // public void flushCounters();

    /**
     * Sends a metric to the APM API, including the gauge name, and value.
     *
     * @param name     - A String with the name of the metric to record.
     * @param value    - A long containing the value to submit.
     * @param hostname - A String with the hostname to submit.
     * @param tags     - A Map containing the tags to submit.
     * @return a boolean to signify the success or failure of the HTTP POST request.
     */
    // public boolean gauge(String name, long value, String hostname, Map<String, Set<String>> tags);
    
    public boolean postSnappyflowMetric(HashMap<String, Object> metrics, String type);    
    
}
