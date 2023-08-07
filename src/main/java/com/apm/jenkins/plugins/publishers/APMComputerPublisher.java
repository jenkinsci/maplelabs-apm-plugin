
package com.apm.jenkins.plugins.publishers;

import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Computer;
import jenkins.model.Jenkins;
import hudson.model.PeriodicWork;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.ClientBase;
import com.apm.jenkins.plugins.metrics.NodeMetrics;
import com.apm.jenkins.plugins.interfaces.APMClient;

@Extension
public class APMComputerPublisher extends PeriodicWork {
	
    private static final NodeMetrics nodeMetrics = new NodeMetrics();
    private static final Logger logger = Logger.getLogger(APMComputerPublisher.class.getName());

    @Override
    public long getRecurrencePeriod() {
        return APMUtil.publisherTime;
    }

    @Override
    protected void doRun() throws Exception {
        try {
            logger.fine("Computing Node metrics");
            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            if(client == null) return;
                                    
            Jenkins jenkins = Jenkins.getInstanceOrNull();
            
            if(jenkins != null){
                Computer[] computers = jenkins.getComputers();
                if(computers != null) {
                    nodeMetrics.setDetails(computers);
                }
            } else {
            	logger.fine("Instance is null and couldn't retrieve computers.");
            }         
            client.postMetric(nodeMetrics.getDetails(), "metric");                       
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
    }

}
