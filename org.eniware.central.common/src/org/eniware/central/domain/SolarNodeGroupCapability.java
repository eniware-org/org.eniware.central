/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

/**
 * A set of capabilities for a node.
 * 
 * @author matt
 * @version $Revision$
 */
public class SolarNodeGroupCapability extends SolarCapability implements Cloneable, Serializable {

	private static final long serialVersionUID = 5120295683193038735L;

	private Long groupId;
	
	/**
	 * Default constructor.
	 */
	public SolarNodeGroupCapability() {
		super();
	}
	
	/**
	 * Construct with values.
	 * 
	 * @param groupId the node group ID
	 * @param generationCapacityWatts the generation capacity
	 * @param storageCapacityWattHours the energy storage capacity
	 */
	public SolarNodeGroupCapability(Long groupId, Long generationCapacityWatts,
			Long storageCapacityWattHours) {
		setGroupId(groupId);
		setGenerationCapacityWatts(generationCapacityWatts);
		setStorageCapacityWattHours(storageCapacityWattHours);
	}
	
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

}