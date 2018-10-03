/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.UserEdgeCertificate;
import org.eniware.central.user.domain.UserEdgePK;

/**
 * DAO API for user Edge certificates.
 * 
 * @version 1.1
 */
public interface UserEdgeCertificateDao extends GenericDao<UserEdgeCertificate, UserEdgePK> {

	/**
	 * Get a certificate object for a given Edge ID and key.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @return the found UserEdgeCertificate, or <em>null</em> if not found
	 */
	UserEdgeCertificate getActiveCertificateForEdge(Long EdgeId);

}
