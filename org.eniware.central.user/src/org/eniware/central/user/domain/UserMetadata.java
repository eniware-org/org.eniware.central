/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;

/**
 * API for user metadata.
 * 
 * @version 1.0
 */
public interface UserMetadata {

	/**
	 * Get the user ID.
	 * 
	 * @return the user ID
	 */
	Long getUserId();

	/**
	 * Get the creation date.
	 * 
	 * @return the creation date
	 */
	DateTime getCreated();

	/**
	 * Get the updated date.
	 * 
	 * @return the updated date
	 */
	DateTime getUpdated();

	/**
	 * Get the metadata.
	 * 
	 * @return the metadata
	 */
	GeneralDatumMetadata getMetadata();

}
