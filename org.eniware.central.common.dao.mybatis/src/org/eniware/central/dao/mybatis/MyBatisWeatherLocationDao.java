/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.eniware.central.dao.WeatherLocationDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisFilterableDao;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.SourceLocation;
import org.eniware.central.domain.SourceLocationMatch;
import org.eniware.central.domain.WeatherLocation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link WeatherLocationDao}.
 * 
 * @author matt
 * @version 1.0
 */
public class MyBatisWeatherLocationDao extends
		BaseMyBatisFilterableDao<WeatherLocation, SourceLocationMatch, SourceLocation, Long>
		implements WeatherLocationDao {

	/**
	 * The query name used for
	 * {@link #getWeatherLocationForName(String,Location)}.
	 */
	public static final String QUERY_FOR_NAME = "get-WeatherLocation-for-name";

	/**
	 * Default constructor.
	 */
	public MyBatisWeatherLocationDao() {
		super(WeatherLocation.class, Long.class, SourceLocationMatch.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public WeatherLocation getWeatherLocationForName(String sourceName, Location locationFilter) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("sourceName", sourceName);
		params.put("filter", locationFilter);

		List<WeatherLocation> results = getSqlSession().selectList(QUERY_FOR_NAME, params,
				new RowBounds(0, 1));
		if ( results == null || results.size() == 0 ) {
			return null;
		}
		return results.get(0);
	}

	@Override
	protected void postProcessFilterProperties(SourceLocation filter, Map<String, Object> sqlProps) {
		if ( filter.getLocation() != null ) {
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

}
