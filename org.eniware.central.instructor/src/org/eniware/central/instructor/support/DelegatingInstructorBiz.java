/* ==================================================================
 * DelegatingInstructorBiz.java - Nov 27, 2012 7:37:48 AM
 * 
 * Copyright 2007-2012 EniwareNetwork.net Dev Team
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA
 * ==================================================================
 */

package org.eniware.central.instructor.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eniware.central.instructor.biz.InstructorBiz;
import org.eniware.central.instructor.domain.Instruction;
import org.eniware.central.instructor.domain.InstructionState;
import org.eniware.central.instructor.domain.EdgeInstruction;

/**
 * Delegates to another InstructorBiz, designed for AOP use.
 * 
 * @version 1.3
 */
public class DelegatingInstructorBiz implements InstructorBiz {

	private final InstructorBiz delegate;

	/**
	 * Constructor.
	 * 
	 * @param delegate
	 *        the delegate
	 */
	public DelegatingInstructorBiz(InstructorBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public List<Instruction> getActiveInstructionsForNode(Long nodeId) {
		return delegate.getActiveInstructionsForNode(nodeId);
	}

	@Override
	public List<EdgeInstruction> getActiveInstructionsForNodes(Set<Long> nodeIds) {
		return delegate.getActiveInstructionsForNodes(nodeIds);
	}

	@Override
	public List<Instruction> getPendingInstructionsForNode(Long nodeId) {
		return delegate.getPendingInstructionsForNode(nodeId);
	}

	@Override
	public List<EdgeInstruction> getPendingInstructionsForNodes(Set<Long> nodeIds) {
		return delegate.getPendingInstructionsForNodes(nodeIds);
	}

	@Override
	public EdgeInstruction queueInstruction(Long nodeId, Instruction instruction) {
		return delegate.queueInstruction(nodeId, instruction);
	}

	@Override
	public List<EdgeInstruction> queueInstructions(Set<Long> nodeIds, Instruction instruction) {
		return delegate.queueInstructions(nodeIds, instruction);
	}

	@Override
	public EdgeInstruction getInstruction(Long instructionId) {
		return delegate.getInstruction(instructionId);
	}

	@Override
	public List<EdgeInstruction> getInstructions(Set<Long> instructionIds) {
		return delegate.getInstructions(instructionIds);
	}

	@Override
	public void updateInstructionState(Long instructionId, InstructionState state) {
		delegate.updateInstructionState(instructionId, state);
	}

	@Override
	public void updateInstructionsState(Set<Long> instructionIds, InstructionState state) {
		delegate.updateInstructionsState(instructionIds, state);
	}

	@Override
	public void updateInstructionState(Long instructionId, InstructionState state,
			Map<String, ?> resultParameters) {
		delegate.updateInstructionState(instructionId, state, resultParameters);
	}

	@Override
	public void updateInstructionsState(Set<Long> instructionIds, InstructionState state,
			Map<Long, Map<String, ?>> resultParameters) {
		delegate.updateInstructionsState(instructionIds, state, resultParameters);
	}

}
