package io.jenkins.plugins.maplelabs.Client.Snappyflow;

import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import io.jenkins.plugins.maplelabs.Utils;

public class SnappyFlowKafkaImpl extends SnappyFlow {

    private static final Logger logger = Logger.getLogger(SnappyFlowKafkaImpl.class.getName());

    /**
     * This function will set http header
     */
    @Override
    protected void getHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl,boolean isEvent) {
        String host = Utils.getGlobalDescriptor().getTargetHost();
        String port = Utils.getGlobalDescriptor().getTargetPort();
        String path = Utils.getGlobalDescriptor().getTargetKafkaPath();
        String token = Utils.getGlobalDescriptor().getTargetKafkaToken();
        // String toipc = APMUtil.getGlobalDescriptor().getTargetKafkaTopic();
		String protocol = Utils.getGlobalDescriptor().getTargetProtocol();
        String profile = Utils.getGlobalDescriptor().getTargetProfileName();
        targetToken.append(token);
        contentType.append("application/vnd.kafka.json.v2+json");
        if(isEvent){
            targetApiUrl.append(protocol + "://" + host + ":" + port + "/" + path + "/topics/metric-" + profile );
        }
        else{
            targetApiUrl.append(protocol + "://" + host + ":" + port + "/" + path + "/topics/log-" + profile );
        }
        logger.fine("targetApi URL for Kafka is: " + targetApiUrl.toString());
        logger.fine("Authroization for Kafka is: " + targetToken.toString());
    }

    /**
     * This function will post payload to snappyflow Kafka
     */
    @Override
    public boolean transmitData(HashMap<String, Object> payload, boolean isEvent) {
        String KafkaData;
        StringEntity data;
        // For Kafka, need to prefix data with `{\"records\":[{\"value\":"`
        KafkaData = new JSONObject(payload).toString().replaceAll("=", ":");
        KafkaData = "{\"records\":[{\"value\":" + KafkaData + "}]}";
        data = new StringEntity(KafkaData, ContentType.APPLICATION_JSON);

        logger.fine("Response Code : " + postRequest(data,isEvent));
        return true;
    }

}
