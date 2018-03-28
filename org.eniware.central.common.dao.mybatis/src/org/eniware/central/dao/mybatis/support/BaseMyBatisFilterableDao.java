/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eniware.central.dao.FilterableDao;
import org.eniware.central.domain.Entity;
import org.eniware.central.domain.Filter;
import org.eniware.central.domain.FilterMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.support.BasicFilterResults;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base MyBatis {@link FilterableDao} implementation.
 * @version 1.1
 */
public abstract class BaseMyBatisFilterableDao<T extends Entity<PK>, M extends FilterMatch<PK>, F extends Filter, PK extends Serializable>
		extends BaseMyBatisGenericDao<T, PK> implements FilterableDao<M, PK, F> {

	/** A query property for a general Filter object value. */
	public static final String FILTER_PROPERTY = "filter";

	private final Class<? extends M> filterResultClass;

	/**
	 * Constructor.
	 * 
	 * @param domainClass
	 *        the domain class
	 * @param pkClass
	 *        the primary key class
	 */
	public BaseMyBatisFilterableDao(Class<? extends T> domainClass, Class<? extends PK> pkClass,
			Class<? extends M> filterResultClass) {
		super(domainClass, pkClass);
		this.filterResultClass = filterResultClass;
	}

	/**
	 * Get the filter query name for a given domain.
	 * 
	 * @param filterDomain
	 *        the domain
	 * @return query name
	 */
	protected String getFilteredQuery(String filterDomain, F filter) {
		return getQueryForAll() + "-" + filterDomain;
	}

	/**
	 * Callback to alter the default SQL properties set up by
	 * {@link #findFiltered(Filter, List, Integer, Integer)}.
	 * 
	 * @param filter
	 *        the current filter
	 * @param sqlProps
	 *        the properties
	 */
	protected void postProcessFilterProperties(F filter, Map<String, Object> sqlProps) {
		// nothing here, extending classes can implement
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<M> findFiltered(F filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		final String filterDomain = getMemberDomainKey(filterResultClass);
		final String query = getFilteredQuery(filterDomain, filter);
		Map<String, Object> sqlProps = new HashMap<String, Object>(1);
		sqlProps.put(FILTER_PROPERTY, filter);
		if ( sortDescriptors != null && sortDescriptors.size() > 0 ) {
			sqlProps.put(SORT_DESCRIPTORS_PROPERTY, sortDescriptors);
		}
		postProcessFilterProperties(filter, sqlProps);

		// attempt count first, if max NOT specified as -1
		Long totalCount = null;
		if ( max != null && max.intValue() != -1 ) {
			Number n = selectLong(query + "-count", sqlProps);
			if ( n != null ) {
				totalCount = n.longValue();
			}
		}

		List<M> rows = selectList(query, sqlProps, offset, max);

		BasicFilterResults<M> results = new BasicFilterResults<M>(rows,
				(totalCount != null ? totalCount : Long.valueOf(rows.size())), offset, rows.size());

		return results;
	}

}
