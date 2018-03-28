/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import org.eniware.central.domain.BaseIdentity;
import org.eniware.central.domain.Location;
import org.eniware.central.domain.NodeGroupInformation;
import org.eniware.central.domain.SolarCapability;
import org.eniware.central.domain.SolarNodeGroupCapability;

/**
 * Simple implementation of {@link NodeGroupInformation}.
 * @version $Revision$
 */
public class SimpleNodeGroupInformation extends BaseIdentity implements NodeGroupInformation {

	private static final long serialVersionUID = -1983417976743765775L;

	private String name;
	private Location location;
	private SolarCapability capability;

	/**
	 * Default constructor.
	 */
	public SimpleNodeGroupInformation() {
		super();
	}
	
	/**
	 * Construct with values.
	 * 
	 * @param group the group to copy values from.
	 * @param location the location
	 */
	public SimpleNodeGroupInformation(String name, SolarNodeGroupCapability capability, 
			Location location) {
		setId(capability.getGroupId());
		this.name = name;
		this.capability = capability;
		this.location = location;
	}
	
	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Increment the generation capacity.
	 * @param amount the amount to add
	 */
	public void addGenerationCapacityWatts(Long amount) {
		capability.setGenerationCapacityWatts(
				capability.getGenerationCapacityWatts() + amount);
	}
	
	/**
	 * Increment the storage capacity.
	 * @param amount the amount to add
	 */
	public void addStorageCapacityWattHours(Long amount) {
		capability.setStorageCapacityWattHours(
				capability.getStorageCapacityWattHours() + amount);
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
