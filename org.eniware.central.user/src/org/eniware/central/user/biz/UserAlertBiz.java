/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.biz;

import java.util.List;

import org.eniware.central.user.domain.UserAlert;
import org.eniware.central.user.domain.UserAlertSituation;
import org.eniware.central.user.domain.UserAlertSituationStatus;

/**
 * API for user alert tasks.
 * 
 * @version 1.1
 */
public interface UserAlertBiz {

	/**
	 * Get all available alerts for a given user. The
	 * {@link UserAlert#getSituation()} property will be populated with the most
	 * recently available <em>active</em> {@link UserAlertSituation}, if one
	 * exists.
	 * 
	 * @param userId
	 *        The ID of the user to get alerts for.
	 * @return List of alerts, or an empty list if none available.
	 */
	List<UserAlert> userAlertsForUser(Long userId);

	/**
	 * Save an alert. This method can be used to create new alerts or update
	 * existing alerts.
	 * 
	 * @param alert
	 *        The alert to save.
	 * @return The primary key of the saved alert.
	 */
	Long saveAlert(UserAlert alert);

	/**
	 * Delete an alert.
	 * 
	 * @param alertId
	 *        The ID of the alert to delete.
	 * @since 1.1
	 */
	void deleteAlert(Long alertId);

	/**
	 * Get an alert with the most recently available <em>active</em>
	 * {@link UserAlertSituation} populated, if one exists.
	 * 
	 * @param alertId
	 *        The ID of the alert to get.
	 * @return The alert, or <em>null</em> if not available.
	 */
	UserAlert alertSituation(Long alertId);

	/**
	 * Update an alert <em>active</em> situation's status. If the given alert
	 * does not have an active situation, nothing will be updated.
	 * 
	 * @param alertId
	 *        The ID of the alert to update the situation status of.
	 * @param status
	 *        The status to update the situation to.
	 * @return The updated alert, or <em>null</em> if not available. The
	 *         {@link UserAlertSituation} will be populated, if one was updated.
	 */
	UserAlert updateSituationStatus(Long alertId, UserAlertSituationStatus status);

	/**
	 * Get a count of <em>active</em> alert situations for a given user.
	 * 
	 * @param userId
	 *        The ID of the user to get the alert situation count for.
	 * @return The number of active alert situations for the given user.
	 * @since 1.1
	 */
	int alertSituationCountForUser(Long userId);

	/**
	 * Get all available <em>active</em> alert situations for a given user. The
	 * {@link UserAlert#getSituation()} property will be populated with matching
	 * {@link UserAlertSituation}.
	 * 
	 * @param userId
	 *        The ID of the user to get alert situations for.
	 * @return List of alerts, or an empty list if none available.
	 * @since 1.1
	 */
	List<UserAlert> alertSituationsForUser(Long userId);

	/**
	 * Get all available <em>active</em> alert situations for a given Edge. The
	 * {@link UserAlert#getSituation()} property will be populated with matching
	 * {@link UserAlertSituation}.
	 * 
	 * @param EdgeId
	 *        The ID of the Edge to get alert situations for.
	 * @return List of alerts, or an empty list if none available.
	 * @since 1.1
	 */
	List<UserAlert> alertSituationsForEdge(Long EdgeId);

}
