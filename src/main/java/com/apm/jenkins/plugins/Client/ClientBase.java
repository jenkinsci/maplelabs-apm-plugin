package com.apm.jenkins.plugins.Client;


import com.apm.jenkins.plugins.interfaces.APMClient;

public class ClientBase {
    private static APMClient testClient;

    public static APMClient getClient() {
    	if(testClient != null){
    		// Only used for tests
    		return testClient;
    	}
    	return ClientBase.getClient();
    }    
}