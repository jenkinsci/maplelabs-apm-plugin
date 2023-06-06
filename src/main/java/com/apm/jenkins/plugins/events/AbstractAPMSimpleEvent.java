package com.apm.jenkins.plugins.events;
import com.apm.jenkins.plugins.APMUtil;

public abstract class AbstractAPMSimpleEvent extends AbstractAPMEvent{
	
	public AbstractAPMSimpleEvent() {
        setHost(APMUtil.getHostname(null));
        setJenkinsUrl(APMUtil.getJenkinsUrl());
        setDate(APMUtil.currentTimeMillis() / 1000);
        setSnappyflowTags(APMUtil.getSnappyflowTags("Event"));        
    }

}
