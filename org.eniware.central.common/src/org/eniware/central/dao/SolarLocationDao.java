/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.Location;
import org.eniware.central.domain.LocationMatch;
import org.eniware.central.domain.SolarLocation;

/**
 * DAO API for Location.
 * 
 * @author matt
 * @version 1.3
 */
public interface SolarLocationDao extends GenericDao<SolarLocation, Long>,
		FilterableDao<LocationMatch, Long, Location> {

	/**
	 * Find a SolarLocation for just a country and time zone.
	 * 
	 * @param country
	 *        the country
	 * @param timeZoneId
	 *        the time zone ID
	 * @return the SolarLocation, or <em>null</em> if none found
	 */
	SolarLocation getSolarLocationForTimeZone(String country, String timeZoneId);

	/**
	 * Find a SolarLocation that exactly matches the given criteria. By exactly
	 * matching, even empty fields must match.
	 * 
	 * @param criteria
	 *        the search criteria
	 * @return the matching location, or <em>null</em> if not found
	 */
	SolarLocation getSolarLocationForLocation(Location criteria);

}
