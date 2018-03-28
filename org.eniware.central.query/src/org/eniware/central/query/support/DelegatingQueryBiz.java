/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */
package org.eniware.central.query.support;

import java.util.List;
import java.util.Set;

import org.eniware.central.datum.domain.AggregateGeneralLocationDatumFilter;
import org.eniware.central.datum.domain.AggregateGeneralNodeDatumFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumFilterMatch;
import org.eniware.central.datum.domain.GeneralNodeDatumFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumFilterMatch;
import org.eniware.central.datum.domain.ReportingGeneralLocationDatumMatch;
import org.eniware.central.datum.domain.ReportingGeneralNodeDatumMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.LocationMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.query.biz.QueryBiz;
import org.eniware.central.query.domain.ReportableInterval;
import org.joda.time.DateTime;

/**
 * Delegating implementation of {@link QueryBiz}, mostly to help with AOP.
 * 
 * @version 2.0
 */
public class DelegatingQueryBiz implements QueryBiz {

	private final QueryBiz delegate;

	/**
	 * Construct with a delegate.
	 * 
	 * @param delegate
	 *        the delegate
	 */
	public DelegatingQueryBiz(QueryBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public ReportableInterval getReportableInterval(Long nodeId, String sourceId) {
		return delegate.getReportableInterval(nodeId, sourceId);
	}

	@Override
	public Set<String> getAvailableSources(Long nodeId, DateTime start, DateTime end) {
		return delegate.getAvailableSources(nodeId, start, end);
	}

	@Override
	public FilterResults<GeneralNodeDatumFilterMatch> findFilteredGeneralNodeDatum(
			GeneralNodeDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		return delegate.findFilteredGeneralNodeDatum(filter, sortDescriptors, offset, max);
	}

	@Override
	public FilterResults<ReportingGeneralNodeDatumMatch> findFilteredAggregateGeneralNodeDatum(
			AggregateGeneralNodeDatumFilter filter, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		return delegate.findFilteredAggregateGeneralNodeDatum(filter, sortDescriptors, offset, max);
	}

	@Override
	public FilterResults<LocationMatch> findFilteredLocations(Location filter,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max) {
		return delegate.findFilteredLocations(filter, sortDescriptors, offset, max);
	}

	@Override
	public FilterResults<GeneralLocationDatumFilterMatch> findGeneralLocationDatum(
			GeneralLocationDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		return delegate.findGeneralLocationDatum(filter, sortDescriptors, offset, max);
	}

	@Override
	public FilterResults<ReportingGeneralLocationDatumMatch> findAggregateGeneralLocationDatum(
			AggregateGeneralLocationDatumFilter filter, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		return delegate.findAggregateGeneralLocationDatum(filter, sortDescriptors, offset, max);
	}

	@Override
	public Set<String> getLocationAvailableSources(Long locationId, DateTime start, DateTime end) {
		return delegate.getLocationAvailableSources(locationId, start, end);
	}

	@Override
	public ReportableInterval getLocationReportableInterval(Long locationId, String sourceId) {
		return delegate.getLocationReportableInterval(locationId, sourceId);
	}

}
