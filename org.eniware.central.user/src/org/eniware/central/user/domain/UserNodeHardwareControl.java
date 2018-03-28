/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.central.domain.BaseEntity;
import org.eniware.central.domain.HardwareControl;

/**
 * A user node hardware control configuration element.
 * 
 * <p>This defines a relationship between a node control and a HardwareControl entity.</p>
 * 
 * @version $Revision$
 */
public class UserNodeHardwareControl extends BaseEntity {

	private static final long serialVersionUID = 9220118628409582403L;

	private Long nodeId;
	private String sourceId;
	private String name;
	private HardwareControl control;
	
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
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
