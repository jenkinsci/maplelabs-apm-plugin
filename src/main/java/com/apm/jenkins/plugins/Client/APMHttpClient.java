package com.apm.jenkins.plugins.Client;

import java.util.Base64;
import java.util.HashMap;
import javax.crypto.Cipher;
import java.io.IOException;
import javax.crypto.SecretKey;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import javax.servlet.ServletException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.TagsUtil;
import com.apm.jenkins.plugins.interfaces.APMClient;
import com.apm.jenkins.plugins.interfaces.APMEvent;

import org.kohsuke.stapler.interceptor.RequirePOST;


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
    
	private byte[] sessionKey = null;
    private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private  HashMap<String,String> target = new HashMap<String,String>();
	    
    private boolean defaultIntakeConnectionBroken = false;
        
    private String appName = null;
    private String instName = null;
    private String profileKey = null;
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
    
    public static APMClient getInstance(String profileKey, String projectName, String appName, String instName, String destination){
        // If the configuration has not changed, return the current instance without validation
        // since we've already validated and/or errored about the data

        APMHttpClient newInstance = new APMHttpClient(profileKey, projectName, appName, instName, destination);
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

    private APMHttpClient(String profileKey, String projectName, String appName, String instName, String destination) {
        this.profileKey = profileKey;
        this.projectName = projectName;
        this.appName = appName;
        this.instName = instName;
        this.destination = destination;
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
    
    public HashMap<String, String> decodeProfileKey() {    	   	
    	try {    
    		// Profile Key is not yet set by the user.
    		// Lets fetch it once again. Else, we wont be able to send the metrics.
    		profileKey = APMUtil.getAPMGlobalDescriptor().getTargetApiKey();

    		// There are chances that profile key is not set yet.
    		if(profileKey != null) {
    			SecretKey secretKey = new SecretKeySpec("SnappyFlow123456".getBytes("UTF-8"), ALGORITHM);
    			sessionKey = Base64.getDecoder().decode(profileKey);
    			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
    			cipher.init(Cipher.DECRYPT_MODE, secretKey,new IvParameterSpec(new byte[16]));
    			byte[] original = cipher.doFinal(sessionKey);
    			String bg = new String(original,StandardCharsets.UTF_8);
    			int index = bg.indexOf("{");
    			String tokenString = "{"+bg.substring(index+1).trim();
    			tokenString= tokenString.substring(1, tokenString.length()-1); //remove curly
    			String[] keyValuePairs = tokenString.split(","); //split the string
    			for(String pair : keyValuePairs) //iterate over the pairs
    			{
    				String[] entry = pair.split(": "); //split the pairs to get key and value    			
    				target.put((entry[0].trim()).replaceAll("\"", ""),(entry[1].trim()).replaceAll("\"", ""));
    			}
    			return target;
    		}    		
    		else
    			return null;
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();    		
    	}
    	return null;
    }
    
    private String getBasicAuthenticationHeader(String username, String password) {
    	String valueToEncode = username+":"+password;
    	return "Basic "+Base64.getEncoder().encodeToString(valueToEncode.getBytes(Charset.forName("UTF-8"))).toString();
    }
    
    public void getKafkaHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl, HashMap<String, String> targetMap) {
       	
       	targetApiUrl.append(targetMap.get("url")+"/topics/"+targetMap.get("profile_id"));
    	System.out.println("targetApi URL for Kafka is: " + targetApiUrl.toString());
    	
    	contentType.append("application/vnd.kafka.json.v2+json");	    	
   		targetToken.append(targetMap.get("token"));
    }
    
    public void getESHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl, HashMap<String, String> targetMap) {
    	String targetUsername = targetMap.get("username");
    	String projName = getProjectName();
    	
    	if(projName == null) {
    		//We can't do much here, lets exit.
    		logger.severe("ProjectName in is null, please fill the required details of snappyflow in Manage Jenkins");
    		return;    		
    	}    	
    	
    	targetToken.append(getBasicAuthenticationHeader(targetUsername, targetMap.get("password")));
    	contentType.append("application/json");

    	String ds_protocol = targetMap.get("protocol");
    	String ds_host = targetMap.get("host");
    	String ds_port = targetMap.get("port");    	  	
    	String ds_index = "metric-"+targetMap.get("profile_id")+"-"+projName+"-$_write";
    	String ds_type = "_doc";
    	targetApiUrl.append(ds_protocol+"://"+ds_host+":"+ds_port+"/"+ds_index+"/"+ds_type);    		
    	logger.info("targetApi URL for ES is:"+targetApiUrl.toString());
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

    public boolean event(APMEvent event) {
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
	public boolean postSnappyflowMetric(HashMap<String, Object> metrics, String type) {
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
        
    	StringBuilder targetToken = new StringBuilder ();
    	StringBuilder contentType = new StringBuilder ();
    	StringBuilder targetApiUrl = new StringBuilder ();
    	    	
    	// Specific to snappyflow target
      	HashMap<String, String> targetMap = decodeProfileKey();
    	if(targetMap != null) {
    		logger.info("================================================");
    		logger.info("decoded profile key:"+targetMap); 

    		try {

    			SSLContext sslContext = SSLContexts.custom()
    					.loadTrustMaterial((chain, authType) -> true)
    					.build();

    			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

    			HttpClient client = HttpClients.custom()
    					.setSSLSocketFactory(socketFactory)
    					.build();
    			
    			SnappyflowTargetType matchedType = null;
    			String targetType = targetMap.get("type");
    			System.out.println("Target type is:" + targetType);
    			
    			for (SnappyflowTargetType tType : SnappyflowTargetType.values()) {
    			    if (tType.getType().equalsIgnoreCase(targetType)) {
    			        matchedType = tType;
    			        break;
    			    }
    			}

    			if (matchedType != null) {
    			    System.out.println(matchedType);
    			    if (matchedType == SnappyflowTargetType.KAFKA) {
    			    	// The value is equal to the Kafka enum value 
    			    	getKafkaHeaders(contentType, targetToken, targetApiUrl, targetMap);
    			    } else if (matchedType == SnappyflowTargetType.ELASTICSEARCH) {
    			    	// The value is equal to the Elasticsearch enum value
    			    	getESHeaders(contentType, targetToken, targetApiUrl, targetMap);
    			    }
    			}
    			else {
    			    logger.severe("Target type not found");
    			    return false;
    			}
    			
    			
    			if (targetApiUrl != null) {
    				
    				logger.fine("Setting up HttpPost...");
        			HttpPost post = new HttpPost(targetApiUrl.toString()); 	        			
    				
    				post.setHeader("Content-Type",contentType.toString());
    				post.setHeader("Authorization",targetToken.toString());
    				post.setHeader( "Accept","application/vnd.kafka.v2+json");    				

    				StringEntity data = new StringEntity(payload.toString().replaceAll("=", ":"), "UTF-8");
    				post.setEntity(data);

    				System.out.println("Post Headers:---------------");
    				Header[] headers = post.getAllHeaders();
    				for (Header header : headers) {    			
    					System.out.println(header.getName() + ":" + header.getValue());
    				}

    				System.out.println("Data is here:---------------");
    				String bg = new String(data.getContent().readAllBytes(),StandardCharsets.UTF_8);
    				System.out.println(bg);

    				HttpResponse response = client.execute(post);
    				int responseCode = response.getStatusLine().getStatusCode();

    				System.out.println("\nSending 'POST' request to URL : " + targetApiUrl);
    				logger.info("================================================");
    				System.out.println("Response Code : " + responseCode);

    				String responseBody = EntityUtils.toString((response).getEntity());
    				System.out.println(responseBody);

    				return status;
    			}
    		}catch(Exception e) {
    			e.printStackTrace();
    			return false;
    		}
    	}
    	logger.severe("profile key is null");
    	return false;
    }
}