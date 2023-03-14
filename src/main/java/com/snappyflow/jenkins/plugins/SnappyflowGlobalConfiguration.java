package com.snappyflow.jenkins.plugins;

import java.io.IOException;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;  
import java.time.format.DateTimeFormatter; 

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.TrustStrategy;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;


import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;

@Extension
public class SnappyflowGlobalConfiguration extends GlobalConfiguration {

	private static final Logger logger = Logger.getLogger(SnappyflowGlobalConfiguration.class.getName());
	private static final String DISPLAY_NAME = "Snappyflow Plugin";
	
	
	private static String targetApiKey = null;
	private static String targetProjectName = null;
	private static String targetAppName = null;
	private static String targetInstanceName = null;
	
	private String metricsReceiverUrl = null;
	// private static String profileKey = "BsH40P5YkC95qYvzPJpI5X5LhfdoGSWnlqu5//HWPEqM86wGoPPouIaGbnwJlKaSqNLB2b+JFXGLzhmsWNtEWCPfwoRd9sklNbULj9jFW11miEdUE/qiXuCDfkB77Xxd0udjveDoam5NhBJziZzrP0Jm8WpaYBrLR69YK6ZfjGjkzmPXlbeH2M4YYXwm4ajzAadxiv11NmlX1MFD1M0fKQHEavgPG5MuMl05nrTf6Qv8lSFlB48/5JE7AXxDU/GG8Fj0NvA+klgJD3wC6xQrFR86TueKTv2mcSZoSUgNjLD4vtXY1pMYUrD+uSGf6w2fp5+8cmxi54TLdFIp+3yGyjWTpFXUVEZ0BPzCJnenMJVBv0pTQJNwMlh2acWFo62Ctjt4zYLLDEK+ZmLjsUYSRWJdk8+Cr4pwPTP1TmqDnxm2MmEKe54y96t6HEehQZpvxyG4Up87wpwZIQQUABKSbPzjnS44V2ukDe6uCPvWUvi0vb6znv5paS3s71lfjh/VLTZst1fcYYbgH0LR7S4OIh/JJ8QmAJUeLtw+mYr/guDr7aefqQl5mVAkAKq1fc2m2USEuOVDUm76XNnJR09QJmdVIrHbGisBB2CYjEyAiuBxm7cA5YV98/D9DpbAJkqrLi3P7gFLsGAXDwAk554QHdk1di9EKNF5944eXrcVLf7KxJuD6rhW1e4kPxH+wxQH/1tLtKcE/9/BvG+goC3IWiqHq8JlyymkU/QXNwt3eFApmXKIR1t5TEELrZg5MXvEAPdpSrFf8a2un1PjT1fYUtE+Fw1CQwB+45e0Y01EmoaSN60gHlBWAXqD+0J3XD5/bYsb+eLUvzSFb9r04pS1BLgGtupAQKwCGMF2y63euw3GJw2MeRfBKwfY6CzkW7WN";
	
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	static byte[] sessionKey = null;
	private  HashMap<String,String> target = new HashMap<String,String>();
	
	public static String appendQuotes(String st) {
		return "\""+st+"\"";
	}
	
	private enum TargetType {
		KAFKA("kafka_rest_proxy"),
		ES("elasticsearch");

