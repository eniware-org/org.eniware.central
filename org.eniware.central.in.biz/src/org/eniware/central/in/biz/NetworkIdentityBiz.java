/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.biz;

import org.eniware.domain.NetworkAssociation;
import org.eniware.domain.NetworkIdentity;

/**
 * API for identifying the SolarIn service to nodes.
 *
 * @version $Revision$
 */
public interface NetworkIdentityBiz {

	/**
	 * Get the public-facing network identity for this service.
	 * 
	 * <p>
	 * This is the information that should be publicly available for users to
	 * view, so they can validate this against the same info presented during
	 * node association.
	 * </p>
	 * 
	 * @return identity key
	 */
	NetworkIdentity getNetworkIdentity();

	/**
	 * Get a network association for a given username and confirmation key.
	 * 
	 * @param username
	 *        the username
	 * @param confirmationKey
	 *        the confirmation key
	 * @return the association, or <em>null</em> if not available
	 */
	NetworkAssociation getNetworkAssociation(String username, String confirmationKey);

}
