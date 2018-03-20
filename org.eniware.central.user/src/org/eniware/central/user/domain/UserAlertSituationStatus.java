/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

/**
 * An email for {@link UserAlertSituation} status.
 * 
 * @author matt
 * @version 1.0
 */
public enum UserAlertSituationStatus {

	/** The alert situation is active. */
	Active,

	/** The alert situation is resolved: no more notifications will be sent. */
	Resolved;

}
