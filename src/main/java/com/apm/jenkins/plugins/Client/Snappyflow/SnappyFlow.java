package com.apm.jenkins.plugins.Client.Snappyflow;

import java.util.Base64;
import java.util.HashMap;
import java.io.IOException;
import java.util.logging.Logger;
import java.nio.charset.Charset;
import java.security.KeyStoreException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.kohsuke.stapler.interceptor.RequirePOST;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import com.apm.jenkins.plugins.Utils;
import com.apm.jenkins.plugins.Client.IClient;

public abstract class SnappyFlow implements IClient {

	private HttpPost post;
	private HttpClient client;
	private static final Logger logger = Logger.getLogger(SnappyFlow.class.getName());

	// This will create post method
	protected void createPost() {
		if(post != null) {
			return;
		} else {
			StringBuilder targetToken = new StringBuilder();
			StringBuilder contentType = new StringBuilder();
			StringBuilder targetApiUrl = new StringBuilder();
			getHeaders(contentType, targetToken, targetApiUrl);
	
			post = new HttpPost(targetApiUrl.toString());
	
			post.setHeader("Content-Type", contentType.toString());
			post.setHeader("Authorization", targetToken.toString());
			post.setHeader("Accept", "application/vnd.kafka.v2+json");
		}
	}
	/**
	 * This function will default tags for snappyflow
	 * 
	 * @param docType
	 * @return
	 */
	public static HashMap<String, Object> getSnappyflowTags(String docType) {

		long currTime = Utils.getCurrentTimeInMillis();
		HashMap<String, Object> result = new HashMap<>();
		String appName = Utils.getGlobalDescriptor().getTargetAppName();
		String instName = Utils.getGlobalDescriptor().getTargetInstanceName();
		String projectName = Utils.getGlobalDescriptor().getTargetProjectName();

		if (projectName != null)
			result.put("_tag_projectName", projectName);

		if (appName != null)
			result.put("_tag_appName", appName);

		if (instName != null)
			result.put("_tag_instanceName", instName);

		result.put("time", currTime);
		result.put("_plugin", "Jenkins");
		result.put("_documentType", docType);

		return result;
	}

	/**
	 * This function return http authentication detail as a String
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	protected String getBasicAuthenticationHeader(String username, String password) {
		String valueToEncode = username + ":" + password;
		return "Basic "
				+ Base64.getEncoder().encodeToString(valueToEncode.getBytes(Charset.forName("UTF-8"))).toString();
	}

	/**
	 * This function will return http client
	 * 
	 * @return
	 */
	protected HttpClient getClient() {
		if (client != null)
			return client;
		else {
			SSLContext sslContext;
			try {
				sslContext = SSLContexts.custom()
						.loadTrustMaterial((chain, authType) -> true)
						.build();
				SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
						NoopHostnameVerifier.INSTANCE);

				client = HttpClients.custom()
						.setSSLSocketFactory(socketFactory)
						.build();
			} catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
				logger.severe("Http creation error : "+e.toString());
			}

		}
		return client;
	}

	/**
	 * This function will do HTTP POST andreturns response code
	 * 
	 * @param data
	 * @return
	 */
	@RequirePOST
	protected int postRequest(StringEntity data) {
		int responseCode = 0;
		try {
			if(post == null) createPost();
			post.setEntity(data);
			logger.info("Post Headers:---------------");
			Header[] headers = post.getAllHeaders();
			for (Header header : headers) {
				logger.info(header.getName() + ":" + header.getValue());
			}

			logger.info("Posted Data is here:---------------");
			logger.info(new String(post.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8));
		
			logger.info("\nSending 'POST' request to URL : " + post.getURI());
			HttpResponse response = getClient().execute(post);
			responseCode = response.getStatusLine().getStatusCode();
			logger.info("================================================");
			String responseBody = EntityUtils.toString((response).getEntity());
			logger.info(responseBody);
		} catch (IOException e) {
			logger.severe("Http Post error : "+e.toString());
		}
		return responseCode;
	}

	abstract protected void getHeaders(StringBuilder contentType, StringBuilder targetToken,
			StringBuilder targetApiUrl);
}
