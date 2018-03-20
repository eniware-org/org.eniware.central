/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.scheduler;

import java.util.Collection;

/**
 * API for management of the SolarNet scheduler.
 * 
 * @author matt
 * @version 1.0
 * @since 1.37
 */
public interface SchedulerManager {

	/**
	 * Get the current status of the scheduler.
	 * 
	 * @return the current status
	 */
	SchedulerStatus currentStatus();

	/**
	 * Change the status of the scheduler.
	 * 
	 * @param desiredStatus
	 *        the desired status to set
	 */
	void updateStatus(SchedulerStatus desiredStatus);

	/**
	 * Get a collection of all available scheduled jobs.
	 * 
	 * @return the collection of jobs; never {@literal null}
	 */
	Collection<JobInfo> allJobInfos();

	/**
	 * Pause a specific job.
	 * 
	 * @param groupId
	 *        the job group ID
	 * @param id
	 *        the job ID
	 */
	void pauseJob(String groupId, String id);

	/**
	 * Resume a paused job.
	 * 
	 * @param groupId
	 *        the job group ID
	 * @param id
	 *        the job ID
	 */
	void resumeJob(String groupId, String id);

}
