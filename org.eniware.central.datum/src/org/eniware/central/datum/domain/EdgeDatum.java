/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * Basic Edge-level EdgeDatum API.
 *
 * @version $Revision$ $Date$
 */
public interface EdgeDatum extends Datum {

	/**
	 * Get the ID of the Edge this datam originates from.
	 * 
	 * @return the Edge ID
	 */
	public Long getEdgeId();
	
}
