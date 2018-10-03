/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.cloud.biz;

import java.util.Map;
import java.util.Set;

import org.eniware.central.cloud.domain.VirtualMachine;
import org.eniware.central.cloud.domain.VirtualMachineState;
import org.eniware.domain.Identifiable;

/**
 * API for controlling cloud virtual machine instances.
 * @version 1.0
 */
public interface VirtualMachineBiz extends Identifiable {

	/**
	 * Get a virtual machine by its display name.
	 * 
	 * @param name
	 *        the name of the machine to get
	 * @return the machine, or {@literal null} if not available
	 */
	VirtualMachine virtualMachineForName(String name);

	/**
	 * Get a set of virtual machines by their IDs.
	 * 
	 * @param ids
	 *        the IDs of the machines to get
	 * @return the found machines
	 */
	Iterable<VirtualMachine> virtualMachinesForIds(Set<String> ids);

	/**
	 * Get the state for a set of virtual machines.
	 * 
	 * @param ids
	 *        the IDs of the machines to get the state for
	 * @return a mapping of machine IDs to associated states; never
	 *         {@literal null}
	 */
	Map<String, VirtualMachineState> stateForVirtualMachines(Set<String> ids);

	/**
	 * Change the state of a set of virtual machines.
	 * 
	 * @param ids
	 *        the IDs of the machines to change the state of
	 * @param state
	 *        the desired state
	 */
	void changeVirtualMachinesState(Set<String> ids, VirtualMachineState state);

}
