/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 * $Id$
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
public class SolarNodeCapability extends SolarCapability implements Cloneable, Serializable {

	private static final long serialVersionUID = -1896754053131443476L;

	private Long nodeId;
	
	/**
	 * Default constructor.
	 */
	public SolarNodeCapability() {
		super();
	}
	
	/**
	 * Construct with values.
	 * 
	 * @param nodeId the node ID
	 * @param generationCapacityWatts the generation capacity
	 * @param storageCapacityWattHours the energy storage capacity
	 */
	public SolarNodeCapability(Long nodeId, Long generationCapacityWatts,
			Long storageCapacityWattHours) {
		setNodeId(nodeId);
		setGenerationCapacityWatts(generationCapacityWatts);
		setStorageCapacityWattHours(storageCapacityWattHours);
	}
	
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

}
