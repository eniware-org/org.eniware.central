/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.UserNodeCertificate;
import org.eniware.central.user.domain.UserNodePK;

/**
 * DAO API for user node certificates.
 * 
 * @version 1.1
 */
public interface UserNodeCertificateDao extends GenericDao<UserNodeCertificate, UserNodePK> {

	/**
	 * Get a certificate object for a given node ID and key.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @return the found UserNodeCertificate, or <em>null</em> if not found
	 */
	UserNodeCertificate getActiveCertificateForNode(Long nodeId);

}
