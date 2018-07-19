/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.domain;

import org.eniware.central.domain.EntityMatch;
import org.joda.time.DateTime;

/**
 * Instruction for a specific Edge.
 * 
 * @version $Revision$
 */
public class EdgeInstruction extends Instruction implements EntityMatch {

	private static final long serialVersionUID = -8910808111207075055L;

	private Long EdgeId;

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
	 * @param EdgeId the Edge ID
	 */
	public EdgeInstruction(String topic, DateTime instructionDate, Long EdgeId) {
		super(topic, instructionDate);
		this.EdgeId = EdgeId;
	}
	
	public Long getEdgeId() {
		return EdgeId;
	}
	public void setEdgeId(Long EdgeId) {
		this.EdgeId = EdgeId;
	}
	
}
