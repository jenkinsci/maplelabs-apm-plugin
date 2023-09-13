package com.apm.jenkins.plugins.Metrics.Data;

import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

import com.apm.jenkins.plugins.Utils;
import com.apm.jenkins.plugins.Client.IClient;
import com.apm.jenkins.plugins.Client.Snappyflow.SnappyFlow;
import com.apm.jenkins.plugins.Metrics.interfaces.IPublishMetrics;

import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.model.Project;
import jenkins.model.Jenkins;

public class JenkinsMetricsImpl implements IPublishMetrics {

    private int plugins;
    private int projects;
    private String hostName;
    private int activePlugins;
    private int failedPlugins;
    private int inactivePlugins;
    private int updateablePlugins;
    private static final Logger logger = Logger.getLogger(JenkinsMetricsImpl.class.getName());

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
    public HashMap<String, Object> collectMetrics(Object details) {
        clear();
        Jenkins instance = (Jenkins) details;
        if (instance == null) {
            logger.severe("No Jenkins instance");
            return null;
        }
        ;
        setProjects(instance.getAllItems(Project.class).size());

        PluginManager pluginManager = instance.getPluginManager();
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
        
        setHostName(Utils.getHostName(null));

        HashMap<String, Object> systemDict = SnappyFlow.getSnappyflowTags("systemMetrics");
        systemDict.put("hostName", getHostName());
        systemDict.put("project_total", getProjects());
        systemDict.put("plugin_total", getPlugins());
        systemDict.put("plugins_active", getActivePlugins());
        systemDict.put("plugins_failed", getFailedPlugins());
        systemDict.put("plugins_inactive", getInactivePlugins());
        systemDict.put("plugins_updatable", getUpdateablePlugins());
        return systemDict; 
    }

}
