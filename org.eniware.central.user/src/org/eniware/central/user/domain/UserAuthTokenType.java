/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

/**
 * Enum for {@link UserAuthToken} type.
 * 
 * @version 1.0
 */
public enum UserAuthTokenType {

	/** A full user token, granting the bearer full access. */
	User,

	/**
	 * A read-only token, granting the bearer access to query read Edge data
	 * only.
	 */
	ReadEdgeData;

}
