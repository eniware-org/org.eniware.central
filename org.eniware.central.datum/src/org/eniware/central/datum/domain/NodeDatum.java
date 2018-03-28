/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * Basic node-level NodeDatum API.
 *
 * @version $Revision$ $Date$
 */
public interface NodeDatum extends Datum {

	/**
	 * Get the ID of the node this datam originates from.
	 * 
	 * @return the node ID
	 */
	public Long getNodeId();
	
}
