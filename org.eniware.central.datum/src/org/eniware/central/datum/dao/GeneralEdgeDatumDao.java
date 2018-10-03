/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.dao;

import java.util.Set;

import org.eniware.central.dao.AggregationFilterableDao;
import org.eniware.central.dao.FilterableDao;
import org.eniware.central.dao.GenericDao;
import org.eniware.central.datum.domain.AggregateGeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatum;
import org.eniware.central.datum.domain.GeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumFilterMatch;
import org.eniware.central.datum.domain.GeneralEdgeDatumPK;
import org.eniware.central.datum.domain.ReportingGeneralEdgeDatumMatch;
import org.joda.time.DateTime;
import org.joda.time.ReadableInterval;

/**
 * DAO API for {@link GeneralEdgeDatum}.
 
 * @version 1.1
 */
public interface GeneralEdgeDatumDao extends GenericDao<GeneralEdgeDatum, GeneralEdgeDatumPK>,
		FilterableDao<GeneralEdgeDatumFilterMatch, GeneralEdgeDatumPK, GeneralEdgeDatumFilter>,
		AggregationFilterableDao<ReportingGeneralEdgeDatumMatch, AggregateGeneralEdgeDatumFilter> {

	/**
	 * Get the interval of available data in the system. Note the returned
	 * interval will be configured with the Edge's local time zone, if
	 * available.
	 * 
	 * @param EdgeId
	 *        the Edge ID to search for
	 * @param sourceId
	 *        an optional source ID to limit the results to, or <em>null</em>
	 *        for all sources
	 * @return interval, or <em>null</em> if no data available
	 */
	ReadableInterval getReportableInterval(Long EdgeId, String sourceId);

	/**
	 * Get the available sources for a given Edge, optionally limited to a date
	 * range.
	 * 
	 * @param EdgeId
	 *        the Edge ID to search for
	 * @param start
	 *        an optional start date (inclusive) to filter on
	 * @param end
	 *        an optional end date (inclusive) to filter on
	 * @return the distinct source IDs available (never <em>null</em>)
	 */
	Set<String> getAvailableSources(Long EdgeId, DateTime start, DateTime end);

	/**
	 * Find the earliest date audit data is available for a given Edge.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param sourceId
	 *        an optional source ID to limit the results to, or {@literal null}
	 *        for all sources
	 * @return the interval, or {@literal null} if no data available
	 * @since 1.1
	 */
	ReadableInterval getAuditInterval(Long EdgeId, String sourceId);

	/**
	 * Get the total audit count of datum property updates for a search
	 * criteria.
	 * 
	 * <p>
	 * The {@code EdgeId}, {@code startDate}, and {@code endDate} values are
	 * required at a minimum. The {@code sourceId} can also be provided.
	 * </p>
	 * 
	 * @param filter
	 *        the filter criteria
	 * @return the total count
	 * @since 1.1
	 */
	long getAuditPropertyCountTotal(GeneralEdgeDatumFilter filter);

}
