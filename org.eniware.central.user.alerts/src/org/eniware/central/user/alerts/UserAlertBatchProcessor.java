/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.alerts;

import org.eniware.central.user.domain.UserAlert;
import org.joda.time.DateTime;

/**
 * API for batch processing user alerts.
 * 
 * @version 1.0
 */
public interface UserAlertBatchProcessor {

	/**
	 * Process a batch of alerts, optionally starting from the ID of the last
	 * alert processed.
	 * 
	 * @param lastProcessedAlertId
	 *        An optional {@link UserAlert} ID representing the last ID
	 *        processed on a previous batch run.
	 * @param validDate
	 *        The valid date to use when batch processing alerts.
	 * @return The ID of the last {@link UserAlert} processed, or <em>null</em>
	 *         if no more alerts are available to process.
	 */
	Long processAlerts(Long lastProcessedAlertId, DateTime validDate);

}
