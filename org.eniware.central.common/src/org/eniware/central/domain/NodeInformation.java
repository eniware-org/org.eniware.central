/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * General node information.
 * @version $Revision$
 */
public interface NodeInformation extends NodeIdentity {

	/**
	 * Get a theoretical maximum power generation capacity.
	 * 
	 * @return generation capacity watts
	 */
	Long getGenerationCapacityWatts();
	
	/**
	 * Get a theoretical maximum power storage capacity.
	 * 
	 * @return storage capacity in watt hours
	 */
	Long getStorageCapacityWattHours();
	
	/**
	 * Get the location of the node.
	 * 
	 * @return location
	 */
	Location getLocation();

}
