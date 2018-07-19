/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao.mybatis;

import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.user.dao.UserEdgeCertificateDao;
import org.eniware.central.user.domain.UserEdgeCertificate;
import org.eniware.central.user.domain.UserEdgePK;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link UserEdgeCertificateDao}.
 * 
 * @version 1.0
 */
public class MyBatisUserEdgeCertificateDao extends
		BaseMyBatisGenericDao<UserEdgeCertificate, UserEdgePK> implements UserEdgeCertificateDao {

	/** The query name used for {@link #getActiveCertificateForNode(Long)}. */
	public static final String QUERY_ACTIVE_FOR_NODE = "get-UserNodeCertificate-for-active-node";

	/**
	 * Default constructor.
	 */
	public MyBatisUserEdgeCertificateDao() {
		super(UserEdgeCertificate.class, UserEdgePK.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeCertificate getActiveCertificateForNode(Long nodeId) {
		return selectFirst(QUERY_ACTIVE_FOR_NODE, nodeId);
	}
}
