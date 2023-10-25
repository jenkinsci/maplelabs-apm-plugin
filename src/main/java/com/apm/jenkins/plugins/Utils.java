package com.apm.jenkins.plugins;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.apm.jenkins.plugins.Events.interfaces.IEvent;
import com.apm.jenkins.plugins.Client.Snappyflow.SnappyFlow;

import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import hudson.EnvVars;
import hudson.ExtensionList;

public class Utils {
	private static final Integer MAX_HOSTNAME = 255;
	public static final long publisherTime = TimeUnit.MINUTES.toMillis(3);
	private static final Logger logger = Logger.getLogger(Utils.class.getName());

	/**
	 * @return - The descriptor for the APM plugin. In this case the global
	 *         configuration.
	 */
	public static Configuration getGlobalDescriptor() {
		try {
			return ExtensionList.lookupSingleton(Configuration.class);
		} catch (RuntimeException e) {
			return null;
		}
	}
	
	public static long getCurrentTimeInMillis() {
		return System.currentTimeMillis();
	}
	
	private static String getOSName() {
		return System.getProperty("os.name").split(" ")[0].toLowerCase();
	}
	
	/**
	 * This function will execute command in terminal and return output
	 * @param cmd
	 * @return
	 */
	public static String getTerminalOP(String[] cmd ) {
		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
    	String op = "";
    	try {
    	    Process proc = processBuilder.start();
    	    InputStream in = proc.getInputStream();
    	    BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
    	    StringBuilder out = new StringBuilder();
    	    String line;
    	    while ((line = reader.readLine()) != null) {
    	        out.append(line);
    	    }
    	    reader.close();
    	    op = out.toString();
    	} catch (IOException e) {
    	    logger.warning("Terminal cmd execution error: " + e.toString());
    	}
    	return op;
	}

	/**
	 * Validator function to ensure that the hostname is valid. Also, fails on
	 * empty String.
	 *
	 * @param hostname - A String object containing the name of a host.
	 * @return a boolean representing the validity of the hostname
	 */
	public static Boolean isValidHostName(String hostname) {
		if (hostname == null) return false;

		String[] localHosts = { 
			"localhost", 
			"ip6-localhost" ,
			"localhost.localdomain",
			"localhost6.localdomain6"
		};
				
		String HostNamePattern = "^(?=.{1,255}$)[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?(?:\\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?)*\\.?$";

		// Check if hostname is local
		if (Arrays.asList(localHosts).contains(hostname.toLowerCase())) {
			logger.warning("Hostname: "+hostname+" is local");
			return false;
		}

		// Ensure proper length
		if (hostname.length() > MAX_HOSTNAME) {
			logger.warning("Hostname: "+hostname +" is too long (max length is "+MAX_HOSTNAME+" characters)");
			return false;
		}

		// RFC1123
		return Pattern.compile(HostNamePattern).matcher(hostname).find();
	}

	/**
	 * This function will get hostname
	 * @param envVars jenkins env
	 * @return hostname
	 */
	public static String getHostName(EnvVars envVars) {
		String hostname = null;
		String[] UNIX_OS = { "mac", "linux", "freebsd", "sunos" };

		// Check hostname using jenkins env variables
		if (envVars != null) {
			hostname = envVars.get("HOSTNAME");
			if (isValidHostName(hostname)) {
				logger.fine("Jenkins sys env. Hostname: " + hostname);
				return hostname;
			}
		}

		// Check OS specific unix commands
		String os = getOSName();
		if (Arrays.asList(UNIX_OS).contains(os)) {

			String[] cmd = { "/bin/hostname", "-f" };
			hostname = getTerminalOP(cmd);

			if (isValidHostName(hostname)) {
				logger.fine(String.format("Using unix hostname found via `/bin/hostname -f`. Hostname: %s",
						hostname));
				return hostname;
			}
		} else if (os.contains("win")) {
			String[] cmd = {"hostname"};
			hostname = getTerminalOP(cmd);

			if (isValidHostName(hostname)) {
				logger.fine(String.format("Using windows hostname found via `hostname `. Hostname: %s",
						hostname));
				return hostname;
			}
		}

		// Never found the hostname
		if (hostname == null || "".equals(hostname)) {
			logger.warning("Unable to find hostname");
		}
		return null;
	}

	/**
	 * This function will get a value if value not found return defaultvalue
	 * 
	 * @param <T>          Generic Type
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static <T> T getValue(T value, T defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
     * This function will assembel Event details and call client
     * 
     * @return
     */
    public static boolean sendEvent(IEvent event) {
        if(Utils.getGlobalDescriptor().getIsEventEnabled()) {
            HashMap<String, Object> payload = SnappyFlow.getSnappyflowTags(event.getEventType());
            payload.put("text", event.getText());
            payload.put("host", event.getHost());
            payload.put("title", event.getTitle());
            payload.put("date_happened", event.getDate());
            payload.put("priority", event.getPriority().name().toLowerCase());
            payload.put("alert_type", event.getAlertType().name().toLowerCase());
            return Utils.getGlobalDescriptor().getDestinationClient().transmitData(payload,true);
        }
        return false;
    }

	/**
	 * This function will send metrics if Enable Metric is selected
	 * @param payload
	 * @return
	 */
	public static boolean sendMetrics(HashMap<String,Object> payload) {
		if(Utils.getGlobalDescriptor().getIsMetricEnabled()) {
            return Utils.getGlobalDescriptor().getDestinationClient().transmitData(payload,false);
        }
        return false;
	}
	public static String replaceCapsWithUnderscore(String project){
		StringBuilder result = new StringBuilder();

        boolean hasUppercase = false;

        for (int i = 0; i < project.length(); i++) {
            char currentChar = project.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                result.append("_").append(Character.toLowerCase(currentChar));
                hasUppercase = true;
            } else {
                result.append(currentChar);
            }
        }

        return hasUppercase ? result.toString() : project;
    }
}
