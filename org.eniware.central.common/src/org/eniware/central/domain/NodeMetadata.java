/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;

/**
 * API for node metadata.
 * @version 1.0
 */
public interface NodeMetadata {

	/**
	 * Get the node ID.
	 * 
	 * @return the node ID
	 */
	Long getNodeId();

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
