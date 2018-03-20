/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * Centralized node control entity.
 * 
 * <p>This entity is designed to hold information about a control so that control
 * could be compared against other controls.</p>
 * 
 * @author matt
 * @version $Revision$
 */
public class HardwareControl extends BaseEntity implements EntityMatch {

	private static final long serialVersionUID = 80531046308809010L;

	private Hardware hardware;
	private String name;
	private String unit;
	
	public Hardware getHardware() {
		return hardware;
	}
	public void setHardware(Hardware hardware) {
		this.hardware = hardware;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
