/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * API for capability information for some identifiable object,
 * such as a Edge, Edge group, etc.
 * 
 * <p>This API is meant to be used for both atomic measurements
 * and aggregated measurements, depending on the context it is
 * used in.</p>
 * 
 * @param <PK> the identity type
 * @version $Revision$
 */
public interface EniwareCapabilityInformation<PK> extends Identity<PK> {

	/**
	 * Get the name of the object.
	 * 
	 * @return the name
	 */
	String getName();

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
