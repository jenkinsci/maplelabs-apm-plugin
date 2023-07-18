
package com.apm.jenkins.plugins.publishers;

import java.util.*;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import hudson.Extension;
import hudson.model.Computer;
import jenkins.model.Jenkins;
import hudson.model.PeriodicWork;
import hudson.node_monitors.ResponseTimeMonitor.Data;
import hudson.node_monitors.SwapSpaceMonitor.MemoryUsage2;
import hudson.node_monitors.DiskSpaceMonitorDescriptor.DiskSpace;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.ClientBase;
import com.apm.jenkins.plugins.interfaces.APMClient;

@Extension
public class APMComputerPublisher extends PeriodicWork {
	
    private static final long RECURRENCE_PERIOD = TimeUnit.SECONDS.toMillis(30);
    private static final Logger logger = Logger.getLogger(APMComputerPublisher.class.getName());

    @Override
    public long getRecurrencePeriod() {
        return RECURRENCE_PERIOD;
    }

    @Override
    protected void doRun() throws Exception {
        try {
            logger.fine("doRun called: Computing Node metrics");

            // Adding the collected metrics to dictionary
            HashMap<String, Object> nodeStats_dict = APMUtil.getSnappyflowTags("nodeStats");      

            long nodeOnline = 0;
            long nodeOffline = 0;
            
            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            if(client == null) return;
                                    
            Jenkins jenkins = Jenkins.getInstanceOrNull();
            Computer[] computers = new Computer[0];
            if(jenkins != null){
                computers = jenkins.getComputers();
            } else {
            	logger.fine("Instance is null and couldn't retrieve computers.");
            }         
            
            nodeStats_dict.put("num_nodes", computers.length);
            ArrayList<Object> nodeArray = new ArrayList<>(); 
            for (Computer computer : computers) {
                if (computer.isOnline()) nodeOnline++;                        
                if (computer.isOffline()) nodeOffline++; 
                HashMap<String, Object> computerMap = new HashMap<String, Object>();
                computerMap.put("free", computer.countIdle());
                computerMap.put("inUse",  computer.countBusy());
                computerMap.put("nodeName",  computer.getDisplayName());
                computerMap.put("connectTime", computer.getConnectTime());
                computerMap.put("executorCount", computer.countExecutors());
                Map<String,Object> moniter  = computer.getMonitorData();
                if(moniter != null) {
                    // RAM, SWAP 
                    MemoryUsage2 memory = (MemoryUsage2)moniter.get("hudson.node_monitors.SwapSpaceMonitor");
                    computerMap.put("swap_total",memory != null ? memory.getTotalSwapSpace() : null);
                    computerMap.put("swap_available",memory != null ? memory.getAvailableSwapSpace() : null);
                    computerMap.put("memory_total",memory != null ? memory.getTotalPhysicalMemory() : null);
                    computerMap.put("memory_available",memory != null ? memory.getAvailablePhysicalMemory() : null);

                    // Disk
                    DiskSpace diskSpaceMonitor = (DiskSpace) moniter.get("hudson.node_monitors.DiskSpaceMonitor");
                    computerMap.put("disk_path", diskSpaceMonitor  != null? diskSpaceMonitor.getPath() : null);
                    computerMap.put("disk_available",diskSpaceMonitor  != null? diskSpaceMonitor.size : null);

                    // Temp
                    diskSpaceMonitor = (DiskSpace)moniter.get("hudson.node_monitors.TemporarySpaceMonitor");
                    computerMap.put("temp_path", diskSpaceMonitor != null? diskSpaceMonitor.getPath() : null);
                    computerMap.put("temp_available",diskSpaceMonitor != null ? diskSpaceMonitor.size : null);

                    // Response
                    Data responseData = (Data)moniter.get("hudson.node_monitors.ResponseTimeMonitor");
                    computerMap.put("response_time",responseData != null ? responseData.getAverage() : null);

                    //Architect
                    computerMap.put("arch",moniter.get("hudson.node_monitors.ArchitectureMonitor"));
                }
                nodeArray.add(computerMap);
            }
            nodeStats_dict.put("computers", nodeArray);
            nodeStats_dict.put("num_node_online", nodeOnline);
            nodeStats_dict.put("num_nodes_offline", nodeOffline);

            client.postSnappyflowMetric(nodeStats_dict, "metric");                       
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
    }

}
