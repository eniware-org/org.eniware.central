/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.central.domain.Location;

/**
 * Extension of {@link DatumFilter} for location-based entities.
 * 
 * @author matt
 * @version 1.0
 */
public interface LocationDatumFilter extends DatumFilter {

	/**
	 * Get a specific location ID.
	 * 
	 * @return the location ID
	 */
	Long getLocationId();

	/**
	 * A location filter.
	 * 
	 * @return the location filter
	 */
	Location getLocation();

}
