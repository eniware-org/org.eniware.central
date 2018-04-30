/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

/**
 * A set of capabilities.
 * @version $Revision$
 */
public class EniwareCapability extends BaseEntity implements Cloneable, Serializable {

	private static final long serialVersionUID = 1449569875028595935L;

	private Long generationCapacityWatts;
	private Long storageCapacityWattHours;

	public Long getGenerationCapacityWatts() {
		return generationCapacityWatts;
	}
	public void setGenerationCapacityWatts(Long generationCapacityWatts) {
		this.generationCapacityWatts = generationCapacityWatts;
	}
	public Long getStorageCapacityWattHours() {
		return storageCapacityWattHours;
	}
	public void setStorageCapacityWattHours(Long storageCapacityWattHours) {
		this.storageCapacityWattHours = storageCapacityWattHours;
	}

}
