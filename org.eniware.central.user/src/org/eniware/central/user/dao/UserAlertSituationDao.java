/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.UserAlert;
import org.eniware.central.user.domain.UserAlertSituation;
import org.joda.time.DateTime;

/**
 * DAO API for UserAlertSituation objects.
 * 
 * @author matt
 * @version 1.0
 */
public interface UserAlertSituationDao extends GenericDao<UserAlertSituation, Long> {

	/**
	 * Get an {@link UserAlertSituation} that is active for a given
	 * {@link UserAlert} ID. If more than one are active, this will return the
	 * most recent one only.
	 * 
	 * @param alertId
	 *        The ID of the {@link UserAlert} to get the active situations for.
	 * @return The found {@link UserAlertSituation}, or <em>null</em> if none
	 *         available.
	 */
	UserAlertSituation getActiveAlertSituationForAlert(Long alertId);

	/**
	 * Purge situations that have reached a final state and are older than a
	 * given date.
	 * 
	 * @param olderThanDate
	 *        The maximum date for which to purge resolved situations.
	 * @return The number of situations deleted.
	 */
	long purgeResolvedSituations(DateTime olderThanDate);

}
