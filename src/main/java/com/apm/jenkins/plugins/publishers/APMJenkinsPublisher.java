package com.apm.jenkins.plugins.publishers;

import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import hudson.Extension;
import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.model.Project;
import jenkins.model.Jenkins;
import hudson.model.PeriodicWork;

import com.apm.jenkins.plugins.APMUtil;
import com.apm.jenkins.plugins.Client.*;
import com.apm.jenkins.plugins.DataModel.PluginData;
import com.apm.jenkins.plugins.interfaces.APMClient;

/**
 * This class registers a {@link PeriodicWork} with Jenkins to run periodically in order to enable
 * us to compute metrics related to Jenkins level metrics.
 */
@Extension
public class APMJenkinsPublisher extends PeriodicWork {

    private static final Logger logger = Logger.getLogger(APMJenkinsPublisher.class.getName());

    private static final long RECURRENCE_PERIOD = TimeUnit.MINUTES.toMillis(1);

    @Override
    public long getRecurrencePeriod() {
        return RECURRENCE_PERIOD;
    }

    @Override
    protected void doRun() throws Exception {
        try {
            logger.fine("doRun called: Computing Jenkins metrics");

            // Get APM Client Instance
            APMClient client = ClientBase.getClient();
            String hostname = APMUtil.getHostname(null);
            
            long projectCount = 0;
            Jenkins instance = Jenkins.getInstanceOrNull();
            if (instance == null) {
                logger.fine("Could not retrieve projects");
            } else {
                projectCount = instance.getAllItems(Project.class).size();
            }

            PluginData pluginData = collectPluginData(instance);
            
            // Adding the collected metrics to dictionary
            HashMap<String, Object> systemStats_dict = APMUtil.getSnappyflowTags("systemStats");
                        
            systemStats_dict.put("hostName", hostname);
            systemStats_dict.put("num_projects", projectCount);
			systemStats_dict.put("num_plugins", pluginData.getCount());
			systemStats_dict.put("num_active_plugins", pluginData.getActive());
			systemStats_dict.put("num_failed_plugins", pluginData.getFailed());
			systemStats_dict.put("num_inactive_plugins", pluginData.getInactive());
			systemStats_dict.put("num_plugin_with_update", pluginData.getUpdatable());
    
			logger.info("System stats dict: " + systemStats_dict);
			// client.postSnappyflowMetric(systemStats_dict, "metric");
            
            
        } catch (Exception e) {
            APMUtil.severe(logger, e, "Failed to compute and send Jenkins metrics");
        }
    }
    
    
    private PluginData collectPluginData(Jenkins instance) {
        PluginData.Builder pluginData = PluginData.newBuilder();

        if (instance == null) {
            logger.fine("Could not retrieve plugins");
            return pluginData.build();
        }

        PluginManager pluginManager = instance.getPluginManager();
        List<PluginWrapper> plugins = pluginManager.getPlugins();
        pluginData.withCount(plugins.size()).withFailed(pluginManager.getFailedPlugins().size());
        for (PluginWrapper w : plugins) {
            if (w.hasUpdate()) {
                pluginData.incrementUpdatable();
            }
            if (w.isActive()) {
                pluginData.incrementActive();
            } else {
                pluginData.incrementInactive();
            }
        }
        return pluginData.build();
    } 
}
