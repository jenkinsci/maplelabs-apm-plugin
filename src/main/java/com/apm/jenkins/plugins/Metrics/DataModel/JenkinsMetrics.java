package com.apm.jenkins.plugins.Metrics.DataModel;

import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.Client;
import com.apm.jenkins.plugins.Client.Snappyflow.SnappyFlow;
import com.apm.jenkins.plugins.Metrics.interfaces.PublishMetrics;

import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.model.Project;
import jenkins.model.Jenkins;

public class JenkinsMetrics implements PublishMetrics {

    private int plugins;
    private int projects;
    private String hostName;
    private int activePlugins;
    private int failedPlugins;
    private int inactivePlugins;
    private int updateablePlugins;
    private static final Logger logger = Logger.getLogger(JenkinsMetrics.class.getName());

    private void clear() {
        activePlugins = 0;
        inactivePlugins = 0;
        setPlugins(0);
        updateablePlugins = 0;
        setProjects(0);
        setHostName(null);
        setFailedPlugins(0);
    }

    public int getPlugins() {
        return this.plugins;
    }

    public void setPlugins(int plugins) {
        this.plugins = plugins;
    }

    public int getProjects() {
        return this.projects;
    }

    public void setProjects(int projects) {
        this.projects = projects;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getActivePlugins() {
        return this.activePlugins;
    }

    public void incrementActivePlugins() {
        this.activePlugins++;
    }

    public int getFailedPlugins() {
        return this.failedPlugins;
    }

    public void setFailedPlugins(int failedPlugins) {
        this.failedPlugins = failedPlugins;
    }

    public int getInactivePlugins() {
        return this.inactivePlugins;
    }

    public void incrementInactivePlugins() {
        this.inactivePlugins++;
    }

    public int getUpdateablePlugins() {
        return this.updateablePlugins;
    }

    public void incrementUpdateablePlugins() {
        this.updateablePlugins++;
    }

    /**
     * This function will set Jenkins properties and send details to client
     * 
     * @param details
     */
    @Override
    public void sendMetrics(Object details) {
        clear();
        Jenkins instance = (Jenkins) details;
        if (instance == null) {
            logger.severe("No Jenkins instance");
            return;
        }
        ;
        setProjects(instance.getAllItems(Project.class).size());
        PluginManager pluginManager = instance.getPluginManager();
        if (pluginManager != null) {
            List<PluginWrapper> plugins = pluginManager.getPlugins();
            setPlugins(plugins.size());
            setFailedPlugins(pluginManager.getFailedPlugins().size());

            for (PluginWrapper w : plugins) {
                if (w.hasUpdate())
                    incrementUpdateablePlugins();
                if (w.isActive())
                    incrementActivePlugins();
                else
                    incrementInactivePlugins();
            }
        } else {
            logger.warning("Plugin manager not found");
        }
        setHostName(APMUtil.getHostname(null));

        HashMap<String, Object> systemDict = SnappyFlow.getSnappyflowTags("systemStat");
        systemDict.put("hostName", getHostName());
        systemDict.put("num_projects", getProjects());
        systemDict.put("num_plugins", getPlugins());
        systemDict.put("num_active_plugins", getActivePlugins());
        systemDict.put("num_failed_plugins", getFailedPlugins());
        systemDict.put("num_inactive_plugins", getInactivePlugins());
        systemDict.put("num_plugin_updateable", getUpdateablePlugins());

        Client communicationClient = APMUtil.getAPMGlobalDescriptor().getDestinationClient();
        if (communicationClient != null) {
            communicationClient.transmitData(systemDict);
            clear();
        } else {
            logger.warning("Destination client is empty");
        }
    }

}
