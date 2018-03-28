/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eniware.central.dao.SolarNodeMetadataDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SolarNodeMetadata;
import org.eniware.central.domain.SolarNodeMetadataFilter;
import org.eniware.central.domain.SolarNodeMetadataFilterMatch;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.support.BasicFilterResults;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link SolarNodeMetadataDao}.
 * @version 1.0
 */
public class MyBatisSolarNodeMetadataDao extends BaseMyBatisGenericDao<SolarNodeMetadata, Long>
		implements SolarNodeMetadataDao {

	/** The query parameter for a general {@link Filter} object value. */
	public static final String PARAM_FILTER = "filter";

	/**
	 * Default constructor.
	 */
	public MyBatisSolarNodeMetadataDao() {
		super(SolarNodeMetadata.class, Long.class);
	}

	private Long executeCountQuery(final String countQueryName, final Map<String, ?> sqlProps) {
		Number n = getSqlSession().selectOne(countQueryName, sqlProps);
		if ( n != null ) {
			return n.longValue();
		}
		return null;
	}

	private String getQueryForFilter(SolarNodeMetadataFilter filter) {
		return getQueryForAll() + "-SolarNodeMetadataMatch";
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<SolarNodeMetadataFilterMatch> findFiltered(SolarNodeMetadataFilter filter,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max) {
		final String query = getQueryForFilter(filter);
		Map<String, Object> sqlProps = new HashMap<String, Object>(1);
		sqlProps.put(PARAM_FILTER, filter);
		if ( sortDescriptors != null && sortDescriptors.size() > 0 ) {
			sqlProps.put(SORT_DESCRIPTORS_PROPERTY, sortDescriptors);
		}

		// attempt count first, if max NOT specified as -1
		Long totalCount = null;
		if ( max != null && max.intValue() != -1 ) {
			totalCount = executeCountQuery(query + "-count", sqlProps);
		}

		List<SolarNodeMetadataFilterMatch> rows = selectList(query, sqlProps, offset, max);

		BasicFilterResults<SolarNodeMetadataFilterMatch> results = new BasicFilterResults<SolarNodeMetadataFilterMatch>(
				rows, (totalCount != null ? totalCount : Long.valueOf(rows.size())), offset,
				rows.size());

		return results;
	}
}
