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
import org.eniware.central.user.dao.UserEdgeConfirmationDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserEdgeConfirmation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link UserEdgeConfirmationDao}.
 * 
 * @version 1.0
 */
public class MyBatisUserEdgeConfirmationDao extends BaseMyBatisGenericDao<UserEdgeConfirmation, Long>
		implements UserEdgeConfirmationDao {

	/** The query name used for {@link #getConfirmationForKey(String, String)}. */
	public static final String QUERY_FOR_KEY = "get-UserEdgeConfirmation-for-key";

	/** The query name used for {@link #findUserEdgesForUser(User)}. */
	public static final String QUERY_FOR_USER = "find-UserEdgeConfirmation-for-User";

	/**
	 * Default constructor.
	 */
	public MyBatisUserEdgeConfirmationDao() {
		super(UserEdgeConfirmation.class, Long.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeConfirmation getConfirmationForKey(Long userId, String key) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("userId", userId);
		params.put("key", key);
		return selectFirst(QUERY_FOR_KEY, params);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdgeConfirmation> findPendingConfirmationsForUser(User user) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("user", user);
		params.put("pending", Boolean.TRUE);

		return getSqlSession().selectList(QUERY_FOR_USER, params);
	}

}
