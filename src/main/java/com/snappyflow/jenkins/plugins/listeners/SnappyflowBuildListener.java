package com.snappyflow.jenkins.plugins.listeners;

import java.util.logging.Logger;

import javax.annotation.Nonnull;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

@Extension
public class SnappyflowBuildListener extends RunListener<Run> {
	
	private static final Logger logger = Logger.getLogger(SnappyflowBuildListener.class.getName());
	
	@Override
    public void onInitialize(Run run) {
		logger.info("Inside onInitialize method");
	}
	
	@Override
    public void onStarted(Run run, TaskListener listener) {
		logger.info("Inside onStarted method");
	}
	
	@Override
    public void onCompleted(Run run, @Nonnull TaskListener listener) {
		logger.info("Inside onCompleted method");
	}
	
	@Override
    public void onFinalized(Run run) {
		logger.info("Inside onFinalized method");
	}
	
    @Override
    public void onDeleted(Run run) {
    	logger.info("Inside onDeleted method");
    }

}
