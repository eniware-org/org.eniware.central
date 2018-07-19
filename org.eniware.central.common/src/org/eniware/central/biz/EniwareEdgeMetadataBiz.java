/* ==================================================================
 * Eniware Open sorce:Nikolai Manchev
 * Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.biz;

import java.util.List;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.EniwareEdgeMetadataFilter;
import org.eniware.central.domain.EniwareEdgeMetadataFilterMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * API for manipulating Edge metadata.
 * @version 1.0
 * @since 1.32
 */
public interface EniwareEdgeMetadataBiz {

	/**
	 * Add metadata to a specific Edge. If metadata already exists for the given
	 * Edge, the values will be merged such that tags are added and info values
	 * are added or updated.
	 * 
	 * @param EdgeId
	 *        the Edge ID to add to
	 * @param meta
	 *        the metadata to add
	 */
	void addEniwareEdgeMetadata(Long EdgeId, GeneralDatumMetadata meta);

	/**
	 * Store metadata to a specific Edge, replacing any existing metadata with
	 * the provided metadata.
	 * 
	 * @param EdgeId
	 *        the Edge ID to add to
	 * @param meta
	 *        the metadata to store
	 */
	void storeEniwareEdgeMetadata(Long EdgeId, GeneralDatumMetadata meta);

	/**
	 * Remove all metadata to a specific Edge.
	 * 
	 * @param EdgeId
	 *        the Edge ID to remove from
	 */
	void removeEniwareEdgeMetadata(Long EdgeId);

	/**
	 * Search for Edge metadata.
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
	FilterResults<EniwareEdgeMetadataFilterMatch> findEniwareEdgeMetadata(EniwareEdgeMetadataFilter criteria,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max);

}
