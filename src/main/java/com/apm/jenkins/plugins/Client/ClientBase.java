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
    public static APMClient getClient(String projectName, String appName, String instName) {
    	if(testClient != null){
    		// Only used for tests
    		return testClient;
    	}
    	return APMHttpClient.getInstance(projectName, appName, instName);
    }

    public static APMClient getClient() {
    	if(testClient != null){
    		// Only used for tests
    		return testClient;
    	}
    	APMGlobalConfiguration descriptor = APMUtil.getAPMGlobalDescriptor();
    	String appName = null;
    	String instName = null;
    	String projectName = null;
    	
    	if(descriptor != null){
    		appName = descriptor.getTargetAppName();
    		instName = descriptor.getTargetInstanceName();
    		projectName = descriptor.getTargetProjectName();
    	}
    	return ClientBase.getClient(projectName, appName, instName);
    }    
}