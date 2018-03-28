/* ==================================================================
 * Eniware Open sorce:Nikolai Manchev
 * Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.biz;

import java.util.List;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SolarNodeMetadataFilter;
import org.eniware.central.domain.SolarNodeMetadataFilterMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * API for manipulating node metadata.
 * @version 1.0
 * @since 1.32
 */
public interface SolarNodeMetadataBiz {

	/**
	 * Add metadata to a specific node. If metadata already exists for the given
	 * node, the values will be merged such that tags are added and info values
	 * are added or updated.
	 * 
	 * @param nodeId
	 *        the node ID to add to
	 * @param meta
	 *        the metadata to add
	 */
	void addSolarNodeMetadata(Long nodeId, GeneralDatumMetadata meta);

	/**
	 * Store metadata to a specific node, replacing any existing metadata with
	 * the provided metadata.
	 * 
	 * @param nodeId
	 *        the node ID to add to
	 * @param meta
	 *        the metadata to store
	 */
	void storeSolarNodeMetadata(Long nodeId, GeneralDatumMetadata meta);

	/**
	 * Remove all metadata to a specific node.
	 * 
	 * @param nodeId
	 *        the node ID to remove from
	 */
	void removeSolarNodeMetadata(Long nodeId);

	/**
	 * Search for node metadata.
	 * 
	 * @param criteria
	 *        the search criteria
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * @return the results, never <em>null</em>
	 */
	FilterResults<SolarNodeMetadataFilterMatch> findSolarNodeMetadata(SolarNodeMetadataFilter criteria,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max);

}
