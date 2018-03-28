/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.domain;

import java.util.List;

import org.eniware.central.domain.Filter;

/**
 * Filter for Instruction entities.
 *
 * @version 1.2
 */
public interface InstructionFilter extends Filter {

	/**
	 * Filter based on a node ID.
	 * 
	 * @return the node ID
	 */
	Long getNodeId();

	/**
	 * Get an array of node IDs.
	 * 
	 * @return array of node IDs (may be {@literal null})
	 * @since 1.2
	 */
	public Long[] getNodeIds();

	/**
	 * Get an array of instruction IDs.
	 * 
	 * @return array of instruction IDs (may be {@literal null})
	 * @since 1.2
	 */
	public Long[] getInstructionIds();

	/**
	 * Filter based on state.
	 * 
	 * @return the state
	 */
	InstructionState getState();

	/**
	 * Filter based on a set of states.
	 * 
	 * @return the states, treated as a logical <em>or</em> so an instruction
	 *         matches if its state is contained in this set
	 */
	List<InstructionState> getStates();
}
