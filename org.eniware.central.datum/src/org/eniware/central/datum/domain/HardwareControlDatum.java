/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * A node control, i.e. knob or switch that can be controlled by the node.
 * 
 * @author matt
 * @version $Revision$
 */
public class HardwareControlDatum extends BaseNodeDatum {

	private static final long serialVersionUID = -1715998499484315641L;

	private Integer integerValue;
	private Float floatValue;

	public Integer getIntegerValue() {
		return integerValue;
	}
	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}
	public Float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}
	
}
