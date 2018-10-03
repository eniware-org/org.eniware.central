/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.user.dao.UserAlertSituationDao;
import org.eniware.central.user.domain.UserAlertSituation;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link UserAlertSituationDao}.
 * 
 * @version 1.0
 */
public class MyBatisUserAlertSituationDao extends BaseMyBatisGenericDao<UserAlertSituation, Long>
		implements UserAlertSituationDao {

	/**
	 * The query name used for {@link #getActiveAlertSituationForAlert(Long)}.
	 */
	public static final String QUERY_ACTIVE_FOR_ALERT = "get-UserAlertSituation-for-active-alert";

	/**
	 * The DELETE query name used for {@link #purgeResolvedSituations(DateTime)}
	 * .
	 */
	public static final String UPDATE_PURGE_RESOLVED = "delete-UserAlertSituation-resolved";

	/**
	 * Default constructor.
	 */
	public MyBatisUserAlertSituationDao() {
		super(UserAlertSituation.class, Long.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserAlertSituation getActiveAlertSituationForAlert(Long alertId) {
		return selectFirst(QUERY_ACTIVE_FOR_ALERT, alertId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public long purgeResolvedSituations(DateTime olderThanDate) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("date", olderThanDate);
		getSqlSession().update(UPDATE_PURGE_RESOLVED, params);
		Long result = (Long) params.get("result");
		return (result == null ? 0 : result.longValue());
	}

}
