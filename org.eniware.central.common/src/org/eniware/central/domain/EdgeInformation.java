/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * General Edge information.
 * @version $Revision$
 */
public interface EdgeInformation extends EdgeIdentity {

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
	 * Get the location of the Edge.
	 * 
	 * @return location
	 */
	Location getLocation();

}
