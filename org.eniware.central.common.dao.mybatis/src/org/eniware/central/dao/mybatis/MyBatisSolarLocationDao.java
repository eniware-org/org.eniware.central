/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.eniware.central.dao.SolarLocationDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisFilterableDao;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.LocationMatch;
import org.eniware.central.domain.SolarLocation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link SolarLocationDao}.
 * 
 * @author matt
 * @version 1.0
 */
public class MyBatisSolarLocationDao extends
		BaseMyBatisFilterableDao<SolarLocation, LocationMatch, Location, Long> implements
		SolarLocationDao {

	/** The query name used for {@link #getSolarLocationForName(String)}. */
	public static final String QUERY_FOR_NAME = "find-SolarLocation-for-name";

	/**
	 * The query name used for
	 * {@link #getSolarLocationForTimeZone(String, String)}.
	 */
	public static final String QUERY_FOR_COUNTRY_TIME_ZONE = "find-SolarLocation-for-country-timezone";

	/**
	 * The query name used for {@link #getSolarLocationForLocation(Location)}.
	 */
	public static final String QUERY_FOR_EXACT_LOCATION = "find-SolarLocation-for-location";

	/**
	 * Default constructor.
	 */
	public MyBatisSolarLocationDao() {
		super(SolarLocation.class, Long.class, LocationMatch.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public SolarLocation getSolarLocationForTimeZone(String country, String timeZoneId) {
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("country", country);
		params.put("timeZoneId", timeZoneId);
		return selectFirst(QUERY_FOR_COUNTRY_TIME_ZONE, params);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public SolarLocation getSolarLocationForLocation(Location criteria) {
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
