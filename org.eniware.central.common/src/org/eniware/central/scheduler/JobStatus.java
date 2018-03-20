/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.scheduler;

/**
 * Status for a job.
 * 
 * @author matt
 * @version 1.0
 * @since 1.37
 */
public enum JobStatus {

	/** The job is scheduled to run normally. */
	Scheduled,

	/**
	 * The job will not be executed in the future, but can be resumed.
	 */
	Paused,

	/**
	 * The job has finished executing and is not scheduled to run again.
	 */
	Complete,

	/**
	 * The job encountered an error.
	 */
	Error,

	/**
	 * The job is not in any known state.
	 */
	Unknown;

}
