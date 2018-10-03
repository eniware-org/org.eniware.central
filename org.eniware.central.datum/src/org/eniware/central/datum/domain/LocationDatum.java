/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * Basic location-level Datum API.
 *
 * @version $Revision$ $Date$
 */
public interface LocationDatum extends Datum {

	/**
	 * Get the ID of the location this datam relates to.
	 * 
	 * @return the Edge ID
	 */
	public Long getLocationId();
	
}
