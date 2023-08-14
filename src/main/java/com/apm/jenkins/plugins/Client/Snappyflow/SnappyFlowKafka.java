package com.apm.jenkins.plugins.Client.Snappyflow;

import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.apm.jenkins.plugins.APMUtil;

public class SnappyFlowKafka extends SnappyFlow {

    private static final Logger logger = Logger.getLogger(SnappyFlowKafka.class.getName());

    /**
     * This function will set http header
     * 
     * @param contentType
     * @param targetToken
     * @param targetApiUrl
     */
    @Override
    protected void getHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl) {
        String host = APMUtil.getAPMGlobalDescriptor().getTargetHost();
        String port = APMUtil.getAPMGlobalDescriptor().getTargetPort();
        String path = APMUtil.getAPMGlobalDescriptor().getTargetKafkaPath();
        String token = APMUtil.getAPMGlobalDescriptor().getTargetKafkaToken();
        // String toipc = APMUtil.getAPMGlobalDescriptor().getTargetKafkaTopic();
		String protocol = APMUtil.getAPMGlobalDescriptor().getTargetProtocol();
        String profile = APMUtil.getAPMGlobalDescriptor().getTargetProfileName();
        targetToken.append(token);
        contentType.append("application/vnd.kafka.json.v2+json");
        targetApiUrl.append(protocol + "://" + host + ":" + port + "/" + path + "/topics/metric-" + profile );
        logger.info("targetApi URL for Kafka is: " + targetApiUrl.toString());
        logger.info("Authroization for Kafka is: " + targetToken.toString());
    }

    /**
     * This function will post payload to snappyflow Kafka
     * 
     * @param payload
     */
    @Override
    public boolean transmitData(HashMap<String, Object> payload) {
        String KafkaData;
        StringEntity data;
        // For Kafka, need to prefix data with `{\"records\":[{\"value\":"`
        KafkaData = new JSONObject(payload).toString().replaceAll("=", ":");
        KafkaData = "{\"records\":[{\"value\":" + KafkaData + "}]}";
        data = new StringEntity(KafkaData, ContentType.APPLICATION_JSON);

        logger.fine("Response Code : " + postRequest(data));
        return true;
    }

}
