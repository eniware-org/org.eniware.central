/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.domain.UserEdgePK;
import org.eniware.central.user.domain.UserEdgeTransfer;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link UserEdgeDao}.
 *
 * @version 1.1
 */
public class MyBatisUserEdgeDao extends BaseMyBatisGenericDao<UserEdge, Long> implements UserEdgeDao {

	/** The query name used for {@link #findUserEdgesForUser(User)}. */
	public static final String QUERY_FOR_USER = "find-UserEdge-for-User";

	/**
	 * The query name used for {@link #findArchivedUserEdgesForUser(Long)}.
	 * 
	 * @since 1.1
	 */
	public static final String QUERY_FOR_USER_ARCHIVED = "find-archived-UserEdge-for-User";

	/**
	 * The query name used for
	 * {@link #updateUserEdgeArchivedStatus(Long, Long[], boolean)}.
	 * 
	 * @since 1.1
	 */
	public static final String UPDATE_ARCHIVED_STATUS = "update-archived-UserEdge-status";

	/**
	 * The query name used for
	 * {@link #findUserEdgesAndCertificatesForUser(Long)}.
	 */
	public static final String QUERY_FOR_USER_WITH_CERT = "find-UserEdge-for-user-with-certs";

	/**
	 * The callable statement used in
	 * {@link #storeUserEdgeTransfer(UserEdgeTransfer)}.
	 */
	public static final String CALL_STORE_USER_Edge_TRANSFER = "store-UserEdgeTransfer";

	/** The query name for {@link #deleteUserEdgeTrasnfer(UserEdgeTransfer)}. */
	public static final String DELETE_USER_Edge_TRANSFER = "delete-UserEdgeTransfer";

	/**
	 * The query name used for {@link #getUserEdgeTransfer(UserEdgePK)}.
	 */
	public static final String QUERY_USER_Edge_TRANSFERS_FOR_ID = "get-UserEdgeTransfer-for-id";

	/**
	 * The query name used for
	 * {@link #findUserEdgeTransferRequestsForEmail(String)}.
	 */
	public static final String QUERY_USER_Edge_TRANSFERS_FOR_EMAIL = "find-UserEdgeTransfer-for-email";

	/**
	 * Default constructor.
	 */
	public MyBatisUserEdgeDao() {
		super(UserEdge.class, Long.class);
	}

	@Override
	protected Long handleInsert(UserEdge datum) {
		super.handleInsert(datum);
		// as our primary key is actually the Edge ID, return that
		assert datum.getEdge() != null;
		return datum.getEdge().getId();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdge> findUserEdgesForUser(User user) {
		List<UserEdge> results = getSqlSession().selectList(QUERY_FOR_USER, user.getId());
		for ( UserEdge userEdge : results ) {
			userEdge.setUser(user);
		}
		return results;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdge> findUserEdgesAndCertificatesForUser(Long userId) {
		return getSqlSession().selectList(QUERY_FOR_USER_WITH_CERT, userId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void storeUserEdgeTransfer(UserEdgeTransfer transfer) {
		getSqlSession().update(CALL_STORE_USER_Edge_TRANSFER, transfer);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeTransfer getUserEdgeTransfer(UserEdgePK pk) {
		return getSqlSession().selectOne(QUERY_USER_Edge_TRANSFERS_FOR_ID, pk);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void deleteUserEdgeTrasnfer(UserEdgeTransfer transfer) {
		int count = getSqlSession().delete(DELETE_USER_Edge_TRANSFER, transfer.getId());
		log.debug("Deleted {} UserEdgeTransfer entities for ID {}", count, transfer.getId());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdgeTransfer> findUserEdgeTransferRequestsForEmail(String email) {
		return getSqlSession().selectList(QUERY_USER_Edge_TRANSFERS_FOR_EMAIL, email);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdge> findArchivedUserEdgesForUser(Long userId) {
		return getSqlSession().selectList(QUERY_FOR_USER_ARCHIVED, userId);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateUserEdgeArchivedStatus(Long userId, Long[] EdgeIds, boolean archived) {
		Map<String, Object> sqlProperties = new HashMap<String, Object>(3);
		sqlProperties.put("userId", userId);
		sqlProperties.put("EdgeIds", EdgeIds);
		sqlProperties.put("archived", archived);
		getSqlSession().update(UPDATE_ARCHIVED_STATUS, sqlProperties);
	}

}
