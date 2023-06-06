package com.apm.jenkins.plugins.Client;

import com.apm.jenkins.plugins.APMGlobalConfiguration;
import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.interfaces.APMClient;

public class ClientBase {
    private static APMClient testClient;

    public static void setTestClient(APMClient testClient){
        // Only used for tests
        ClientBase.testClient = testClient;
    }

    // This can be overloaded to support destinations other than snappyflow over HTTP in future.
    public static APMClient getClient(APMClient.ClientType type, String profileKey, String projectName, String appName, String instName, String destination) {
    	if(testClient != null){
    		// Only used for tests
    		return testClient;
    	}
    	switch(type){
    	case HTTP:
    		return APMHttpClient.getInstance(profileKey, projectName, appName, instName, destination);
    	default:
    		return null;
    	}
    }

    public static APMClient getClient() {
    	if(testClient != null){
    		// Only used for tests
    		return testClient;
    	}
    	APMGlobalConfiguration descriptor = APMUtil.getAPMGlobalDescriptor();
    	String profileKey = null;
    	String projectName = null;
    	String appName = null;
    	String instName = null;
    	String destination = null;
    	APMClient.ClientType cType = APMClient.ClientType.HTTP;;
    	
    	if(descriptor != null){
    		profileKey = descriptor.getTargetApiKey();
    		projectName = descriptor.getTargetProjectName();
    		appName = descriptor.getTargetAppName();
    		instName = descriptor.getTargetInstanceName();
    		if (descriptor.getTargetDestination().equals("Snappyflow")){
    		    cType = APMClient.ClientType.HTTP;
    		}
    	}
    	return ClientBase.getClient(cType, profileKey, projectName, appName, instName, destination);
    }    
}