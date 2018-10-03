/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

/**
 * Base interface for other authenticated detail interfaces to extend.
 
 * @version 1.0
 */
public interface SecurityActor {

	/**
	 * Return <em>true</em> if the actor authenticated via a token.
	 * 
	 * @return boolean
	 */
	boolean isAuthenticatedWithToken();

}
