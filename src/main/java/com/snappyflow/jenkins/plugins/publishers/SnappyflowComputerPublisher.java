
package com.snappyflow.jenkins.plugins;

import java.util.concurrent.TimeUnit;
import java.util.*;
import java.util.logging.Logger;

import java.text.DateFormat;  
import java.text.SimpleDateFormat;  

import hudson.Extension;
import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.model.Computer;
import hudson.model.PeriodicWork;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
// import hudson.node_monitors.DiskSpaceMonitorDescriptor.DiskSpace;

@Extension
public class SnappyflowComputerPublisher extends PeriodicWork {
	
    private static final Logger logger = Logger.getLogger(SnappyflowComputerPublisher.class.getName());

    private static final long RECURRENCE_PERIOD = TimeUnit.SECONDS.toMillis(30);

    @Override
    public long getRecurrencePeriod() {
        return RECURRENCE_PERIOD;
    }

    @Override
    protected void doRun() throws Exception {
        try {
            logger.fine("doRun called: Computing Node metrics");

            long nodeCount = 0;
            long nodeOffline = 0;
            long nodeOnline = 0;
            long projectCount = 0;
            long plugincount = 0;
            long failed = 0;
            long active = 0;
            long inactive = 0;
            long updatable = 0;
                                    
            Jenkins jenkins = Jenkins.getInstanceOrNull();
            Computer[] computers = new Computer[0];
            if(jenkins != null){
                computers = jenkins.getComputers();
            } else {
            	logger.fine("Instance is null and couldn't retrieve computers.");
            }
            
            for (Computer computer : computers) {
                nodeCount++;
                if (computer.isOffline()) {
                    nodeOffline++;
                    
                }   
                if (computer.isOnline()) {
                    nodeOnline++;
                    
                }
                int executorCount = computer.countExecutors();
                int inUse = computer.countBusy();
                int free = computer.countIdle();
                long connectTime = computer.getConnectTime();
                String nodeName = computer.getDisplayName();
                                
                HashMap<String, Object> nodeStats_dict = SnappyflowGlobalConfiguration.addHeaders("nodeStats");                               
                // Adding the collected metrics to dictionary
                nodeStats_dict.put("executorCount", executorCount);
                nodeStats_dict.put("inUse", inUse);
                nodeStats_dict.put("free", free);
                nodeStats_dict.put("connectTime", connectTime);
                nodeStats_dict.put("nodeName", nodeName);
                
                logger.info("Node stats dict: " +  nodeStats_dict);                                       
                
            }
            
            PluginManager pluginManager = jenkins.getPluginManager();
            failed = pluginManager.getFailedPlugins().size();
            
            List<PluginWrapper> plugins = pluginManager.getPlugins();
            plugincount = plugins.size();
            
            
            for (PluginWrapper pwr : plugins) {
                if (pwr.hasUpdate()) {
                    updatable++;
                }
                if (pwr.isActive()) {
                	active++;
                } else {
                	inactive++;
                }
            }
            
            SnappyflowGlobalConfiguration SfGC = new SnappyflowGlobalConfiguration();
            if(StringUtils.isNotBlank(SfGC.getTargetApiKey())) {
				// Adding the collected metrics to dictionary
				HashMap<String, Object> systemStats_dict = SnappyflowGlobalConfiguration.addHeaders("systemStats");
				systemStats_dict.put(SfGC.appendQuotes("projectCount"), projectCount);
				systemStats_dict.put(SfGC.appendQuotes("plugincount"), plugincount);
				systemStats_dict.put(SfGC.appendQuotes("Active Plugins"), active);
				systemStats_dict.put(SfGC.appendQuotes("Failed Plugins"), failed);
				systemStats_dict.put(SfGC.appendQuotes("inactive Plugins"), inactive);
				systemStats_dict.put(SfGC.appendQuotes("Updatable Plugins"), updatable);
				systemStats_dict.put(SfGC.appendQuotes("nodeCount"), nodeCount);
				systemStats_dict.put(SfGC.appendQuotes("nodeOffline"), nodeOffline);
				systemStats_dict.put(SfGC.appendQuotes("nodeOnline"), nodeOnline);

				logger.info("System stats dict: " + systemStats_dict);
				SfGC.doPostData(systemStats_dict);
            }
                                  
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}
