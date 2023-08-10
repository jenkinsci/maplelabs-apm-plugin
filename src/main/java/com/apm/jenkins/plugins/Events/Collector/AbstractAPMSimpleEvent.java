package com.apm.jenkins.plugins.Events.Collector;
import com.apm.jenkins.plugins.APMUtil;

public abstract class AbstractAPMSimpleEvent extends AbstractAPMEvent{
	
	public AbstractAPMSimpleEvent() {
        setHost(APMUtil.getHostname(null));
        setDate(APMUtil.currentTimeMillis() / 1000);
    }

}
