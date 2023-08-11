package com.apm.jenkins.plugins;

import java.io.InputStream;
import java.net.Inet4Address;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import hudson.EnvVars;
import hudson.ExtensionList;

public class APMUtil {
	public static final long publisherTime = TimeUnit.MINUTES.toMillis(1);
	/**
	 * Getter function to return either the saved hostname global configuration,
	 * or the hostname that is set in the Jenkins host itself. Returns null if no
	 * valid hostname is found.
	 *
	 * Tries, in order:
	 * Jenkins configuration
	 * Jenkins hostname environment variable
	 * Unix hostname via `/bin/hostname -f`
	 * Localhost hostname
	 *
	 * @param envVars - The Jenkins environment variables
	 * @return a human readable String for the hostname.
	 */
	private static final Logger logger = Logger.getLogger(APMUtil.class.getName());

	private static final Integer MAX_HOSTNAME_LEN = 255;
	// private static final String DATE_FORMAT_ISO8601 =
	// "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	/**
	 * Validator function to ensure that the hostname is valid. Also, fails on
	 * empty String.
	 *
	 * @param hostname - A String object containing the name of a host.
	 * @return a boolean representing the validity of the hostname
	 */
	public static Boolean isValidHostname(String hostname) {
		if (hostname == null) {
			return false;
		}

		String[] localHosts = { "localhost", "localhost.localdomain",
				"localhost6.localdomain6", "ip6-localhost" };
		String VALID_HOSTNAME_RFC_1123_PATTERN = "^(([a-zA-Z0-9]|"
				+ "[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*"
				+ "([A-Za-z0-9]|"
				+ "[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
		String host = hostname.toLowerCase();

		// Check if hostname is local
		if (Arrays.asList(localHosts).contains(host)) {
			logger.fine(String.format("Hostname: %s is local", hostname));
			return false;
		}

		// Ensure proper length
		if (hostname.length() > MAX_HOSTNAME_LEN) {
			logger.fine(String.format("Hostname: %s is too long (max length is %s characters)",
					hostname, MAX_HOSTNAME_LEN));
			return false;
		}

		// Check compliance with RFC 1123
		Pattern r = Pattern.compile(VALID_HOSTNAME_RFC_1123_PATTERN);
		Matcher m = r.matcher(hostname);

		// Final check: Hostname matches RFC1123?
		return m.find();
	}

	/**
	 * @return - The descriptor for the APM plugin. In this case the global
	 *         configuration.
	 */
	public static APMGlobalConfiguration getAPMGlobalDescriptor() {
		try {
			return ExtensionList.lookupSingleton(APMGlobalConfiguration.class);
		} catch (RuntimeException e) {
			return null;
		}
	}

	private static String getOS() {
		String out = System.getProperty("os.name");
		String os = out.split(" ")[0];
		return os.toLowerCase();
	}

	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public static String getHostname(EnvVars envVars) {
		String[] UNIX_OS = { "mac", "linux", "freebsd", "sunos" };

		// Check hostname configuration from Jenkins
		String hostname = null;

		// Check hostname using jenkins env variables
		if (envVars != null) {
			hostname = envVars.get("HOSTNAME");
		}
		if (isValidHostname(hostname)) {
			logger.fine("Using hostname set in 'Manage Plugins'. Hostname: " + hostname);
			return hostname;
		}

		// Check OS specific unix commands
		String os = getOS();
		if (Arrays.asList(UNIX_OS).contains(os)) {
			// Attempt to grab unix hostname
			try {
				String[] cmd = { "/bin/hostname", "-f" };
				Process proc = Runtime.getRuntime().exec(cmd);
				InputStream in = proc.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
				StringBuilder out = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					out.append(line);
				}
				reader.close();
				hostname = out.toString();
			} catch (Exception e) {
				logger.severe("Failed to obtain UNIX hostname");
				e.printStackTrace();
			}

			// Check hostname
			if (isValidHostname(hostname)) {
				logger.fine(String.format("Using unix hostname found via `/bin/hostname -f`. Hostname: %s",
						hostname));
				return hostname;
			}
		}

		// Check localhost hostname
		try {
			hostname = Inet4Address.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.warning(String.format("Unknown hostname error received for localhost. Error: %s", e));
		}
		if (isValidHostname(hostname)) {
			logger.fine(String.format("Using hostname found via "
					+ "Inet4Address.getLocalHost().getHostName()."
					+ " Hostname: %s", hostname));
			return hostname;
		}

		// Never found the hostname
		if (hostname == null || "".equals(hostname)) {
			logger.warning("Unable to reliably determine host name. You can define one in "
					+ "the 'Manage Plugins' section under the 'APM Plugin' section.");
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

}
