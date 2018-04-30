/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.eniware.central.dao.EniwareLocationDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisFilterableDao;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.LocationMatch;
import org.eniware.central.domain.EniwareLocation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link EniwareLocationDao}.
 * @version 1.0
 */
public class MyBatisEniwareLocationDao extends
		BaseMyBatisFilterableDao<EniwareLocation, LocationMatch, Location, Long> implements
		EniwareLocationDao {

	/** The query name used for {@link #getEniwareLocationForName(String)}. */
	public static final String QUERY_FOR_NAME = "find-EniwareLocation-for-name";

	/**
	 * The query name used for
	 * {@link #getEniwareLocationForTimeZone(String, String)}.
	 */
	public static final String QUERY_FOR_COUNTRY_TIME_ZONE = "find-EniwareLocation-for-country-timezone";

	/**
	 * The query name used for {@link #getEniwareLocationForLocation(Location)}.
	 */
	public static final String QUERY_FOR_EXACT_LOCATION = "find-EniwareLocation-for-location";

	/**
	 * Default constructor.
	 */
	public MyBatisEniwareLocationDao() {
		super(EniwareLocation.class, Long.class, LocationMatch.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public EniwareLocation getEniwareLocationForTimeZone(String country, String timeZoneId) {
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("country", country);
		params.put("timeZoneId", timeZoneId);
		return selectFirst(QUERY_FOR_COUNTRY_TIME_ZONE, params);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public EniwareLocation getEniwareLocationForLocation(Location criteria) {
		return selectFirst(QUERY_FOR_EXACT_LOCATION, criteria);
	}

	@Override
	protected void postProcessFilterProperties(Location filter, Map<String, Object> sqlProps) {
		StringBuilder fts = new StringBuilder();
		spaceAppend(filter.getName(), fts);
		spaceAppend(filter.getCountry(), fts);
		spaceAppend(filter.getRegion(), fts);
		spaceAppend(filter.getStateOrProvince(), fts);
		spaceAppend(filter.getLocality(), fts);
		spaceAppend(filter.getPostalCode(), fts);
		if ( fts.length() > 0 ) {
			sqlProps.put("fts", fts.toString());
		}
	}

}
