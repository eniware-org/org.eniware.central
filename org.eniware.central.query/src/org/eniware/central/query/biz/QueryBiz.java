/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.biz;

import java.util.List;
import java.util.Set;

import org.eniware.central.datum.domain.AggregateGeneralLocationDatumFilter;
import org.eniware.central.datum.domain.AggregateGeneralNodeDatumFilter;
import org.eniware.central.datum.domain.GeneralLocationDatum;
import org.eniware.central.datum.domain.GeneralLocationDatumFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumFilterMatch;
import org.eniware.central.datum.domain.GeneralNodeDatum;
import org.eniware.central.datum.domain.GeneralNodeDatumFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumFilterMatch;
import org.eniware.central.datum.domain.ReportingGeneralLocationDatumMatch;
import org.eniware.central.datum.domain.ReportingGeneralNodeDatumMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.LocationMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.query.domain.ReportableInterval;
import org.joda.time.DateTime;

/**
 * API for querying business logic.
 * 
 * @version 2.0
 */
public interface QueryBiz {

	/**
	 * Get a date interval of available data for a node, optionally limited to a
	 * source ID.
	 * 
	 * <p>
	 * This method can be used to find the earliest and latest dates data is
	 * available for a set of given {@link GeneralNodeDatum}. This could be
	 * useful for reporting UIs that want to display a view of the complete
	 * range of data available.
	 * </p>
	 * <p>
	 * If the {@code sourceId} parameter is <em>null</em> then the returned
	 * interval will be for the node as a whole, for any sources.
	 * </p>
	 * 
	 * @param nodeId
	 *        the ID of the node to look for
	 * @param sourceId
	 *        an optional source ID to find the available interval for
	 * @return ReadableInterval instance, or <em>null</em> if no data available
	 */
	ReportableInterval getReportableInterval(Long nodeId, String sourceId);

	/**
	 * Get the available source IDs for a given node, optionally limited to a
	 * date range.
	 * 
	 * @param nodeId
	 *        the node ID to search for
	 * @param start
	 *        an optional start date (inclusive) to filter on
	 * @param end
	 *        an optional end date (inclusive) to filter on
	 * @return the distinct source IDs available (never <em>null</em>)
	 */
	Set<String> getAvailableSources(Long nodeId, DateTime start, DateTime end);

	/**
	 * API for querying for a filtered set of GeneralNodeDatum results from all
	 * possible results.
	 * 
	 * @param filter
	 *        the query filter
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * @return the results, never <em>null</em>
	 * @since 1.4
	 */
	FilterResults<GeneralNodeDatumFilterMatch> findFilteredGeneralNodeDatum(
			GeneralNodeDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max);

	/**
	 * API for querying for a filtered set of aggregated GeneralNodeDatum
	 * results from all possible results.
	 * 
	 * @param filter
	 *        the query filter
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * @return the results, never <em>null</em>
	 * @since 1.4
	 */
	FilterResults<ReportingGeneralNodeDatumMatch> findFilteredAggregateGeneralNodeDatum(
			AggregateGeneralNodeDatumFilter filter, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max);

	/**
	 * API for querying for a filtered set of
	 * {@link GeneralLocationDatumFilterMatch} results from all possible
	 * results.
	 * 
	 * @param filter
	 *        the query filter
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * @return the results, never <em>null</em>
	 * @since 1.5
	 */
	FilterResults<GeneralLocationDatumFilterMatch> findGeneralLocationDatum(
			GeneralLocationDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max);

	/**
	 * API for querying for a filtered set of aggregated
	 * {@link ReportingGeneralLocationDatumMatch} results from all possible
	 * results.
	 * 
	 * @param filter
	 *        the query filter
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * @return the results, never <em>null</em>
	 * @since 1.5
	 */
	FilterResults<ReportingGeneralLocationDatumMatch> findAggregateGeneralLocationDatum(
			AggregateGeneralLocationDatumFilter filter, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max);

	/**
	 * Get the available source IDs for a given location, optionally limited to
	 * a date range.
	 * 
	 * @param locationId
	 *        the location ID to search for
	 * @param start
	 *        an optional start date (inclusive) to filter on
	 * @param end
	 *        an optional end date (inclusive) to filter on
	 * @return the distinct source IDs available (never <em>null</em>)
	 * @since 1.5
	 */
	Set<String> getLocationAvailableSources(Long locationId, DateTime start, DateTime end);

	/**
	 * Get a date interval of available data for a location, optionally limited
	 * to a source ID.
	 * 
	 * <p>
	 * This method can be used to find the earliest and latest dates data is
	 * available for a set of given {@link GeneralLocationDatum}. This could be
	 * useful for reporting UIs that want to display a view of the complete
	 * range of data available.
	 * </p>
	 * <p>
	 * If the {@code sourceId} parameter is <em>null</em> then the returned
	 * interval will be for the node as a whole, for any sources.
	 * </p>
	 * 
	 * @param locationId
	 *        the ID of the location to look for
	 * @param sourceId
	 *        an optional source ID to find the available interval for
	 * @return ReadableInterval instance, or <em>null</em> if no data available
	 * @since 1.5
	 */
	ReportableInterval getLocationReportableInterval(Long locationId, String sourceId);

	/**
	 * API for querying for a filtered set of locations from all possible
	 * results.
	 * 
	 * @param filter
	 *        the query filter
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * 
	 * @return the results, never <em>null</em>
	 * @since 1.4
	 */
	FilterResults<LocationMatch> findFilteredLocations(Location filter,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max);
}
