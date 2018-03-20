/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */
package org.eniware.central.query.support;

import java.util.List;

import org.eniware.central.datum.domain.AggregateGeneralNodeDatumFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumFilterMatch;
import org.eniware.central.datum.domain.ReportingGeneralNodeDatumMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.query.biz.QueryAuditor;
import org.eniware.central.query.biz.QueryBiz;
import org.eniware.util.OptionalService;

/**
 * {@link QueryBiz} implementation that audits query events using a
 * {@link QueryAuditor}.
 * 
 * @author matt
 * @version 1.0
 */
public class AuditingQueryBiz extends DelegatingQueryBiz {

	private final OptionalService<QueryAuditor> queryAuditor;

	/**
	 * Constructor.
	 * 
	 * @param delegate
	 *        the delegate
	 * @param queryAuditor
	 *        the query auditor service to use
	 */
	public AuditingQueryBiz(QueryBiz delegate, OptionalService<QueryAuditor> queryAuditor) {
		super(delegate);
		this.queryAuditor = queryAuditor;
	}

	private QueryAuditor getQueryAuditor() {
		return (queryAuditor != null ? queryAuditor.service() : null);
	}

	@Override
	public FilterResults<GeneralNodeDatumFilterMatch> findFilteredGeneralNodeDatum(
			GeneralNodeDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		FilterResults<GeneralNodeDatumFilterMatch> results = super.findFilteredGeneralNodeDatum(filter,
				sortDescriptors, offset, max);
		QueryAuditor auditor = getQueryAuditor();
		if ( auditor != null ) {
			auditor.auditNodeDatumFilterResults(filter, results);
		}
		return results;
	}

	@Override
	public FilterResults<ReportingGeneralNodeDatumMatch> findFilteredAggregateGeneralNodeDatum(
			AggregateGeneralNodeDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		FilterResults<ReportingGeneralNodeDatumMatch> results = super.findFilteredAggregateGeneralNodeDatum(
				filter, sortDescriptors, offset, max);
		QueryAuditor auditor = getQueryAuditor();
		if ( auditor != null ) {
			auditor.auditNodeDatumFilterResults(filter, results);
		}
		return results;
	}

}
