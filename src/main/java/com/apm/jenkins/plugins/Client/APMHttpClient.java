package com.apm.jenkins.plugins.Client;

import java.util.Base64;
import java.util.HashMap;
import java.io.IOException;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import javax.servlet.ServletException;
import java.security.KeyStoreException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.interceptor.RequirePOST;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;


import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.TagsUtil;
import com.apm.jenkins.plugins.interfaces.APMEvent;
import com.apm.jenkins.plugins.interfaces.APMClient;

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

    private static final String EVENT = "v1/events";
    private static final String METRIC = "v1/series";
        
    public final static boolean enableValidations = true;
    	    
    private boolean defaultIntakeConnectionBroken = false;
        
    private String appName = null;
    private String instName = null;
    private String destination = null;
    private String projectName = null;
    
    
    private enum SnappyflowTargetType {
        KAFKA("kafka_rest_proxy"),
        ELASTICSEARCH("elasticsearch");

        private String type;

        SnappyflowTargetType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }  
    
    public static APMClient getInstance(String projectName, String appName, String instName){
        // If the configuration has not changed, return the current instance without validation
        // since we've already validated and/or errored about the data

        APMHttpClient newInstance = new APMHttpClient(projectName, appName, instName);
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

    private APMHttpClient(String projectName, String appName, String instName) {
        this.projectName = projectName;
        this.appName = appName;
        this.instName = instName;
    }     
     
    public String getProjectName() {    	
    	// Lets see if project Name is filled by user now.
    	projectName = APMUtil.getAPMGlobalDescriptor().getTargetProjectName();    	
        return projectName;
    }
    
    public String getDestination() {
    	return destination;
    }
    
    @Override
    public void setDestination(String destination) {
    	this.destination = destination;
    }
    
    private String getBasicAuthenticationHeader(String username, String password) {
    	String valueToEncode = username+":"+password;
    	return "Basic "+Base64.getEncoder().encodeToString(valueToEncode.getBytes(Charset.forName("UTF-8"))).toString();
    }
    
    public void getKafkaHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl) {
       	
    	String host = APMUtil.getAPMGlobalDescriptor().getTargetHost();
        String profile = APMUtil.getAPMGlobalDescriptor().getTargetProfileName();
        targetApiUrl.append(host+"/topics/metric-"+profile);
        
        String token = APMUtil.getAPMGlobalDescriptor().getTargetKafkaToken();
        contentType.append("application/vnd.kafka.json.v2+json");	    	
        targetToken.append(token);
        
        logger.fine("targetApi URL for Kafka is: " + targetApiUrl.toString());
        logger.fine("Authroization for Kafka is: " + targetToken.toString());
 }
    
    public void getESHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl) {
    	String targetUsername = APMUtil.getAPMGlobalDescriptor().getTargetESUserName();
    	String targetPassword = APMUtil.getAPMGlobalDescriptor().getTargetESPassword();
    	String projName = getProjectName();
    	
    	if(projName == null) {
    		//We can't do much here, lets exit.
    		logger.severe("ProjectName in is null, please fill the required details of snappyflow in Manage Jenkins");
    		return;    		
    	}    	
    	
    	targetToken.append(getBasicAuthenticationHeader(targetUsername, targetPassword));
    	contentType.append("application/json");

    	String ds_host = APMUtil.getAPMGlobalDescriptor().getTargetHost();
    	String ds_port = APMUtil.getAPMGlobalDescriptor().getTargetPort();
    	String ds_protocol = APMUtil.getAPMGlobalDescriptor().getTargetProtocol();
    	String profile_id = APMUtil.getAPMGlobalDescriptor().getTargetProfileName();
    	String ds_index = "metric-"+profile_id+"-"+projName+"-$_write";
    	String ds_type = "_doc";
    	targetApiUrl.append(ds_protocol+"://"+ds_host+":"+ds_port+"/"+ds_index+"/"+ds_type);    		
    	logger.fine("targetApi URL for ES is:"+targetApiUrl.toString());
    	logger.fine("Authroization for ES is: " + targetToken.toString());
    }  
    
    @Override
    public void setHostname(String hostname) {
        // noop
    }
    
    @Override
    public boolean isDefaultIntakeConnectionBroken() {
        return defaultIntakeConnectionBroken;
    }

    @Override
    public void setDefaultIntakeConnectionBroken(boolean defaultIntakeConnectionBroken) {
        this.defaultIntakeConnectionBroken = defaultIntakeConnectionBroken;
    }

    public boolean postEvent(APMEvent event) {
        logger.fine("Sending event");
        if(this.isDefaultIntakeConnectionBroken()){
            logger.severe("Your client is not initialized properly");
            return false;
        }

        try {
            JSONObject payload = TagsUtil.convertHashMapToJSONObject(event.getSnappyflowTags());
            payload.put("title", event.getTitle());
            payload.put("text", event.getText());
            payload.put("host", event.getHost());
            payload.put("date_happened", event.getDate());            
            payload.put("priority", event.getPriority().name().toLowerCase());
            payload.put("alert_type", event.getAlertType().name().toLowerCase());            
            
            logger.info(String.format("payload: %s", payload.toString()));            
            return postSnappyflow(payload, EVENT);
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to send event");
            return false;
        }
    }    
   
    @Override
	public boolean postMetric(HashMap<String, Object> metrics, String type) {
        if(this.isDefaultIntakeConnectionBroken()){
            logger.severe("Your client is not initialized properly");
            return false;
        }
  
        JSONObject payload = TagsUtil.convertHashMapToJSONObject(metrics);        
        logger.info(String.format("payload: %s", payload.toString()));

        boolean status;
        try {
            status = postSnappyflow(payload, METRIC);
        } catch (Exception e) {
            // APMUtil.severe(logger, e, "Failed to send metric payload");
            status = false;
        }
        return status;
    }
    
    /**
     * Posts a given {@link JSONObject} payload to the APM snappyflow API, using the
     * user configured snappyflow details.
     *
     * @param payload - A JSONObject containing a specific subset of a builds metadata.
     * @param type    - A String containing the URL subpath pertaining to the type of API post required.
     * @return a boolean to signify the success or failure of the HTTP POST request.
     */
    
	@RequirePOST
    private boolean postSnappyflow(final JSONObject payload, final String type) throws IOException, ServletException{
    
        if(this.isDefaultIntakeConnectionBroken()){
            logger.severe("Your client is not initialized properly");
            return false;
        }
        
        boolean status = true;
        
        String KafkaData;
        StringEntity data;
        Boolean isKafka = false;
    	StringBuilder targetToken = new StringBuilder ();
    	StringBuilder contentType = new StringBuilder ();
    	StringBuilder targetApiUrl = new StringBuilder ();
        String targetType = APMUtil.getAPMGlobalDescriptor().getTargetSnappyFlowDestination();
        
        if(targetType == null ) {
            //No destination is configured.
            logger.severe("Target Destination is null");
            return false;
    	}    
    	try {

    		SSLContext sslContext = SSLContexts.custom()
    				.loadTrustMaterial((chain, authType) -> true)
    				.build();

    		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

    		HttpClient client = HttpClients.custom()
    				.setSSLSocketFactory(socketFactory)
    				.build();	
    		
            logger.fine("================================================");
            logger.fine("Target Type configured is: "+targetType);


    		if("SnappyflowES".equals(targetType)) {
    			// The value is equal to the Snappyflow Elasticsearch
    			getESHeaders(contentType, targetToken, targetApiUrl);
    		}
    		if("SnappyflowKafka".equals(targetType)){
    			// The value is equal to the Snappyflow Kafka 
    			getKafkaHeaders(contentType, targetToken, targetApiUrl);
    			isKafka = true;
    		}    			
    			
    		if (targetApiUrl != null) {

    			logger.fine("Setting up HttpPost...");
    			HttpPost post = new HttpPost(targetApiUrl.toString()); 	        			

    			post.setHeader("Content-Type",contentType.toString());
    			post.setHeader("Authorization",targetToken.toString());
    			post.setHeader( "Accept","application/vnd.kafka.v2+json");  			
    			
    			if (isKafka) {
    				// For Kafka, need to prefix data with `{\"records\":[{\"value\":"` 
    				KafkaData = payload.toString().replaceAll("=", ":");
    				KafkaData = "{\"records\":[{\"value\":" + KafkaData + "}]}";
    				data = new StringEntity(KafkaData,ContentType.APPLICATION_JSON);    				
    			}
    			else
    				data = new StringEntity(payload.toString().replaceAll("=", ":"),ContentType.APPLICATION_JSON);
    			
    			post.setEntity(data);

    			logger.fine("Post Headers:---------------");
    			Header[] headers = post.getAllHeaders();
    			for (Header header : headers) {    			
    				logger.fine(header.getName() + ":" + header.getValue());
    			}

    			logger.fine("Posted Data is here:---------------");
    			String requestBody = new String(data.getContent().readAllBytes(),StandardCharsets.UTF_8);
    			logger.fine(requestBody);
    						
    			HttpResponse response = client.execute(post);
    			int responseCode = response.getStatusLine().getStatusCode();

    			logger.info("\nSending 'POST' request to URL : " + targetApiUrl);
    			logger.info("================================================");
    			logger.info("Response Code : " + responseCode);

    			String responseBody = EntityUtils.toString((response).getEntity());
    			logger.fine(responseBody);    			
    		}
       	}catch(KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
			return false;
		}
    	return status;
	}
}