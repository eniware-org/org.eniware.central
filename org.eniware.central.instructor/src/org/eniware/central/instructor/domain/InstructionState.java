/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.domain;

/**
 * An instruction state.
 * 
 * @version $Revision$
 */
public enum InstructionState {
	
	/**
	 * The instruction state is not known.
	 */
	Unknown,
	
	/**
	 * The instruction has been queued, but not acknowledged yet.
	 */
	Queued,

	/**
	 * The instruction has been acknowledged, but has not been looked at yet. 
	 */
	Received,
	
	/** 
	 * The instruction has been acknowledged and is being executed currently.
	 */
	Executing,
	
	/**
	 * The instruction was acknowledged but has been declined and will not be executed.
	 */
	Declined,
	
	/**
	 * The instruction was acknowledged and has been executed.
	 */
	Completed;

}
