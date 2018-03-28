/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.cloud.domain;

/**
 * Enum of possible states for a virtual machine to be in.
 * @version 1.0
 */
public enum VirtualMachineState {

	/** The machine is starting. */
	Starting,

	/** The machine is running. */
	Running,

	/** The machine is not running, but could be started again. */
	Stopped,

	/**
	 * The machine is shutting down, either to end in the {@code Stopped} or
	 * {@code Terminated} states.
	 */
	Stopping,

	/** The machine is terminated, and cannot be started again. */
	Terminated,

	/** The machine is in an unknown state. */
	Unknown;

}
