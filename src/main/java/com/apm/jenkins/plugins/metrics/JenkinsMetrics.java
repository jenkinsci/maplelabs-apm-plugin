package com.apm.jenkins.plugins.metrics;

import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

import com.apm.jenkins.plugins.APMUtil;

import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.model.Project;
import jenkins.model.Jenkins;

public class JenkinsMetrics implements StatDetails {

    private int plugins;
    private int projects;
    private String hostName;
    private int activePlugins;
    private int failedPlugins;
    private int inactivePlugins;
    private int updateablePlugins;

    private void clear() {
        setPlugins(-1);
        setProjects(-1);
        activePlugins = -1;
        setFailedPlugins(-1);
        inactivePlugins = -1;
        updateablePlugins = -1;
        setHostName("");
    }
    private int getPlugins() {
        return this.plugins;
    }

    private void setPlugins(int plugins) {
        this.plugins = plugins;
    }

    private int getProjects() {
        return this.projects;
    }

    private void setProjects(int projects) {
        this.projects = projects;
    }

    private String getHostName() {
        return this.hostName;
    }

    private void setHostName(String hostName) {
        this.hostName = hostName;
    }

    private int getActivePlugins() {
        return this.activePlugins;
    }

    private void incrementActivePlugins() {
        if(activePlugins < 0) activePlugins = 0;
        this.activePlugins++;
    }

    private int getFailedPlugins() {
        return this.failedPlugins;
    }

    private void setFailedPlugins(int failedPlugins) {
        this.failedPlugins = failedPlugins;
    }

    private int getInactivePlugins() {
        return this.inactivePlugins;
    }

    private void incrementInactivePlugins() {
        if(inactivePlugins < 0) inactivePlugins = 0;
        this.inactivePlugins++;
    }

    private int getUpdateablePlugins() {
        return this.updateablePlugins;
    }

    private void incrementUpdateablePlugins() {
        if(updateablePlugins < 0) updateablePlugins = 0;
        this.updateablePlugins++;
    }

    /**
     * This function will set Jenkins properties
     * @param details
     */
    @Override
    public void setDetails(Object details) {
        clear();
        Jenkins instance = (Jenkins)details;
        if (instance == null) return;
        setProjects(instance.getAllItems(Project.class).size());
        PluginManager pluginManager = instance.getPluginManager();
        if(pluginManager != null) {
            List<PluginWrapper> plugins = pluginManager.getPlugins();
            setPlugins(plugins.size());
            setFailedPlugins(pluginManager.getFailedPlugins().size());
             
            for (PluginWrapper w : plugins) {
                if (w.hasUpdate()) incrementUpdateablePlugins();
    
                if (w.isActive()) incrementActivePlugins();
                else incrementInactivePlugins();
            }
        }
        setHostName(APMUtil.getHostname(null));
    }

    /**
     * This function will assembel jenkins details and return as a HashMap
     * @return
     */
    @Override
    public HashMap<String, Object> getDetails() {
        HashMap<String, Object> systemDict = new HashMap<>();
        systemDict.put("hostName", getHostName());
        systemDict.put("num_projects", getProjects());
		systemDict.put("num_plugins", getPlugins());
		systemDict.put("num_active_plugins", getActivePlugins());
		systemDict.put("num_failed_plugins", getFailedPlugins());
		systemDict.put("num_inactive_plugins", getInactivePlugins());
		systemDict.put("num_plugin_with_update", getUpdateablePlugins());
        return systemDict;
    }
    
}
