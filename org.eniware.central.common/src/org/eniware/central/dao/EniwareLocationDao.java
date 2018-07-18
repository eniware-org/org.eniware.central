/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.Location;
import org.eniware.central.domain.LocationMatch;
import org.eniware.central.domain.EniwareLocation;

/**
 * DAO API for Location.
 * @version 1.3
 */
public interface EniwareLocationDao extends GenericDao<EniwareLocation, Long>,
		FilterableDao<LocationMatch, Long, Location> {

	/**
	 * Find a EniwareLocation for just a country and time zone.
	 * 
	 * @param country
	 *        the country
	 * @param timeZoneId
	 *        the time zone ID
	 * @return the EniwareLocation, or <em>null</em> if none found
	 */
	EniwareLocation getEniwareLocationForTimeZone(String country, String timeZoneId);

	/**
	 * Find a EniwareLocation that exactly matches the given criteria. By exactly
	 * matching, even empty fields must match.
	 * 
	 * @param criteria
	 *        the search criteria
	 * @return the matching location, or <em>null</em> if not found
	 */
	EniwareLocation getEniwareLocationForLocation(Location criteria);

}
