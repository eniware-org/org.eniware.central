/* ===================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ===================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.SolarNode;

/**
 * DAO API for SolarNode data.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
public interface SolarNodeDao extends GenericDao<SolarNode, Long> {

	/**
	 * Get an unused node ID value.
	 * 
	 * <p>Once an ID has been returned by this method, that ID will
	 * never be returned again.</p>
	 * 
	 * @return an unused node ID
	 */
	Long getUnusedNodeId();
	
}
