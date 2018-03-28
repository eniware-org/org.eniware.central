/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

/**
 * Enum for {@link UserAuthToken} status.
 * 
 * @version 1.0
 */
public enum UserAuthTokenStatus {

	/** The token is active and valid. */
	Active,

	/** The token is disabled and should not be used. */
	Disabled;

}
