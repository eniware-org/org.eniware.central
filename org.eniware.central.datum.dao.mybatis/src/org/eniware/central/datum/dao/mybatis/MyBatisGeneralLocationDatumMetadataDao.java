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
import org.eniware.central.datum.dao.GeneralLocationDatumMetadataDao;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadata;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.LocationSourcePK;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.support.BasicFilterResults;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link GeneralLocationDatumMetadataDao}.
 * 
 * @author matt
 * @version 1.1
 */
public class MyBatisGeneralLocationDatumMetadataDao
		extends BaseMyBatisGenericDao<GeneralLocationDatumMetadata, LocationSourcePK>
		implements GeneralLocationDatumMetadataDao {

	/** The query parameter for a general {@link Filter} object value. */
	public static final String PARAM_FILTER = "filter";

	/**
	 * The default query name used for
	 * {@link #getFilteredSources(Long[], String)}.
	 * 
	 * @since 1.1
	 */
	public static final String QUERY_FOR_SOURCES = "find-loc-metadata-distinct-sources";

	/**
	 * Default constructor.
	 */
	public MyBatisGeneralLocationDatumMetadataDao() {
		super(GeneralLocationDatumMetadata.class, LocationSourcePK.class);
	}

	private Long executeCountQuery(final String countQueryName, final Map<String, ?> sqlProps) {
		Number n = getSqlSession().selectOne(countQueryName, sqlProps);
		if ( n != null ) {
			return n.longValue();
		}
		return null;
	}

	private String getQueryForFilter(GeneralLocationDatumMetadataFilter filter) {
		return getQueryForAll() + "-GeneralLocationDatumMetadataMatch";
	}

	@Override
	// Propagation.REQUIRED for server-side cursors
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public FilterResults<GeneralLocationDatumMetadataFilterMatch> findFiltered(
			GeneralLocationDatumMetadataFilter filter, List<SortDescriptor> sortDescriptors,
			Integer offset, Integer max) {
		final String query = getQueryForFilter(filter);
		Map<String, Object> sqlProps = new HashMap<String, Object>(1);
		sqlProps.put(PARAM_FILTER, filter);
		if ( sortDescriptors != null && sortDescriptors.size() > 0 ) {
			sqlProps.put(SORT_DESCRIPTORS_PROPERTY, sortDescriptors);
		}

		addFullTextSearchFilterProperties(filter, sqlProps);

		// attempt count first, if max NOT specified as -1
		Long totalCount = null;
		if ( max != null && max.intValue() != -1 ) {
			totalCount = executeCountQuery(query + "-count", sqlProps);
		}

		List<GeneralLocationDatumMetadataFilterMatch> rows = selectList(query, sqlProps, offset, max);

		BasicFilterResults<GeneralLocationDatumMetadataFilterMatch> results = new BasicFilterResults<GeneralLocationDatumMetadataFilterMatch>(
				rows, (totalCount != null ? totalCount : Long.valueOf(rows.size())), offset,
				rows.size());

		return results;
	}

	protected void addFullTextSearchFilterProperties(GeneralLocationDatumMetadataFilter filter,
			Map<String, Object> sqlProps) {
		if ( filter != null && filter.getLocation() != null ) {
			Location loc = filter.getLocation();
			StringBuilder fts = new StringBuilder();
			spaceAppend(loc.getName(), fts);
			spaceAppend(loc.getCountry(), fts);
			spaceAppend(loc.getRegion(), fts);
			spaceAppend(loc.getStateOrProvince(), fts);
			spaceAppend(loc.getLocality(), fts);
			spaceAppend(loc.getPostalCode(), fts);
			if ( fts.length() > 0 ) {
				sqlProps.put("fts", fts.toString());
			}
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Set<LocationSourcePK> getFilteredSources(Long[] locationIds, String metadataFilter) {
		Map<String, Object> sqlProps = new HashMap<String, Object>(2);
		sqlProps.put("locationIds", locationIds);
		sqlProps.put(PARAM_FILTER, metadataFilter);
		List<LocationSourcePK> rows = selectList(QUERY_FOR_SOURCES, sqlProps, null, null);
		return new LinkedHashSet<LocationSourcePK>(rows);
	}

}
