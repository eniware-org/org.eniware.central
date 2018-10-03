/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.scheduler;

/**
 * Status for the scheduler.
 * @version 1.0
 * @since 1.37
 */
public enum SchedulerStatus {

	/** The scheduler is starting up, but not yet executing any jobs. */
	Starting,

	/** The scheduler has started and is scheduling jobs normally. */
	Running,

	/**
	 * The scheduler has been paused, is not executing any more jobs, and can be
	 * re-started.
	 */
	Paused,

	/**
	 * The scheduler has been stopped and will not execute any more jobs without
	 * manual intervention.
	 */
	Destroyed,

	/**
	 * The scheduler is not in any known state.
	 */
	Unknown;

}
