/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.dao.mybatis;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.datum.dao.GeneralNodeDatumMetadataDao;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadata;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.NodeSourcePK;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.support.BasicFilterResults;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link GeneralNodeDatumMetadataDao}.
 *
 * @version 1.1
 */
public class MyBatisGeneralNodeDatumMetadataDao
		extends BaseMyBatisGenericDao<GeneralNodeDatumMetadata, NodeSourcePK>
		implements GeneralNodeDatumMetadataDao {

	/** The query parameter for a general {@link Filter} object value. */
	public static final String PARAM_FILTER = "filter";

	/**
	 * The default query name used for
	 * {@link #getFilteredSources(Long[], String)}.
	 * 
	 * @since 1.1
	 */
	public static final String QUERY_FOR_SOURCES = "find-metadata-distinct-sources";

	/**
	 * Default constructor.
	 */
	public MyBatisGeneralNodeDatumMetadataDao() {
		super(GeneralNodeDatumMetadata.class, NodeSourcePK.class);
	}

	private Long executeCountQuery(final String countQueryName, final Map<String, ?> sqlProps) {
		Number n = getSqlSession().selectOne(countQueryName, sqlProps);
		if ( n != null ) {
			return n.longValue();
		}
		return null;
	}

	private String getQueryForFilter(GeneralNodeDatumMetadataFilter filter) {
		return getQueryForAll() + "-GeneralNodeDatumMetadataMatch";
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public FilterResults<GeneralNodeDatumMetadataFilterMatch> findFiltered(
			GeneralNodeDatumMetadataFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
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

		List<GeneralNodeDatumMetadataFilterMatch> rows = selectList(query, sqlProps, offset, max);

		BasicFilterResults<GeneralNodeDatumMetadataFilterMatch> results = new BasicFilterResults<GeneralNodeDatumMetadataFilterMatch>(
				rows, (totalCount != null ? totalCount : Long.valueOf(rows.size())), offset,
				rows.size());

		return results;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Set<NodeSourcePK> getFilteredSources(Long[] nodeIds, String metadataFilter) {
		Map<String, Object> sqlProps = new HashMap<String, Object>(2);
		sqlProps.put("nodeIds", nodeIds);
		sqlProps.put(PARAM_FILTER, metadataFilter);
		List<NodeSourcePK> rows = selectList(QUERY_FOR_SOURCES, sqlProps, null, null);
		return new LinkedHashSet<NodeSourcePK>(rows);
	}

}
