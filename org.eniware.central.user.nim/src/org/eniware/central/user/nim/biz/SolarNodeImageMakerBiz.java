/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.nim.biz;

import org.eniware.central.security.SecurityToken;

/**
 * API for integration with a SolarNode Image Maker instance.
 * 
 * @author matt
 * @version 1.0
 */
public interface SolarNodeImageMakerBiz {

	/**
	 * Get a NIM authorization key for a SolarNetwork auth token.
	 * 
	 * <p>
	 * This method might take several minutes to complete, for example if NIM is
	 * running on a managed virtual machine and needs to be booted up.
	 * </p>
	 * 
	 * @param token
	 *        the auth token to use
	 * @param solarNetworkBaseUrl
	 *        the SolarNetwork base URL
	 * @return the NIM authorization token
	 */
	String authorizeToken(SecurityToken token, String solarNetworkBaseUrl);

}
