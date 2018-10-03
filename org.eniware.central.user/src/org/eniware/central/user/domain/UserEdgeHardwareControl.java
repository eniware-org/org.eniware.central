/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.central.domain.BaseEntity;
import org.eniware.central.domain.HardwareControl;

/**
 * A user Edge hardware control configuration element.
 * 
 * <p>This defines a relationship between a Edge control and a HardwareControl entity.</p>
 * 
 * @version $Revision$
 */
public class UserEdgeHardwareControl extends BaseEntity {

	private static final long serialVersionUID = 9220118628409582403L;

	private Long EdgeId;
	private String sourceId;
	private String name;
	private HardwareControl control;
	
	public Long getEdgeId() {
		return EdgeId;
	}
	public void setEdgeId(Long EdgeId) {
		this.EdgeId = EdgeId;
	}
	public HardwareControl getControl() {
		return control;
	}
	public void setControl(HardwareControl control) {
		this.control = control;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	
}
