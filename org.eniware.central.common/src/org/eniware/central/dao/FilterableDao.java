/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import java.io.Serializable;
import java.util.List;

import org.eniware.central.domain.Filter;
import org.eniware.central.domain.FilterMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;

/**
 * API for DAOs that support filtered queries.
 * 
 * @param <M>
 *        the domain object type
 * @param <PK>
 *        the primary key type
 * @param <F>
 *        the filter type
 * @version 1.0
 */
public interface FilterableDao<M extends FilterMatch<PK>, PK extends Serializable, F extends Filter> {

	/**
	 * API for querying for a filtered set of results from all possible results.
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
	FilterResults<M> findFiltered(F filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max);

}
