package io.jenkins.plugins.Metrics.Data;

import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import io.jenkins.plugins.Utils;
import io.jenkins.plugins.Client.IClient;
import io.jenkins.plugins.Client.Snappyflow.SnappyFlow;
import io.jenkins.plugins.Metrics.interfaces.IPublishMetrics;

import hudson.node_monitors.ResponseTimeMonitor.Data;
import hudson.node_monitors.SwapSpaceMonitor.MemoryUsage2;
import hudson.node_monitors.DiskSpaceMonitorDescriptor.DiskSpace;

import hudson.model.Computer;

public class NodeMetricsImpl implements IPublishMetrics {
    private int numNodes;
    private int numNodesOnline;
    private int numNodesOffline;
    private ArrayList<HashMap<String, Object>> compuerList;
    private static final Logger logger = Logger.getLogger(NodeMetricsImpl.class.getName());

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

    public ArrayList<HashMap<String, Object>> getComputerDetails() {
        return this.compuerList;
    }

    public void addComputerDetails(HashMap<String, Object> computerDetails) {
        if (compuerList == null)
            compuerList = new ArrayList<HashMap<String, Object>>();
        this.compuerList.add(computerDetails);
    }
    //function to convert size to gb
    public String convertLongToGB(long space) {
        space /= 1024L;   // convert to KB
        space /= 1024L;   // convert to MB
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(new BigDecimal(space).scaleByPowerOfTen(-3));
    }

    /**
     * This function will set node properties and send details to client
     * 
     * @param details
     */
    @Override
    public HashMap<String, Object> collectMetrics(Object details) {
        clear();
        if (details instanceof Computer[]) {
            Computer[] computerList = (Computer[]) details;
            int nodeOnline = 0, nodeOffline = 0;
            setNumNodes(computerList.length);

            for (Computer computer : computerList) {
                if (computer.isOnline())
                    nodeOnline++;
                if (computer.isOffline())
                    nodeOffline++;

                HashMap<String, Object> computerMap = new HashMap<String, Object>();
                computerMap.put("free", computer.countIdle());
                computerMap.put("inUse", computer.countBusy());
                computerMap.put("nodeName", computer.getDisplayName());
                computerMap.put("connectTime", computer.getConnectTime());
                computerMap.put("executorCount", computer.countExecutors());

                Map<String, Object> moniter = computer.getMonitorData();
                if (moniter != null) {
                    // RAM, SWAP
                    MemoryUsage2 memory = (MemoryUsage2) moniter.get("hudson.node_monitors.SwapSpaceMonitor");
                    computerMap.put("swap_total_in_GB", memory != null ? Float.parseFloat(convertLongToGB(memory.getTotalSwapSpace())) : null);
                    computerMap.put("swap_available_in_GB", memory != null ? Float.parseFloat(convertLongToGB(memory.getAvailableSwapSpace())) : null);
                    computerMap.put("memory_total_in_GB", memory != null ? Float.parseFloat(convertLongToGB(memory.getTotalPhysicalMemory())) : null);
                    computerMap.put("memory_available_in_GB", memory != null ? Float.parseFloat(convertLongToGB(memory.getAvailablePhysicalMemory())) : null);

                    // Disk
                    DiskSpace diskSpaceMonitor = (DiskSpace) moniter.get("hudson.node_monitors.DiskSpaceMonitor");
                    computerMap.put("disk_path", diskSpaceMonitor != null ? diskSpaceMonitor.getPath() : null);
                    computerMap.put("disk_available_in_GB", diskSpaceMonitor != null ? Float.parseFloat(convertLongToGB(diskSpaceMonitor.size)) : null);

                    // Temp
                    diskSpaceMonitor = (DiskSpace) moniter.get("hudson.node_monitors.TemporarySpaceMonitor");
                    computerMap.put("temp_path", diskSpaceMonitor != null ? diskSpaceMonitor.getPath() : null);
                    computerMap.put("temp_available_in_GB", diskSpaceMonitor != null ? Float.parseFloat(convertLongToGB(diskSpaceMonitor.size)) : null);

                    // Response
                    Data responseData = (Data) moniter.get("hudson.node_monitors.ResponseTimeMonitor");
                    computerMap.put("response_time", responseData != null ? responseData.getAverage() : null);

                    // Architect
                    computerMap.put("arch", moniter.get("hudson.node_monitors.ArchitectureMonitor"));
                } else {
                    logger.warning("Monitor object not available");
                }
                addComputerDetails(computerMap);
            }
            setNumNodesOnline(nodeOnline);
            setNumNodesOffline(nodeOffline);
        } else {
            logger.severe("No Computer instance");
            return null;
        }

        HashMap<String, Object> computerDetails = SnappyFlow.getSnappyflowTags("nodeMetircs");
        computerDetails.put("nodes_total", getNumNodes());
        computerDetails.put("computers", getComputerDetails());
        computerDetails.put("nodes_online", getNumNodesOnline());
        computerDetails.put("nodes_offline", getNumNodesOffline());

        return computerDetails;
    }

}
