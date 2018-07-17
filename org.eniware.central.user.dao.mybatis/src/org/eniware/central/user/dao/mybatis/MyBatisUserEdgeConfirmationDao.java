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
import org.eniware.central.user.dao.UserNodeConfirmationDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserNodeConfirmation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link UserNodeConfirmationDao}.
 * 
 * @version 1.0
 */
public class MyBatisUserNodeConfirmationDao extends BaseMyBatisGenericDao<UserNodeConfirmation, Long>
		implements UserNodeConfirmationDao {

	/** The query name used for {@link #getConfirmationForKey(String, String)}. */
	public static final String QUERY_FOR_KEY = "get-UserNodeConfirmation-for-key";

	/** The query name used for {@link #findUserNodesForUser(User)}. */
	public static final String QUERY_FOR_USER = "find-UserNodeConfirmation-for-User";

	/**
	 * Default constructor.
	 */
	public MyBatisUserNodeConfirmationDao() {
		super(UserNodeConfirmation.class, Long.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserNodeConfirmation getConfirmationForKey(Long userId, String key) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("userId", userId);
		params.put("key", key);
		return selectFirst(QUERY_FOR_KEY, params);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserNodeConfirmation> findPendingConfirmationsForUser(User user) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("user", user);
		params.put("pending", Boolean.TRUE);

		return getSqlSession().selectList(QUERY_FOR_USER, params);
	}

}
