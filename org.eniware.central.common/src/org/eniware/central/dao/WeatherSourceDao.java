/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.WeatherSource;

/**
 * DAO API for WeatherSource.
 * 
 * @author matt
 * @version $Revision$
 */
public interface WeatherSourceDao extends GenericDao<WeatherSource, Long> {

	/**
	 * Find a unique WeatherSource for a given name.
	 * 
	 * @param name the WeatherSource name
	 * @return the WeatherSource, or <em>null</em> if not found
	 */
	WeatherSource getWeatherSourceForName(String sourceName);

}