		TargetType(String string) {
			
		}
	}
	
	
	@DataBoundConstructor
	public SnappyflowGlobalConfiguration() {
		load();
	}
	
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }
    
    @RequirePOST
    public FormValidation doTestConnection(
            @QueryParameter("targetApiKey") final String targetApiKey,
            @QueryParameter("targetProjectName") final String targetProjectName,
            @QueryParameter("targetAppName") final String targetAppName,
            @QueryParameter("targetInstanceName") final String targetInstanceName)
            throws IOException, ServletException {

    	// TODO Write logic here to send request to Snappyflow API server and validate the API Key
    	// For testing, added null checks
    	if (StringUtils.isNotBlank(targetApiKey) && StringUtils.isNotBlank(targetProjectName) &&
    			StringUtils.isNotBlank(targetAppName) && StringUtils.isNotBlank(targetInstanceName)) {
    		logger.info("targetapikey" + targetApiKey);
    		return FormValidation.ok("API key is valid!");
    	} else {
    		return FormValidation.error("API key seems to be invalid. Check API Key/ project name/ app name/ instance name fields.");
    	}  	
    }
    
    private String getBasicAuthenticationHeader(String username, String password) {
    	String valueToEncode = username+":"+password;
    	return "Basic "+Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
    
    @RequirePOST
    public void doPostData(HashMap<String,Object> rdata)throws IOException, ServletException {

    	String targetUsername;
    	StringBuilder targetToken = new StringBuilder ();
    	StringBuilder contentType = new StringBuilder ();
    	StringBuilder targetApiUrl = new StringBuilder ();
    	
    	HashMap<String,Object> data = rdata;
      	HashMap<String, String> targetMap = decodeProfileKey();
    	    	
    	logger.info("================================================");
    	logger.info("decoded profile key:"+targetMap);     
    	    	
    	try {
    		
    		SSLContextBuilder builder = new SSLContextBuilder();
    	    builder.loadTrustMaterial(null, new TrustStrategy() {
    	        @Override
    	        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    	            return true;
    	        }
    	    });

    	    SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(builder.build(),
    	            SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
   		    		
    		HttpClient client = HttpClients
    				.custom()
    				.setSSLSocketFactory(sslSF)
    				.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustSelfSignedStrategy.INSTANCE).build())
    				.build();  		
  		    	
    		String dType="kafka_rest_proxy";
    		String dType1="elasticsearch";
    		
    		if (targetMap.get("type").equals(dType)) {
    			getKafkaHeaders(contentType, targetToken, targetApiUrl, targetMap);
    		} else if (targetMap.get("type").equals(dType1)) {
    			getESHeaders(contentType, targetToken, targetApiUrl, targetMap);
    		}
  		
    		System.out.println("Updated Headers for Kafka/ES are:---------------");
    		System.out.println("TargetAPIURL: "+targetApiUrl.toString());
    		System.out.println("Content-Type: "+contentType.toString());
    		System.out.println("Authorization: "+targetToken.toString());
    		    		
    		HttpPost post = new HttpPost(targetApiUrl.toString());
			post.setHeader("Content-Type",contentType.toString());
			post.setHeader("Authorization",targetToken.toString());
			post.setHeader( "Accept","application/vnd.kafka.v2+json");
    		
    		StringEntity data1 = new StringEntity(data.toString().replaceAll("=", ":"), "UTF-8");
    		post.setEntity( data1);

    		System.out.println("Post Headers:---------------");
    		Header[] headers = post.getAllHeaders();
    		for (Header header : headers) {    			
    			System.out.println(header.getName() + ":" + header.getValue());
    		}
    		
    		String params = post.getEntity().toString();
    		System.out.println("Post data: "+params);

    		System.out.println("Data is here:---------------");
    		String bg = new String(data1.getContent().readAllBytes(),StandardCharsets.UTF_8);
     		System.out.println(bg);

    		HttpResponse response = client.execute(post);
    		int responseCode = response.getStatusLine().getStatusCode();

    		System.out.println("\nSending 'POST' request to URL : " + targetApiUrl);
    		logger.info("================================================");
    		System.out.println("Response Code : " + responseCode);

    		 String responseBody = EntityUtils.toString((response).getEntity());
    		 System.out.println(responseBody); 		
    		  		
       	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    public static HashMap<String, Object> addHeaders(String type){
    	HashMap<String, Object> hMap = new HashMap<String, Object>();    	
    
    	hMap.put(appendQuotes("_tag_projectName"), appendQuotes(getTargetProjectName()));
    	hMap.put(appendQuotes("_tag_appName"), appendQuotes(getTargetAppName()));
    	hMap.put(appendQuotes("_tag_instanceName"), appendQuotes(getTargetInstanceName()));
    	hMap.put(appendQuotes("_plugin"),appendQuotes("Jenkins"));
    	hMap.put(appendQuotes("time"), System.currentTimeMillis());
    	hMap.put(appendQuotes("document_type"), appendQuotes(type));    	    	
    	
    	return hMap;
    }
    
    public HashMap<String, String> decodeProfileKey() {
    	try {
    		SecretKey secretKey = new SecretKeySpec("SnappyFlow123456".getBytes("UTF-8"), ALGORITHM);
    		sessionKey = Base64.getDecoder().decode(targetApiKey);
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
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return target;
    }

    public void getKafkaHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl, HashMap<String, String> targetMap) {
   	
       	targetApiUrl.append(targetMap.get("url")+"/topics/"+targetMap.get("profile_id"));
    	System.out.println("targetApi URL for Kafka is: " + targetApiUrl.toString());
    	
    	contentType.append("application/vnd.kafka.json.v2+json");	    	
   		targetToken.append(targetMap.get("token"));
    }
    
    public void getESHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl, HashMap<String, String> targetMap) {
    	String targetUsername = targetMap.get("username");
    	targetToken.append(getBasicAuthenticationHeader(targetUsername, targetMap.get("password")));
    	contentType.append("application/json");

    	String ds_protocol = targetMap.get("protocol");
    	String ds_host = targetMap.get("host");
    	String ds_port = targetMap.get("port");    	  	
    	String ds_index = "metric-"+targetMap.get("profile_id")+"-"+getTargetProjectName()+"-$_write";
    	String ds_type;

    	if (targetMap.get("es_7x") == "true")
    		ds_type = "_doc";
    	else
    		ds_type = "_doc";

    	targetApiUrl.append(ds_protocol+"://"+ds_host+":"+ds_port+"/"+ds_index+"/"+ds_type);    		
    	System.out.println("targetApi URL for ES is:"+targetApiUrl.toString());
    }
	
	public static String getTargetApiKey() {
		return targetApiKey;
	}

	public void setTargetApiKey(String targetApiKey) {
		this.targetApiKey = targetApiKey;
	}

	public static String getTargetProjectName() {
		return targetProjectName;
	}

	public void setTargetProjectName(String targetProjectName) {
		this.targetProjectName = targetProjectName;		
	}
	
	public static String getTargetAppName() {
		return targetAppName;
	}

	public void setTargetAppName(String targetAppName) {
		this.targetAppName = targetAppName;
	}
	
	public static String getTargetInstanceName() {
		return targetInstanceName;
	}

	public void setTargetInstanceName(String targetInstanceName) {
		this.targetInstanceName = targetInstanceName;
	}
	
	public String getMetricsReceiverUrl() {
		return metricsReceiverUrl;
	}

	public void setMetricsReceiverUrl(String metricsReceiverUrl) {
		this.metricsReceiverUrl = metricsReceiverUrl;
	}

}
