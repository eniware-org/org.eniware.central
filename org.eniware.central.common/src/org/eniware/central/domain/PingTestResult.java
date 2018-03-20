/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.util.Map;

/**
 * A results object for a single {@link PingTest} result.
 * 
 * @author matt
 * @version 1.0
 */
public class PingTestResult {

	private final boolean success;
	private final String message;
	private final Map<String, ?> properties;

	/**
	 * Construct values.
	 * 
	 * @param success
	 *        The success flag.
	 * @param message
	 *        The message.
	 * @param properties
	 *        Optional properties.
	 */
	public PingTestResult(boolean success, String message, Map<String, ?> properties) {
		super();
		this.success = success;
		this.message = message;
		this.properties = properties;
	}

	/**
	 * Construct with status flag and message.
	 * 
	 * @param success
	 *        The success flag.
	 * @param message
	 *        The message.
	 */
	public PingTestResult(boolean success, String message) {
		this(success, message, null);
	}

	public String getMessage() {
		return message;
	}

	public boolean isSuccess() {
		return success;
	}

	public Map<String, ?> getProperties() {
		return properties;
	}

}
