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
import org.eniware.central.user.dao.UserAlertDao;
import org.eniware.central.user.domain.UserAlert;
import org.eniware.central.user.domain.UserAlertType;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link UserAlertDao}.
 *
 * @version 1.1
 */
public class MyBatisUserAlertDao extends BaseMyBatisGenericDao<UserAlert, Long> implements UserAlertDao {

	/**
	 * The query name used for
	 * {@link #findAlertsToProcess(UserAlertType, Long, Integer)}.
	 */
	public static final String QUERY_FOR_PROCESSING = "find-UserAlert-for-processing";

	/** The query name used for {@link #findAlertsForUser(Long)}. */
	public static final String QUERY_FOR_USER_WITH_SITUATION = "find-UserAlert-for-user-with-situation";

	/** The query name used for {@link #getAlertSituation(Long)}. */
	public static final String QUERY_FOR_SITUATION = "get-UserAlert-with-situation";

	/** The query name used for {@link #deleteAllAlertsForNode(Long, Long)}. */
	public static final String DELETE_FOR_NODE = "delete-UserAlert-for-node";

	/** The query name used for {@link #updateValidTo(Long, DateTime)}. */
	public static final String UPDATE_VALID_TO = "update-UserAlert-valid-to";

	/** The query name used for {@link #findActiveAlertSituationsForNode(Long)}. */
	public static final String QUERY_ACTIVE_SITUATIONS_FOR_NODE = "find-UserAlert-active-for-node";

	/** The query name used for {@link #findActiveAlertSituationsForUser(Long)}. */
	public static final String QUERY_ACTIVE_SITUATIONS_FOR_USER = "find-UserAlert-active-for-user";

	/** The query name used for {@link #alertSituationCountForUser(Long)}. */
	public static final String QUERY_ACTIVE_SITUATIONS_FOR_USER_COUNT = "find-UserAlert-active-for-user-count";

	/**
	 * Default constructor.
	 */
	public MyBatisUserAlertDao() {
		super(org.eniware.central.user.domain.UserAlert.class, Long.class);
	}

	@Override
	// Propagation.REQUIRED for server-side cursors
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<UserAlert> findAlertsToProcess(UserAlertType type, Long startingId, DateTime validDate,
			Integer max) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("type", type);
		if ( startingId != null ) {
			params.put("startingId", startingId);
		}
		params.put("validDate", (validDate == null ? new DateTime() : validDate));
		return selectList(QUERY_FOR_PROCESSING, params, null, max);
	}

	@Override
	// Propagation.REQUIRED for server-side cursors
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<UserAlert> findAlertsForUser(Long userId) {
		return selectList(QUERY_FOR_USER_WITH_SITUATION, userId, null, null);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int deleteAllAlertsForNode(Long userId, Long nodeId) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("user", userId);
		params.put("node", nodeId);
		return getSqlSession().delete(DELETE_FOR_NODE, params);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserAlert getAlertSituation(Long alertId) {
		return selectFirst(QUERY_FOR_SITUATION, alertId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateValidTo(Long alertId, DateTime validTo) {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("id", alertId);
		params.put("validDate", (validTo == null ? new DateTime() : validTo));
		getSqlSession().update(UPDATE_VALID_TO, params);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserAlert> findActiveAlertSituationsForUser(Long userId) {
		return selectList(QUERY_ACTIVE_SITUATIONS_FOR_USER, userId, null, null);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserAlert> findActiveAlertSituationsForNode(Long nodeId) {
		return selectList(QUERY_ACTIVE_SITUATIONS_FOR_NODE, nodeId, null, null);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public int alertSituationCountForUser(Long userId) {
		Number n = getSqlSession().selectOne(QUERY_ACTIVE_SITUATIONS_FOR_USER_COUNT, userId);
		if ( n != null ) {
			return n.intValue();
		}
		return 0;
	}

}
