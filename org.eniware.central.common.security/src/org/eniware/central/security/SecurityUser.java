/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

/**
 * API for user details.
 * 
 * @author matt
 * @version 1.0
 */
public interface SecurityUser extends SecurityActor {

	/**
	 * Get a friendly display name.
	 * 
	 * @return display name
	 */
	String getDisplayName();

	/**
	 * Get the email used to authenticate the user with.
	 * 
	 * @return email
	 */
	String getEmail();

	/**
	 * Get a unique user ID.
	 * 
	 * @return the user ID
	 */
	Long getUserId();

}
