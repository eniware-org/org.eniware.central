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
import org.eniware.central.datum.domain.AggregateGeneralNodeDatumFilter;
import org.eniware.central.datum.domain.GeneralNodeDatum;
import org.eniware.central.datum.domain.GeneralNodeDatumFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumFilterMatch;
import org.eniware.central.datum.domain.GeneralNodeDatumPK;
import org.eniware.central.datum.domain.ReportingGeneralNodeDatumMatch;
import org.joda.time.DateTime;
import org.joda.time.ReadableInterval;

/**
 * DAO API for {@link GeneralNodeDatum}.
 
 * @version 1.1
 */
public interface GeneralNodeDatumDao extends GenericDao<GeneralNodeDatum, GeneralNodeDatumPK>,
		FilterableDao<GeneralNodeDatumFilterMatch, GeneralNodeDatumPK, GeneralNodeDatumFilter>,
		AggregationFilterableDao<ReportingGeneralNodeDatumMatch, AggregateGeneralNodeDatumFilter> {

	/**
	 * Get the interval of available data in the system. Note the returned
	 * interval will be configured with the node's local time zone, if
	 * available.
	 * 
	 * @param nodeId
	 *        the node ID to search for
	 * @param sourceId
	 *        an optional source ID to limit the results to, or <em>null</em>
	 *        for all sources
	 * @return interval, or <em>null</em> if no data available
	 */
	ReadableInterval getReportableInterval(Long nodeId, String sourceId);

	/**
	 * Get the available sources for a given node, optionally limited to a date
	 * range.
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
	 * Find the earliest date audit data is available for a given node.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param sourceId
	 *        an optional source ID to limit the results to, or {@literal null}
	 *        for all sources
	 * @return the interval, or {@literal null} if no data available
	 * @since 1.1
	 */
	ReadableInterval getAuditInterval(Long nodeId, String sourceId);

	/**
	 * Get the total audit count of datum property updates for a search
	 * criteria.
	 * 
	 * <p>
	 * The {@code nodeId}, {@code startDate}, and {@code endDate} values are
	 * required at a minimum. The {@code sourceId} can also be provided.
	 * </p>
	 * 
	 * @param filter
	 *        the filter criteria
	 * @return the total count
	 * @since 1.1
	 */
	long getAuditPropertyCountTotal(GeneralNodeDatumFilter filter);

}
