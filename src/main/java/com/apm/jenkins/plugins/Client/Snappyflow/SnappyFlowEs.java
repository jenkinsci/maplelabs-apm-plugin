package com.apm.jenkins.plugins.Client.Snappyflow;

import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.kohsuke.stapler.interceptor.RequirePOST;

import com.apm.jenkins.plugins.APMUtil;

public class SnappyFlowEs extends SnappyFlow {

	private static final Logger logger = Logger.getLogger(SnappyFlowEs.class.getName());

	/**
	 * This function will set http header
	 * 
	 * @param contentType
	 * @param targetToken
	 * @param targetApiUrl
	 */
	@Override
	protected void getHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl) {
		String projName = APMUtil.getAPMGlobalDescriptor().getTargetProjectName();

		if (projName == null) {
			logger.severe("ProjectName in is null, please fill the required details of snappyflow in Manage Jenkins");
			return;
		}
		String targetUsername = APMUtil.getAPMGlobalDescriptor().getTargetESUserName();
		String targetPassword = APMUtil.getAPMGlobalDescriptor().getTargetESPassword();

		targetToken.append(getBasicAuthenticationHeader(targetUsername, targetPassword));
		contentType.append("application/json");

		String ds_host = APMUtil.getAPMGlobalDescriptor().getTargetHost();
		String ds_port = APMUtil.getAPMGlobalDescriptor().getTargetPort();
		String ds_protocol = APMUtil.getAPMGlobalDescriptor().getTargetProtocol();
		String profile_id = APMUtil.getAPMGlobalDescriptor().getTargetProfileName();
		String ds_index = "metric-" + profile_id + "-" + projName + "-$_write";
		String ds_type = "_doc";
		targetApiUrl.append(ds_protocol + "://" + ds_host + ":" + ds_port + "/" + ds_index + "/" + ds_type);
		logger.info("targetApi URL for ES is:" + targetApiUrl.toString());
		logger.info("Authroization for ES is: " + targetToken.toString());
	}

	/**
	 * This function will post payload to snappyflow ES
	 * 
	 * @param payload
	 */
	@Override
	@RequirePOST
	public boolean transmitData(HashMap<String, Object> payload) {
		logger.info("Response Code : " + postRequest(new StringEntity(new JSONObject(payload).toString().replaceAll("=", ":"), ContentType.APPLICATION_JSON)));
		return true;
	}

}
