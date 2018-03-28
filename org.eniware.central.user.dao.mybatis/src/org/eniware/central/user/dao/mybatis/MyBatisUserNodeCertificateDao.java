/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao.mybatis;

import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.user.dao.UserNodeCertificateDao;
import org.eniware.central.user.domain.UserNodeCertificate;
import org.eniware.central.user.domain.UserNodePK;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link UserNodeCertificateDao}.
 * 
 * @version 1.0
 */
public class MyBatisUserNodeCertificateDao extends
		BaseMyBatisGenericDao<UserNodeCertificate, UserNodePK> implements UserNodeCertificateDao {

	/** The query name used for {@link #getActiveCertificateForNode(Long)}. */
	public static final String QUERY_ACTIVE_FOR_NODE = "get-UserNodeCertificate-for-active-node";

	/**
	 * Default constructor.
	 */
	public MyBatisUserNodeCertificateDao() {
		super(UserNodeCertificate.class, UserNodePK.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserNodeCertificate getActiveCertificateForNode(Long nodeId) {
		return selectFirst(QUERY_ACTIVE_FOR_NODE, nodeId);
	}
}
