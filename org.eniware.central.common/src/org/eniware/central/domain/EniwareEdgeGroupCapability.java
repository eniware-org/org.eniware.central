/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

/**
 * A set of capabilities for a Edge.
 * @version $Revision$
 */
public class EniwareEdgeGroupCapability extends EniwareCapability implements Cloneable, Serializable {

	private static final long serialVersionUID = 5120295683193038735L;

	private Long groupId;
	
	/**
	 * Default constructor.
	 */
	public EniwareEdgeGroupCapability() {
		super();
	}
	
	/**
	 * Construct with values.
	 * 
	 * @param groupId the Edge group ID
	 * @param generationCapacityWatts the generation capacity
	 * @param storageCapacityWattHours the energy storage capacity
	 */
	public EniwareEdgeGroupCapability(Long groupId, Long generationCapacityWatts,
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
