/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.domain;

import org.eniware.central.domain.EntityMatch;
import org.joda.time.DateTime;

/**
 * Instruction for a specific node.
 * 
 * @version $Revision$
 */
public class EdgeInstruction extends Instruction implements EntityMatch {

	private static final long serialVersionUID = -8910808111207075055L;

	private Long nodeId;

	/**
	 * Default constructor.
	 */
	public EdgeInstruction() {
		super();
	}
	
	/**
	 * Construct with values.
	 * 
	 * @param topic the topic
	 * @param instructionDate the instruction date
	 * @param nodeId the node ID
	 */
	public EdgeInstruction(String topic, DateTime instructionDate, Long nodeId) {
		super(topic, instructionDate);
		this.nodeId = nodeId;
	}
	
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	
}
