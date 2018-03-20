/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.biz;

import org.eniware.central.datum.domain.GeneralNodeDatumFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumPK;
import org.eniware.central.domain.FilterMatch;
import org.eniware.central.domain.FilterResults;

/**
 * API for auditing query events in SolarNetwork.
 * 
 * @author matt
 * @version 1.0
 */
public interface QueryAuditor {

	/**
	 * Audit the results of a general node datum query.
	 * 
	 * @param filter
	 *        the criteria used for the query
	 * @param results
	 *        the query results
	 */
	<T extends FilterMatch<GeneralNodeDatumPK>> void auditNodeDatumFilterResults(
			GeneralNodeDatumFilter filter, FilterResults<T> results);

}
