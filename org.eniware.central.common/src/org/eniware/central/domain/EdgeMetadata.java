/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;

/**
 * API for Edge metadata.
 * @version 1.0
 */
public interface EdgeMetadata {

	/**
	 * Get the Edge ID.
	 * 
	 * @return the Edge ID
	 */
	Long getEdgeId();

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
