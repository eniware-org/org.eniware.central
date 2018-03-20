/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.eniware.central.dao.PriceLocationDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisFilterableDao;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.PriceLocation;
import org.eniware.central.domain.SourceLocation;
import org.eniware.central.domain.SourceLocationMatch;
import org.eniware.central.support.PriceLocationFilter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link PriceLocationDao}.
 * 
 * @author matt
 * @version 1.0
 */
public class MyBatisPriceLocationDao extends
		BaseMyBatisFilterableDao<PriceLocation, SourceLocationMatch, SourceLocation, Long>
		implements PriceLocationDao {

	/** The query name used for {@link #getPriceLocationForName(String,String)}. */
	public static final String QUERY_FOR_NAME = "get-PriceLocation-for-name";

	/**
	 * Default constructor.
	 */
	public MyBatisPriceLocationDao() {
		super(PriceLocation.class, Long.class, SourceLocationMatch.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PriceLocation getPriceLocationForName(String sourceName, String locationName) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("locationName", locationName);
		params.put("sourceName", sourceName);
		return selectFirst(QUERY_FOR_NAME, params);
	}

	@Override
	protected void postProcessFilterProperties(SourceLocation filter, Map<String, Object> sqlProps) {
		PriceLocationFilter pFilter;
		if ( filter instanceof PriceLocationFilter ) {
			pFilter = (PriceLocationFilter) filter;
		} else {
			// mapping expects a PriceLocationFilter, so replace the input with one of those
			pFilter = new PriceLocationFilter(filter);
			sqlProps.put(FILTER_PROPERTY, pFilter);
		}
		StringBuilder fts = new StringBuilder();
		spaceAppend(pFilter.getCurrency(), fts);
		if ( filter.getLocation() != null ) {
			Location loc = filter.getLocation();
			spaceAppend(loc.getName(), fts);
			spaceAppend(loc.getCountry(), fts);
			spaceAppend(loc.getRegion(), fts);
			spaceAppend(loc.getStateOrProvince(), fts);
			spaceAppend(loc.getLocality(), fts);
			spaceAppend(loc.getPostalCode(), fts);
		}
		if ( fts.length() > 0 ) {
			sqlProps.put("fts", fts.toString());
		}
	}

}
