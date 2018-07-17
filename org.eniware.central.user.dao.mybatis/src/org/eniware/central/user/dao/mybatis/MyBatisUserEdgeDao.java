/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.user.dao.UserNodeDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserNode;
import org.eniware.central.user.domain.UserNodePK;
import org.eniware.central.user.domain.UserNodeTransfer;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link UserNodeDao}.
 *
 * @version 1.1
 */
public class MyBatisUserNodeDao extends BaseMyBatisGenericDao<UserNode, Long> implements UserNodeDao {

	/** The query name used for {@link #findUserNodesForUser(User)}. */
	public static final String QUERY_FOR_USER = "find-UserNode-for-User";

	/**
	 * The query name used for {@link #findArchivedUserNodesForUser(Long)}.
	 * 
	 * @since 1.1
	 */
	public static final String QUERY_FOR_USER_ARCHIVED = "find-archived-UserNode-for-User";

	/**
	 * The query name used for
	 * {@link #updateUserNodeArchivedStatus(Long, Long[], boolean)}.
	 * 
	 * @since 1.1
	 */
	public static final String UPDATE_ARCHIVED_STATUS = "update-archived-UserNode-status";

	/**
	 * The query name used for
	 * {@link #findUserNodesAndCertificatesForUser(Long)}.
	 */
	public static final String QUERY_FOR_USER_WITH_CERT = "find-UserNode-for-user-with-certs";

	/**
	 * The callable statement used in
	 * {@link #storeUserNodeTransfer(UserNodeTransfer)}.
	 */
	public static final String CALL_STORE_USER_NODE_TRANSFER = "store-UserNodeTransfer";

	/** The query name for {@link #deleteUserNodeTrasnfer(UserNodeTransfer)}. */
	public static final String DELETE_USER_NODE_TRANSFER = "delete-UserNodeTransfer";

	/**
	 * The query name used for {@link #getUserNodeTransfer(UserNodePK)}.
	 */
	public static final String QUERY_USER_NODE_TRANSFERS_FOR_ID = "get-UserNodeTransfer-for-id";

	/**
	 * The query name used for
	 * {@link #findUserNodeTransferRequestsForEmail(String)}.
	 */
	public static final String QUERY_USER_NODE_TRANSFERS_FOR_EMAIL = "find-UserNodeTransfer-for-email";

	/**
	 * Default constructor.
	 */
	public MyBatisUserNodeDao() {
		super(UserNode.class, Long.class);
	}

	@Override
	protected Long handleInsert(UserNode datum) {
		super.handleInsert(datum);
		// as our primary key is actually the node ID, return that
		assert datum.getNode() != null;
		return datum.getNode().getId();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserNode> findUserNodesForUser(User user) {
		List<UserNode> results = getSqlSession().selectList(QUERY_FOR_USER, user.getId());
		for ( UserNode userNode : results ) {
			userNode.setUser(user);
		}
		return results;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserNode> findUserNodesAndCertificatesForUser(Long userId) {
		return getSqlSession().selectList(QUERY_FOR_USER_WITH_CERT, userId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void storeUserNodeTransfer(UserNodeTransfer transfer) {
		getSqlSession().update(CALL_STORE_USER_NODE_TRANSFER, transfer);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserNodeTransfer getUserNodeTransfer(UserNodePK pk) {
		return getSqlSession().selectOne(QUERY_USER_NODE_TRANSFERS_FOR_ID, pk);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void deleteUserNodeTrasnfer(UserNodeTransfer transfer) {
		int count = getSqlSession().delete(DELETE_USER_NODE_TRANSFER, transfer.getId());
		log.debug("Deleted {} UserNodeTransfer entities for ID {}", count, transfer.getId());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserNodeTransfer> findUserNodeTransferRequestsForEmail(String email) {
		return getSqlSession().selectList(QUERY_USER_NODE_TRANSFERS_FOR_EMAIL, email);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserNode> findArchivedUserNodesForUser(Long userId) {
		return getSqlSession().selectList(QUERY_FOR_USER_ARCHIVED, userId);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateUserNodeArchivedStatus(Long userId, Long[] nodeIds, boolean archived) {
		Map<String, Object> sqlProperties = new HashMap<String, Object>(3);
		sqlProperties.put("userId", userId);
		sqlProperties.put("nodeIds", nodeIds);
		sqlProperties.put("archived", archived);
		getSqlSession().update(UPDATE_ARCHIVED_STATUS, sqlProperties);
	}

}
