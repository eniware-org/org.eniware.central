/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import java.util.List;

import org.eniware.central.domain.AggregationFilter;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;

/**
 * API for DAOs that support filtered queries of aggregate data.
 * 
 * @param <M>
 *        the result match type
 * @param <F>
 *        the filter type
 * @version 1.0
 */
public interface AggregationFilterableDao<M, F extends AggregationFilter> {

	/**
	 * API for querying for a filtered set of aggregated results from all
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
	 */
	FilterResults<M> findAggregationFiltered(F filter, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max);

}
