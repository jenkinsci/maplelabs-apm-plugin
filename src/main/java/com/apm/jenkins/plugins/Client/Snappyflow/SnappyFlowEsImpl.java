package com.apm.jenkins.plugins.Client.Snappyflow;

import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.apm.jenkins.plugins.Utils;

public class SnappyFlowEsImpl extends SnappyFlow {

	private static final Logger logger = Logger.getLogger(SnappyFlowEsImpl.class.getName());

	/**
	 * This function will set http header
	 * 
	 * @param contentType
	 * @param targetToken
	 * @param targetApiUrl
	 */
	@Override
	protected void getHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl) {
		String projName = Utils.getGlobalDescriptor().getTargetProjectName();

		if (projName == null) {
			logger.severe("ProjectName in is null, please fill the required details of snappyflow in Manage Jenkins");
			return;
		}
		String targetUsername = Utils.getGlobalDescriptor().getTargetESUserName();
		String targetPassword = Utils.getGlobalDescriptor().getTargetESPassword();

		targetToken.append(getBasicAuthenticationHeader(targetUsername, targetPassword));
		contentType.append("application/json");

		String ds_host = Utils.getGlobalDescriptor().getTargetHost();
		String ds_port = Utils.getGlobalDescriptor().getTargetPort();
		String ds_protocol = Utils.getGlobalDescriptor().getTargetProtocol();
		String profile_id = Utils.getGlobalDescriptor().getTargetProfileName();
		String ds_index = "metric-" + profile_id + "-" + projName + "-$_write";
		String ds_type = "_doc";
		targetApiUrl.append(ds_protocol + "://" + ds_host + ":" + ds_port + "/" + ds_index + "/" + ds_type);
		logger.fine("targetApi URL for ES is:" + targetApiUrl.toString());
		logger.fine("Authroization for ES is: " + targetToken.toString());
	}

	/**
	 * This function will post payload to snappyflow ES
	 * 
	 * @param payload
	 */
	@Override
	public boolean transmitData(HashMap<String, Object> payload) {
		logger.fine("Response Code : " + postRequest(new StringEntity(new JSONObject(payload).toString().replaceAll("=", ":"), ContentType.APPLICATION_JSON)));
		return true;
	}

}
