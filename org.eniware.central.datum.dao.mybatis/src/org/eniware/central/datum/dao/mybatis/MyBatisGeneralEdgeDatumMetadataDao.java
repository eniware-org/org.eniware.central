/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
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
import org.eniware.central.datum.dao.GeneralEdgeDatumMetadataDao;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadata;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.EdgeSourcePK;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.support.BasicFilterResults;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link GeneralEdgeDatumMetadataDao}.
 *
 * @version 1.1
 */
public class MyBatisGeneralEdgeDatumMetadataDao
		extends BaseMyBatisGenericDao<GeneralEdgeDatumMetadata, EdgeSourcePK>
		implements GeneralEdgeDatumMetadataDao {

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
	public MyBatisGeneralEdgeDatumMetadataDao() {
		super(GeneralEdgeDatumMetadata.class, EdgeSourcePK.class);
	}

	private Long executeCountQuery(final String countQueryName, final Map<String, ?> sqlProps) {
		Number n = getSqlSession().selectOne(countQueryName, sqlProps);
		if ( n != null ) {
			return n.longValue();
		}
		return null;
	}

	private String getQueryForFilter(GeneralEdgeDatumMetadataFilter filter) {
		return getQueryForAll() + "-GeneralEdgeDatumMetadataMatch";
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public FilterResults<GeneralEdgeDatumMetadataFilterMatch> findFiltered(
			GeneralEdgeDatumMetadataFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
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

		List<GeneralEdgeDatumMetadataFilterMatch> rows = selectList(query, sqlProps, offset, max);

		BasicFilterResults<GeneralEdgeDatumMetadataFilterMatch> results = new BasicFilterResults<GeneralEdgeDatumMetadataFilterMatch>(
				rows, (totalCount != null ? totalCount : Long.valueOf(rows.size())), offset,
				rows.size());

		return results;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Set<EdgeSourcePK> getFilteredSources(Long[] EdgeIds, String metadataFilter) {
		Map<String, Object> sqlProps = new HashMap<String, Object>(2);
		sqlProps.put("EdgeIds", EdgeIds);
		sqlProps.put(PARAM_FILTER, metadataFilter);
		List<EdgeSourcePK> rows = selectList(QUERY_FOR_SOURCES, sqlProps, null, null);
		return new LinkedHashSet<EdgeSourcePK>(rows);
	}

}
