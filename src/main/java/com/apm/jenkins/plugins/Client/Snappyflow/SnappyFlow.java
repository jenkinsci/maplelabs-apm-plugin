package com.apm.jenkins.plugins.Client.Snappyflow;

import java.util.Base64;
import java.io.IOException;
import java.util.logging.Logger;
import java.nio.charset.Charset;
import java.security.KeyStoreException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import com.apm.jenkins.plugins.Client.Communication;


public abstract class SnappyFlow implements Communication {

	private HttpClient client;
    private static final Logger logger = Logger.getLogger(SnappyFlow.class.getName());

	/**
	 * This function return http authentication detail
	 * @param username
	 * @param password
	 * @return
	 */
    protected String getBasicAuthenticationHeader(String username, String password) {
    	String valueToEncode = username+":"+password;
    	return "Basic "+Base64.getEncoder().encodeToString(valueToEncode.getBytes(Charset.forName("UTF-8"))).toString();
    }
    
	/**
	 * This function will return http client
	 * @return
	 */
	protected HttpClient getClient() {
		if(client != null) return client;
		else {
			SSLContext sslContext;
			try {
				sslContext = SSLContexts.custom()
						.loadTrustMaterial((chain, authType) -> true)
						.build();
						SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
			
				client = HttpClients.custom()
								.setSSLSocketFactory(socketFactory)
								.build();	
			} catch (KeyStoreException |KeyManagementException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		}
		return client;
	}

	/**
	 * This function will do HTTP POST
	 * @param post
	 * @param payload
	 * @return
	 */
	protected int post(HttpPost post, StringEntity payload) {
		int responseCode = 0;
		try {
			logger.info("Posted Data is here:---------------");
			String requestBody = new String(payload.getContent().readAllBytes(),StandardCharsets.UTF_8);
			logger.info(requestBody);
			logger.info("\nSending 'POST' request to URL : " + post.getURI());
			HttpResponse response = getClient().execute(post);
			responseCode = response.getStatusLine().getStatusCode();
			logger.info("================================================");
			String responseBody = EntityUtils.toString((response).getEntity());
			logger.info(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseCode;
	}

	abstract protected void getHeaders(StringBuilder contentType, StringBuilder targetToken, StringBuilder targetApiUrl);
}
