/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.biz;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eniware.central.instructor.domain.Instruction;
import org.eniware.central.instructor.domain.InstructionState;
import org.eniware.central.instructor.domain.EdgeInstruction;

/**
 * API for central instruction service.
 *
 * @version 1.3
 */
public interface InstructorBiz {

	/**
	 * Return any active instructions for a specific Edge.
	 * 
	 * <p>
	 * An instruction is considered <em>active</em> if it is in the
	 * {@link InstructionState#Queued} state.
	 * </p>
	 * 
	 * @param EdgeId
	 *        the ID of the Edge to get active instructions for
	 * @return the instructions
	 */
	List<Instruction> getActiveInstructionsForEdge(Long EdgeId);

	/**
	 * Return any active instructions for a set of Edges.
	 * 
	 * <p>
	 * An instruction is considered <em>active</em> if it is in the
	 * {@link InstructionState#Queued} state.
	 * </p>
	 * 
	 * @param EdgeIds
	 *        the IDs of the Edges to get active instructions for
	 * @return the instructions
	 * @since 1.3
	 */
	List<EdgeInstruction> getActiveInstructionsForEdges(Set<Long> EdgeIds);

	/**
	 * Return any pending instructions for a specific Edge.
	 * 
	 * <p>
	 * An instruction is considered <em>pending</em> if it is in
	 * {@link InstructionState#Queued}, {@link InstructionState#Received}, or
	 * {@link InstructionState#Executing} states.
	 * </p>
	 * 
	 * @param EdgeId
	 *        the ID of the Edge to get pending instructions for
	 * @return the instructions
	 * @since 1.1
	 */
	List<Instruction> getPendingInstructionsForEdge(Long EdgeId);

	/**
	 * Return any pending instructions for a set of Edges.
	 * 
	 * <p>
	 * An instruction is considered <em>pending</em> if it is in
	 * {@link InstructionState#Queued}, {@link InstructionState#Received}, or
	 * {@link InstructionState#Executing} states.
	 * </p>
	 * 
	 * @param EdgeIds
	 *        the IDs of the Edges to get pending instructions for
	 * @return the instructions
	 * @since 1.3
	 */
	List<EdgeInstruction> getPendingInstructionsForEdges(Set<Long> EdgeIds);

	/**
	 * Queue an instruction for a specific Edge. The instruction will be put
	 * into the {@link InstructionState#Queued} state.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param instruction
	 *        the instruction
	 * @return the persisted instruction
	 */
	EdgeInstruction queueInstruction(Long EdgeId, Instruction instruction);

	/**
	 * Queue an instruction for multiple Edges. The instruction will be put into
	 * the {@link InstructionState#Queued} state.
	 * 
	 * @param EdgeIds
	 *        a set of Edge IDs to enqueue the instruction on
	 * @param instruction
	 *        the instruction
	 * @return the persisted instructions, in iteration order of {@code EdgeIds}
	 * @since 1.3
	 */
	List<EdgeInstruction> queueInstructions(Set<Long> EdgeIds, Instruction instruction);

	/**
	 * Get a specific instruction.
	 * 
	 * @param instructionId
	 *        the instruction ID
	 * @return the found instruction, or {@literal null} if not found
	 */
	EdgeInstruction getInstruction(Long instructionId);

	/**
	 * Get a set of instructions.
	 * 
	 * @param instructionIds
	 *        the instruction IDs to fetch
	 * @return the found instructions, or {@literal null} if not found
	 * @since 1.3
	 */
	List<EdgeInstruction> getInstructions(Set<Long> instructionIds);

	/**
	 * Update the state of a specific instruction.
	 * 
	 * <p>
	 * As an instruction is processed, for example by a Edge, the state should
	 * be updated by that processor.
	 * </p>
	 * 
	 * @param instructionId
	 *        the instruction ID
	 * @param state
	 *        the new state
	 */
	void updateInstructionState(Long instructionId, InstructionState state);

	/**
	 * Update the state of a set of instructions.
	 * 
	 * <p>
	 * As an instruction is processed, for example by a Edge, the state should
	 * be updated by that processor.
	 * </p>
	 * 
	 * @param instructionIds
	 *        the instruction IDs to update
	 * @param state
	 *        the new state
	 * @since 1.3
	 */
	void updateInstructionsState(Set<Long> instructionIds, InstructionState state);

	/**
	 * Update the state of a specific instruction.
	 * 
	 * <p>
	 * As an instruction is processed, for example by a Edge, the state should
	 * be updated by that processor.
	 * </p>
	 * 
	 * @param instructionId
	 *        the instruction ID
	 * @param state
	 *        the new state
	 * @param resultParameters
	 *        optional result parameters to include
	 * @since 1.2
	 */
	void updateInstructionState(Long instructionId, InstructionState state,
			Map<String, ?> resultParameters);

	/**
	 * Update the state of a specific instruction.
	 * 
	 * <p>
	 * As an instruction is processed, for example by a Edge, the state should
	 * be updated by that processor.
	 * </p>
	 * 
	 * @param instructionIds
	 *        the instruction IDs
	 * @param state
	 *        the new state
	 * @param resultParameters
	 *        optional result parameters to include, with top level instruction
	 *        ID keys and associated result parameter values
	 * @since 1.3
	 */
	void updateInstructionsState(Set<Long> instructionIds, InstructionState state,
			Map<Long, Map<String, ?>> resultParameters);

}
