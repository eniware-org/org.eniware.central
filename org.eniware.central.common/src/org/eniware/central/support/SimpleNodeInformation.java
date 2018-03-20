/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import org.eniware.central.domain.BaseIdentity;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.NodeInformation;
import org.eniware.central.domain.SolarNodeCapability;

/**
 * Simple implementation of {@link NodeInformation}.
 * 
 * @author matt
 * @version $Revision$
 */
public class SimpleNodeInformation extends BaseIdentity implements NodeInformation {

	private static final long serialVersionUID = -7130984585644772072L;

	private Location location;
	private SolarNodeCapability capability;
	
	/**
	 * Default constructor.
	 */
	public SimpleNodeInformation() {
		super();
	}
	
	/**
	 * Construct with values.
	 * 
	 * @param group the group to copy values from.
	 * @param location the location
	 */
	public SimpleNodeInformation(SolarNodeCapability capability, Location location) {
		setId(capability.getNodeId());
		this.capability = capability;
		this.location = location;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the generationCapacityWatts
	 */
	public Long getGenerationCapacityWatts() {
		return capability.getGenerationCapacityWatts();
	}

	/**
	 * @return the storageCapacityWattHours
	 */
	public Long getStorageCapacityWattHours() {
		return capability.getStorageCapacityWattHours();
	}

}
