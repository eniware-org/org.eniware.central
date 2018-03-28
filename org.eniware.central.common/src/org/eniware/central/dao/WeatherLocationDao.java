/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.Location;
import org.eniware.central.domain.SourceLocation;
import org.eniware.central.domain.SourceLocationMatch;
import org.eniware.central.domain.WeatherLocation;

/**
 * DAO API for WeatherLocation.
 * @version $Revision$
 */
public interface WeatherLocationDao extends GenericDao<WeatherLocation, Long>,
FilterableDao<SourceLocationMatch, Long, SourceLocation> {

	/**
	 * Find a unique WeatherLocation for a given WeatherSource name and location name.
	 * 
	 * @param sourceName the WeatherSource name
	 * @param location the location filter
	 * @return the WeatherLocation, or <em>null</em> if not found
	 */
	WeatherLocation getWeatherLocationForName(String sourceName, Location locationFilter);

}
