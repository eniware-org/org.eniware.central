/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.scheduler;

import org.joda.time.DateTime;

/**
 * Information about a scheduled job.
 * 
 * <p>
 * Jobs are uniquely defined by a group name plus a job name.
 * </p>
 * 
 * @author matt
 * @version 1.0
 * @since 1.37
 */
public interface JobInfo {

	/**
	 * Get the group this job belongs to.
	 * 
	 * @return the group ID
	 */
	String getGroupId();

	/**
	 * Get the ID of this job, unique to the job's group.
	 * 
	 * @return the job ID
	 */
	String getId();

	/**
	 * Get the status of this job.
	 * 
	 * @return the job status
	 */
	JobStatus getJobStatus();

	/**
	 * Flag indicating the job is currently executing.
	 * 
	 * @return {@literal true} if the job is executing
	 */
	boolean isExecuting();

	/**
	 * Get the previous execution time of the job.
	 * 
	 * <p>
	 * If the job is currently executing, this value represents the time the job
	 * started.
	 * </p>
	 * 
	 * @return the previous execution time, or {@literal null} if the job has
	 *         never run before
	 */
	DateTime getPreviousExecutionTime();

	/**
	 * Get the next execution time of the job.
	 * 
	 * @return the next execution time, or {@literal null} if no more executions
	 *         are scheduled
	 */
	DateTime getNextExecutionTime();

	/**
	 * Get a description of the execution schedule of the job.
	 * 
	 * <p>
	 * The description might be a period like "every 10 minutes" or a cron
	 * expression, for example.
	 * </p>
	 * 
	 * @return a description of the execution schedule
	 */
	String getExecutionScheduleDescription();

}
