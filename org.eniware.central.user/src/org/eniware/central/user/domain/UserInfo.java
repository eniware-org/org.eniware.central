/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import java.util.Map;

/**
 * API for user information.
 *
 * @version 1.0
 * @since 1.25
 */
public interface UserInfo {

	/**
	 * Get the unique ID of the user.
	 * 
	 * @return the ID
	 */
	Long getId();

	/**
	 * Get the full name.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Get the email.
	 * 
	 * @return the email
	 */
	String getEmail();

	/**
	 * Get the enabled flag.
	 * 
	 * @return the enabled flag
	 */
	Boolean getEnabled();

	/**
	 * Get the user's location ID.
	 * 
	 * @return the location ID, or {@literal null} if not available
	 */
	Long getLocationId();

	/**
	 * Get the internal data.
	 * 
	 * <p>
	 * This data object is arbitrary information needed for internal use, for
	 * example metadata like external billing account IDs to integrate with an
	 * external billing system.
	 * </p>
	 * 
	 * @return the internal data
	 */
	Map<String, Object> getInternalData();

}
