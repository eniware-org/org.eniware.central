/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import java.util.List;

import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.UserAlert;
import org.eniware.central.user.domain.UserAlertSituation;
import org.eniware.central.user.domain.UserAlertType;
import org.joda.time.DateTime;

/**
 * DAO API for UserAlert objects.
 * 
 * @version 1.1
 */
public interface UserAlertDao extends GenericDao<UserAlert, Long> {

	/**
	 * Find a set of alerts that need processing. The results are sorted by ID
	 * in ascending order.
	 * 
	 * @param type
	 *        The type of alert to find.
	 * @param startingId
	 *        An optional {@link UserAlert} ID value to start from. Only alerts
	 *        with an ID value <em>higher</em> than this ID will be considered.
	 *        If <em>null</em> then consider all alerts.
	 * @param validDate
	 *        A timestamp to use for the validity check date. If
	 *        {@code startingId} is provided, this value can be provided to
	 *        issue a stable batch query based on the same valid date as the
	 *        previous call to this method. If not provided the current time
	 *        will be used, but then a subsequent batch call might not have the
	 *        same date if another batch call is needed. Therefore it is
	 *        recommended to always pass a value for this parameter.
	 * @param max
	 *        An optional maximum number of result rows to return.
	 * @return The found alerts, or an empty list if none found.
	 */
	List<UserAlert> findAlertsToProcess(UserAlertType type, Long startingId, DateTime validDate,
			Integer max);

	/**
	 * Get a set of all alerts configured for a user. The alerts will have the
	 * most recently available active {@link UserAlertSituation} populated on
	 * the {@link UserAlert#getSituation()} property.
	 * 
	 * @param userId
	 *        The ID of the user to get all alerts for.
	 * @return The found alerts, or an empty list if none found.
	 */
	List<UserAlert> findAlertsForUser(Long userId);

	/**
	 * Delete all alerts configured for a given user and Edge.
	 * 
	 * @param userId
	 *        The ID of the owner of the alerts.
	 * @param EdgeId
	 *        The ID of the Edge.
	 * @return The count of alerts deleted.
	 * @since 1.1
	 */
	int deleteAllAlertsForEdge(Long userId, Long EdgeId);

	/**
	 * Get a specific alert with the most recently available active
	 * {@link UserAlertSituation} populated on the
	 * {@link UserAlert#getSituation()} property.
	 * 
	 * @param alertId
	 *        The ID of the alert to get.
	 * @return The found alert, or <em>null</em> if not available.
	 */
	UserAlert getAlertSituation(Long alertId);

	/**
	 * Update the {@code validTo} property to a new date.
	 * 
	 * @param alertId
	 *        The ID of the alert to update.
	 * @param validTo
	 *        The new value for the {@code validTo} property.
	 * @since 1.1
	 */
	void updateValidTo(Long alertId, DateTime validTo);

	/**
	 * Get all available active situations for a given user. The situations are
	 * returned as {@link UserAlert} entities with the
	 * {@link UserAlert#getSituation()} populated.
	 * 
	 * @param userId
	 *        The ID of the user to get all active situations for.
	 * @return The found alerts with active situations.
	 * @since 1.1
	 */
	List<UserAlert> findActiveAlertSituationsForUser(Long userId);

	/**
	 * Get all available active situations for a given Edge. The situations are
	 * returned as {@link UserAlert} entities with the
	 * {@link UserAlert#getSituation()} populated.
	 * 
	 * @param EdgeId
	 *        The ID of the Edge to get all active situations for.
	 * @return The found alerts with active situations.
	 * @since 1.1
	 */
	List<UserAlert> findActiveAlertSituationsForEdge(Long EdgeId);

	/**
	 * Get a count of <em>active</em> alert situations for a given user.
	 * 
	 * @param userId
	 *        The ID of the user to get the alert situation count for.
	 * @return The number of active alert situations for the given user.
	 * @since 1.1
	 */
	int alertSituationCountForUser(Long userId);

}
