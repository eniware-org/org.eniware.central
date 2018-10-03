/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

/**
 * Enum for {@link UserAlert} status.
 *
 * @version 1.0
 */
public enum UserAlertStatus {

	/** The alert is active. */
	Active,

	/** The alert is disabled, and will not be triggered. */
	Disabled,

	/** The alert is active, but will not trigger any notifications. */
	Suppressed;

}
