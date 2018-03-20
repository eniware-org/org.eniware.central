/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.dao;

import java.util.Set;

import org.eniware.central.dao.AggregationFilterableDao;
import org.eniware.central.dao.FilterableDao;
import org.eniware.central.dao.GenericDao;
import org.eniware.central.datum.domain.AggregateGeneralLocationDatumFilter;
import org.eniware.central.datum.domain.DatumMappingInfo;
import org.eniware.central.datum.domain.GeneralLocationDatum;
import org.eniware.central.datum.domain.GeneralLocationDatumFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumFilterMatch;
import org.eniware.central.datum.domain.GeneralLocationDatumPK;
import org.eniware.central.datum.domain.LocationDatum;
import org.eniware.central.datum.domain.ReportingGeneralLocationDatumMatch;
import org.joda.time.DateTime;
import org.joda.time.ReadableInterval;

/**
 * DAO API for {@link GeneralLocationDatum}.
 * 
 * @author matt
 * @version 1.1
 */
public interface GeneralLocationDatumDao
		extends
		GenericDao<GeneralLocationDatum, GeneralLocationDatumPK>,
		FilterableDao<GeneralLocationDatumFilterMatch, GeneralLocationDatumPK, GeneralLocationDatumFilter>,
		AggregationFilterableDao<ReportingGeneralLocationDatumMatch, AggregateGeneralLocationDatumFilter> {

	/**
	 * Get the interval of available data in the system. Note the returned
	 * interval will be configured with the location's local time zone, if
	 * available.
	 * 
	 * @param locationId
	 *        the location ID to search for
	 * @param sourceId
	 *        an optional source ID to limit the results to, or <em>null</em>
	 *        for all sources
	 * @return interval, or <em>null</em> if no data available
	 */
	ReadableInterval getReportableInterval(Long locationId, String sourceId);

	/**
	 * Get the available sources for a given location, optionally limited to a
	 * date range.
	 * 
	 * @param locationId
	 *        the location ID to search for
	 * @param start
	 *        an optional start date (inclusive) to filter on
	 * @param end
	 *        an optional end date (inclusive) to filter on
	 * @return the distinct source IDs available (never <em>null</em>)
	 */
	Set<String> getAvailableSources(Long locationId, DateTime start, DateTime end);

	/**
	 * Get mapping info for a given {@link LocationDatum} to help migrate the
	 * datum into a {@link GeneralLocationDatum} object.
	 * 
	 * @param datum
	 *        The datum being mapped.
	 * @return The mapping info, or {@code null} if no mapping info is
	 *         available.
	 * @since 1.1
	 */
	DatumMappingInfo getMappingInfo(LocationDatum datum);

}
