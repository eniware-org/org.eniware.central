/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */
package org.eniware.central.query.support;

import java.util.List;
import java.util.Set;

import org.eniware.central.datum.domain.AggregateGeneralLocationDatumFilter;
import org.eniware.central.datum.domain.AggregateGeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumFilterMatch;
import org.eniware.central.datum.domain.GeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumFilterMatch;
import org.eniware.central.datum.domain.ReportingGeneralLocationDatumMatch;
import org.eniware.central.datum.domain.ReportingGeneralEdgeDatumMatch;
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
	public ReportableInterval getReportableInterval(Long EdgeId, String sourceId) {
		return delegate.getReportableInterval(EdgeId, sourceId);
	}

	@Override
	public Set<String> getAvailableSources(Long EdgeId, DateTime start, DateTime end) {
		return delegate.getAvailableSources(EdgeId, start, end);
	}

	@Override
	public FilterResults<GeneralEdgeDatumFilterMatch> findFilteredGeneralEdgeDatum(
			GeneralEdgeDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		return delegate.findFilteredGeneralEdgeDatum(filter, sortDescriptors, offset, max);
	}

	@Override
	public FilterResults<ReportingGeneralEdgeDatumMatch> findFilteredAggregateGeneralEdgeDatum(
			AggregateGeneralEdgeDatumFilter filter, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		return delegate.findFilteredAggregateGeneralEdgeDatum(filter, sortDescriptors, offset, max);
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
