/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * General Edge group information.
 * @version $Revision$
 */
public interface EdgeGroupInformation extends EdgeGroupIdentity {

	/**
	 * Get the name of the group.
	 * 
	 * @return the group name
	 */
	String getName();
	
	/**
	 * Get the location of the Edge.
	 * 
	 * @return location
	 */
	Location getLocation();

	/**
	 * Get a theoretical maximum power generation capacity of all group members combined.
	 * 
	 * @return generation capacity watts
	 */
	Long getGenerationCapacityWatts();
	
	/**
	 * Get a theoretical maximum power storage capacity of all group members combined.
	 * 
	 * @return storage capacity in watt hours
	 */
	Long getStorageCapacityWattHours();
	
}
