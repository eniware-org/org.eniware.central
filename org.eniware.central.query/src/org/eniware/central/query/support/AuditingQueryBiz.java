/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */
package org.eniware.central.query.support;

import java.util.List;

import org.eniware.central.datum.domain.AggregateGeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumFilterMatch;
import org.eniware.central.datum.domain.ReportingGeneralEdgeDatumMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.query.biz.QueryAuditor;
import org.eniware.central.query.biz.QueryBiz;
import org.eniware.util.OptionalService;

/**
 * {@link QueryBiz} implementation that audits query events using a
 * {@link QueryAuditor}.
 * 
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
	public FilterResults<GeneralEdgeDatumFilterMatch> findFilteredGeneralEdgeDatum(
			GeneralEdgeDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		FilterResults<GeneralEdgeDatumFilterMatch> results = super.findFilteredGeneralEdgeDatum(filter,
				sortDescriptors, offset, max);
		QueryAuditor auditor = getQueryAuditor();
		if ( auditor != null ) {
			auditor.auditEdgeDatumFilterResults(filter, results);
		}
		return results;
	}

	@Override
	public FilterResults<ReportingGeneralEdgeDatumMatch> findFilteredAggregateGeneralEdgeDatum(
			AggregateGeneralEdgeDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		FilterResults<ReportingGeneralEdgeDatumMatch> results = super.findFilteredAggregateGeneralEdgeDatum(
				filter, sortDescriptors, offset, max);
		QueryAuditor auditor = getQueryAuditor();
		if ( auditor != null ) {
			auditor.auditEdgeDatumFilterResults(filter, results);
		}
		return results;
	}

}
