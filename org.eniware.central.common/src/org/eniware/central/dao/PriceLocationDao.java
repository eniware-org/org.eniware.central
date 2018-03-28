/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.PriceLocation;
import org.eniware.central.domain.SourceLocation;
import org.eniware.central.domain.SourceLocationMatch;

/**
 * DAO API for PriceLocation.
 * @version $Revision$
 */
public interface PriceLocationDao extends GenericDao<PriceLocation, Long>,
FilterableDao<SourceLocationMatch, Long, SourceLocation> {

	/**
	 * Find a unique PriceLocation for a given PriceSource name and location name.
	 * 
	 * @param sourceName the PriceSource name
	 * @param locationName the location name
	 * @return the PriceLocation, or <em>null</em> if not found
	 */
	PriceLocation getPriceLocationForName(String sourceName, String locationName);
}
