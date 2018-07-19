/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import org.eniware.central.domain.BaseIdentity;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.EdgeInformation;
import org.eniware.central.domain.EniwareEdgeCapability;

/**
 * Simple implementation of {@link EdgeInformation}.
 * @version $Revision$
 */
public class SimpleEdgeInformation extends BaseIdentity implements EdgeInformation {

	private static final long serialVersionUID = -7130984585644772072L;

	private Location location;
	private EniwareEdgeCapability capability;
	
	/**
	 * Default constructor.
	 */
	public SimpleEdgeInformation() {
		super();
	}
	
	/**
	 * Construct with values.
	 * 
	 * @param group the group to copy values from.
	 * @param location the location
	 */
	public SimpleEdgeInformation(EniwareEdgeCapability capability, Location location) {
		setId(capability.getEdgeId());
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
