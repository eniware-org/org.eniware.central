/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.UserEdgeCertificate;
import org.eniware.central.user.domain.UserEdgePK;

/**
 * DAO API for user node certificates.
 * 
 * @version 1.1
 */
public interface UserEdgeCertificateDao extends GenericDao<UserEdgeCertificate, UserEdgePK> {

	/**
	 * Get a certificate object for a given node ID and key.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @return the found UserNodeCertificate, or <em>null</em> if not found
	 */
	UserEdgeCertificate getActiveCertificateForNode(Long nodeId);

}
