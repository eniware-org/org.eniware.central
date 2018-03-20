/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web;

import java.util.List;

import org.eniware.central.instructor.domain.Instruction;

/**
 * Result object for bulk upload operations.
 * 
 * <p>
 * The configurable properties of this class are:
 * </p>
 * 
 * <dl class="class-properties">
 * <dt></dt>
 * <dd></dd>
 * </dl>
 * 
 * @author matt
 * @version 1.0
 */
public class BulkUploadResult {

	private List<Object> datum;
	private List<Instruction> instructions;

	public List<Object> getDatum() {
		return datum;
	}

	public void setDatum(List<Object> datum) {
		this.datum = datum;
	}

	public List<Instruction> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<Instruction> instructions) {
		this.instructions = instructions;
	}

}
