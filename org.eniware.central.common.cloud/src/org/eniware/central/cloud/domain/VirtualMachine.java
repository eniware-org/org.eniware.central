 /* ==================================================================
 * 
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.cloud.domain;

import org.eniware.central.domain.Entity;

/**
 * A virtual machine.
 * @version 1.0
 */
public interface VirtualMachine extends Entity<String> {

	/**
	 * Get a name for this machine.
	 * 
	 * @return a name
	 */
	String getDisplayName();

	/**
	 * Get the state of this machine.
	 * 
	 * <p>
	 * This value might be a cached, last known value. Use
	 * {@code VirtualMachineBiz} to read the current state.
	 * </p>
	 * 
	 * @return the state
	 */
	VirtualMachineState getState();

}
