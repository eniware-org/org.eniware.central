/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 * $Id$
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

/**
 * A set of capabilities for a Edge.
 * @version $Revision$
 */
public class EniwareEdgeCapability extends EniwareCapability implements Cloneable, Serializable {

	private static final long serialVersionUID = -1896754053131443476L;

	private Long EdgeId;
	
	/**
	 * Default constructor.
	 */
	public EniwareEdgeCapability() {
		super();
	}
	
	/**
	 * Construct with values.
	 * 
	 * @param EdgeId the Edge ID
	 * @param generationCapacityWatts the generation capacity
	 * @param storageCapacityWattHours the energy storage capacity
	 */
	public EniwareEdgeCapability(Long EdgeId, Long generationCapacityWatts,
			Long storageCapacityWattHours) {
		setEdgeId(EdgeId);
		setGenerationCapacityWatts(generationCapacityWatts);
		setStorageCapacityWattHours(storageCapacityWattHours);
	}
	
	public Long getEdgeId() {
		return EdgeId;
	}
	public void setEdgeId(Long EdgeId) {
		this.EdgeId = EdgeId;
	}

}
