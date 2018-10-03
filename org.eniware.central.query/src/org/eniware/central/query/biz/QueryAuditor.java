/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.biz;

import org.eniware.central.datum.domain.GeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumPK;
import org.eniware.central.domain.FilterMatch;
import org.eniware.central.domain.FilterResults;

/**
 * API for auditing query events in EniwareNetwork.
 * 
 * @version 1.0
 */
public interface QueryAuditor {

	/**
	 * Audit the results of a general Edge datum query.
	 * 
	 * @param filter
	 *        the criteria used for the query
	 * @param results
	 *        the query results
	 */
	<T extends FilterMatch<GeneralEdgeDatumPK>> void auditEdgeDatumFilterResults(
			GeneralEdgeDatumFilter filter, FilterResults<T> results);

}
