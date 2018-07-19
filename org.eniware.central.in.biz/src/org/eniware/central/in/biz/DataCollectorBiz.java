/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.biz;

import java.util.List;

import org.eniware.central.datum.domain.Datum;
import org.eniware.central.datum.domain.GeneralLocationDatum;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.GeneralEdgeDatum;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilterMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.LocationMatch;
import org.eniware.central.domain.EniwareEdgeMetadataFilter;
import org.eniware.central.domain.EniwareEdgeMetadataFilterMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.domain.SourceLocation;
import org.eniware.central.domain.SourceLocationMatch;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * API for collecting data from eniware Edges.
 * 
 * <p>
 * Serves as a transactional facade to posting data into central system.
 * </p>
 *
 * @version 1.5
 */
public interface DataCollectorBiz {

	/**
	 * Post a new {@link Datum}.
	 * 
	 * @param <D>
	 *        the Datum type
	 * @param datum
	 *        the data to persist
	 * @return the persisted entity
	 * @deprecated See {@link #postGeneralEdgeDatum(Iterable)}
	 */
	@Deprecated
	<D extends Datum> D postDatum(D datum);

	/**
	 * Post a collection of datum of {@link Datum} in a single transaction.
	 * 
	 * @param datums
	 *        the collection of datum
	 * @return the persisted entities, ordered in iterator order of
	 *         {@code datums}
	 * @deprecated See {@link #postGeneralEdgeDatum(Iterable)}
	 */
	@Deprecated
	List<Datum> postDatum(Iterable<Datum> datums);

	/**
	 * Post a collection of {@link GeneralEdgeDatum} in a single transaction.
	 * 
	 * @param datums
	 *        the collection of datums
	 */
	void postGeneralEdgeDatum(Iterable<GeneralEdgeDatum> datums);

	/**
	 * Post a collection of {@link GeneralLocationDatum} in a single
	 * transaction.
	 * 
	 * @param datums
	 *        the collection of datums
	 * @since 1.3
	 */
	void postGeneralLocationDatum(Iterable<GeneralLocationDatum> datums);

	/**
	 * Add metadata to a specific Edge and source. If metadata already exists
	 * for the given Edge and source, the values will be merged such that tags
	 * are only added and only new info values will be added.
	 * 
	 * @param EdgeId
	 *        the Edge ID to add to
	 * @param sourceId
	 *        the source ID to add to
	 * @param meta
	 *        the metadata to add
	 */
	void addGeneralEdgeDatumMetadata(Long EdgeId, String sourceId, GeneralDatumMetadata meta);

	/**
	 * Add metadata to a specific Edge.
	 * 
	 * <p>
	 * If metadata already exists for the given Edge and source, the values will
	 * be merged such that tags are only added and only new info values will be
	 * added.
	 * </p>
	 * 
	 * @param EdgeId
	 *        the Edge ID to add to
	 * @param meta
	 *        the metadata to add
	 * @since 1.5
	 */
	void addEniwareEdgeMetadata(Long EdgeId, GeneralDatumMetadata meta);

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
	 * @since 1.5
	 */
	FilterResults<EniwareEdgeMetadataFilterMatch> findEniwareEdgeMetadata(EniwareEdgeMetadataFilter criteria,
			final List<SortDescriptor> sortDescriptors, final Integer offset, final Integer max);

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
	FilterResults<GeneralEdgeDatumMetadataFilterMatch> findGeneralEdgeDatumMetadata(
			GeneralEdgeDatumMetadataFilter criteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max);

	/**
	 * Search for location datum metadata based on a location criteria. The
	 * location and metadata criteria must both match for results to be
	 * included.
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
	 * @since 1.3
	 */
	FilterResults<GeneralLocationDatumMetadataFilterMatch> findGeneralLocationDatumMetadata(
			GeneralLocationDatumMetadataFilter metadataCriteria, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max);

	/**
	 * Look up PriceLocation objects based on a source name and location name.
	 * 
	 * @param criteria
	 *        the the search criteria
	 * @return the matching location, or <em>null</em> if not found
	 */
	List<SourceLocationMatch> findPriceLocations(SourceLocation criteria);

	/**
	 * Look up price location objects based on a location search filter and sort
	 * options.
	 * 
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * @return the results, never <em>null</em>
	 */
	FilterResults<SourceLocationMatch> findPriceLocations(SourceLocation criteria,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max);

	/**
	 * Look up WeatherLocation objects based on a source name and location.
	 * 
	 * <p>
	 * At a minimum the {@link SourceLocation#getSource()} must be provided in
	 * the supplied criteria.
	 * </p>
	 * 
	 * @param criteria
	 *        the search criteria
	 * @return the matching locations, or an empty list if none found
	 */
	List<SourceLocationMatch> findWeatherLocations(SourceLocation criteria);

	/**
	 * Look up weather location objects based on a location search filter and
	 * sort options.
	 * 
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * @return the results, never <em>null</em>
	 */
	FilterResults<SourceLocationMatch> findWeatherLocations(SourceLocation criteria,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max);

	/**
	 * Look up location objects based on a location search filter.
	 * 
	 * @param criteria
	 *        the search criteria
	 * @return the matching locations, or an empty list if none found
	 */
	List<LocationMatch> findLocations(Location criteria);

}
