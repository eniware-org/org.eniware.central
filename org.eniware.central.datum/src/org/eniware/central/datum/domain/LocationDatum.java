/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * Basic location-level Datum API.
 *
 * @author matt
 * @version $Revision$ $Date$
 */
public interface LocationDatum extends Datum {

	/**
	 * Get the ID of the location this datam relates to.
	 * 
	 * @return the node ID
	 */
	public Long getLocationId();
	
}
