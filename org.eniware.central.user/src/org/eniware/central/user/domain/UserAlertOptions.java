/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

/**
 * {@link UserAlert} option constants.
 * 
 * @version 1.1
 */
public interface UserAlertOptions {

	/**
	 * An age threshold, in the form of a decimal number of seconds.
	 */
	static String AGE_THRESHOLD = "age";

	/**
	 * A list of string datum source ID values.
	 */
	static String SOURCE_IDS = "sourceIds";

	/**
	 * A list of time window objects.
	 */
	static String TIME_WINDOWS = "windows";

}
