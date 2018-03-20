/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

/**
 * A token based actor.
 * 
 * @author matt
 * @version 1.1
 */
public interface SecurityToken extends SecurityActor {

	/**
	 * Get a unique user ID that owns the token.
	 * 
	 * @return the user ID
	 */
	Long getUserId();

	/**
	 * Get the token value.
	 * 
	 * @return the token
	 */
	String getToken();

	/**
	 * Get the type of token.
	 * 
	 * @return the token type
	 */
	String getTokenType();

	/**
	 * Get an optional security policy.
	 * 
	 * @return optional security policy
	 */
	SecurityPolicy getPolicy();
}
