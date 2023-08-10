package com.apm.jenkins.plugins.Metrics.DataModel;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.Communication;
import com.apm.jenkins.plugins.Metrics.interfaces.PublishMetrics;

import hudson.node_monitors.ResponseTimeMonitor.Data;
import hudson.node_monitors.SwapSpaceMonitor.MemoryUsage2;
import hudson.node_monitors.DiskSpaceMonitorDescriptor.DiskSpace;

import hudson.model.Computer;

public class NodeMetrics implements PublishMetrics {
    private int numNodes;
    private int numNodesOnline;
    private int numNodesOffline;
    ArrayList<HashMap<String,Object>> compuerList;

    // clear all values
    private void clear() {
        setNumNodes(0);
        setNumNodesOnline(0);
        setNumNodesOffline(0);
        compuerList = null;
    }

    public int getNumNodes() {
        return this.numNodes;
    }

    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }

    public int getNumNodesOnline() {
        return this.numNodesOnline;
    }

    public void setNumNodesOnline(int numNodesOnline) {
        this.numNodesOnline = numNodesOnline;
    }

    public int getNumNodesOffline() {
        return this.numNodesOffline;
    }

    public void setNumNodesOffline(int numNodesOffline) {
        this.numNodesOffline = numNodesOffline;
    }

    public ArrayList<HashMap<String,Object>> getComputerDetails() {
        return this.compuerList;
    }

    public void addComputerDetails(HashMap<String, Object> computerDetails) {
        if(compuerList == null) compuerList = new ArrayList<HashMap<String,Object>>();
        this.compuerList.add(computerDetails);
    }

    /**
     * This function will set node properties and send details to client
     * @param details
     */
    @Override
    public void sendMetrics(Object details) {
        if(details instanceof Computer[]) {
            Computer[] computerList = (Computer[])details;
            int nodeOnline = 0, nodeOffline = 0;
            setNumNodes(computerList.length);

            for (Computer computer : computerList) {
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
                addComputerDetails(computerMap);
            }
            setNumNodesOnline(nodeOnline);
            setNumNodesOffline(nodeOffline);
        } else return;

        HashMap<String, Object> computerDetails =APMUtil.getSnappyflowTags("nodeStat");
        computerDetails.put("num_nodes", getNumNodes());
        computerDetails.put("computers", getComputerDetails());
        computerDetails.put("num_nodes_online", getNumNodesOnline());
        computerDetails.put("num_nodes_offline", getNumNodesOffline());
        
        Communication communicationClient = APMUtil.getAPMGlobalDescriptor().getCommunicationClient();
        if(communicationClient != null) {
            communicationClient.transmit(computerDetails);
            clear();
        }
    }
    
}
