/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.biz;

import java.util.List;
import java.util.Set;

import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.LocationSourcePK;
import org.eniware.central.datum.domain.NodeSourcePK;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * API for manipulating general datum metadata.
 
 * @version 1.2
 */
public interface DatumMetadataBiz {

	/**
	 * Add metadata to a specific node and source. If metadata already exists
	 * for the given node and source, the values will be merged such that tags
	 * are added and info values are added or updated.
	 * 
	 * @param nodeId
	 *        the node ID to add to
	 * @param sourceId
	 *        the source ID to add to
	 * @param meta
	 *        the metadata to add
	 */
	void addGeneralNodeDatumMetadata(Long nodeId, String sourceId, GeneralDatumMetadata meta);

	/**
	 * Store metadata to a specific node and source, replacing any existing
	 * metadata with the provided metadata.
	 * 
	 * @param nodeId
	 *        the node ID to add to
	 * @param sourceId
	 *        the source ID to add to
	 * @param meta
	 *        the metadata to store
	 */
	void storeGeneralNodeDatumMetadata(Long nodeId, String sourceId, GeneralDatumMetadata meta);

	/**
	 * Remove all metadata to a specific node and source.
	 * 
	 * @param nodeId
	 *        the node ID to remove from
	 * @param sourceId
	 *        the source ID to remove from
	 */
	void removeGeneralNodeDatumMetadata(Long nodeId, String sourceId);

	/**
	 * Search for datum metadata.
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
	FilterResults<GeneralNodeDatumMetadataFilterMatch> findGeneralNodeDatumMetadata(
			GeneralNodeDatumMetadataFilter criteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max);

	/**
	 * Add metadata to a specific location and source. If metadata already
	 * exists for the given location and source, the values will be merged such
	 * that tags are added and info values are added or updated.
	 * 
	 * @param locationId
	 *        the location ID to add to
	 * @param sourceId
	 *        the source ID to add to
	 * @param meta
	 *        the metadata to add
	 * @since 1.1
	 */
	void addGeneralLocationDatumMetadata(Long locationId, String sourceId, GeneralDatumMetadata meta);

	/**
	 * Store metadata to a specific location and source, replacing any existing
	 * metadata with the provided metadata.
	 * 
	 * @param locationId
	 *        the location ID to add to
	 * @param sourceId
	 *        the source ID to add to
	 * @param meta
	 *        the metadata to store
	 * @since 1.1
	 */
	void storeGeneralLocationDatumMetadata(Long locationId, String sourceId, GeneralDatumMetadata meta);

	/**
	 * Remove all metadata to a specific location and source.
	 * 
	 * @param locationId
	 *        the location ID to remove from
	 * @param sourceId
	 *        the source ID to remove from
	 * @since 1.1
	 */
	void removeGeneralLocationDatumMetadata(Long locationId, String sourceId);

	/**
	 * Search for datum metadata.
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
	 * @since 1.1
	 */
	FilterResults<GeneralLocationDatumMetadataFilterMatch> findGeneralLocationDatumMetadata(
			GeneralLocationDatumMetadataFilter criteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max);

	/**
	 * Find available datum source IDs that match a datum metadata filter.
	 * 
	 * The metadata filter must be expressed in LDAP search filter style, using
	 * JSON pointer style paths for keys, for example {@code (/m/foo=bar)},
	 * {@code (t=foo)}, or {@code (&(&#47;**&#47;foo=bar)(t=special))}.
	 * 
	 * @param nodeIds
	 *        the node IDs to search for
	 * @param metadataFilter
	 *        A metadata search filter, in LDAP search filter syntax.
	 * @return the distinct node ID and source IDs combinations that match the
	 *         given filter (never <em>null</em>)
	 * @since 1.2
	 */
	Set<NodeSourcePK> getGeneralNodeDatumMetadataFilteredSources(Long[] nodeIds, String metadataFilter);

	/**
	 * Find available location source IDs that match a location metadata filter.
	 * 
	 * The metadata filter must be expressed in LDAP search filter style, using
	 * JSON pointer style paths for keys, for example {@code (/m/foo=bar)},
	 * {@code (t=foo)}, or {@code (&(&#47;**&#47;foo=bar)(t=special))}.
	 * 
	 * @param locationIds
	 *        the node IDs to search for
	 * @param metadataFilter
	 *        A metadata search filter, in LDAP search filter syntax.
	 * @return the distinct node ID and source IDs combinations that match the
	 *         given filter (never <em>null</em>)
	 * @since 1.2
	 */
	Set<LocationSourcePK> getGeneralLocationDatumMetadataFilteredSources(Long[] locationIds,
			String metadataFilter);

}
