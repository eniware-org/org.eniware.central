/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.scheduler;

/**
 * Constants for the Scheduler API.
 * 
 * <p>
 * The scheduler works by sending OSGi EventAdmin events. Post a
 * {@link #TOPIC_JOB_REQUEST} event to schedule a job. The job will run at the
 * time specified by the {@link #JOB_DATE} property, triggering a new event
 * using the topic specified by the request {@link #JOB_TOPIC} property.
 * </p>
 * 
 * <p>
 * When the job executes, it will expect an acknowledgment event to be posted,
 * either {@link #TOPIC_JOB_COMPLETE} or {@link #TOPIC_JOB_FAILURE}, so the job
 * can tell if the work was executed or not. The job will wait at most
 * {@link #JOB_MAX_WAIT} milliseconds (using a default value if this is not
 * specified on the job request), after which it will time out.
 * </p>
 * @version 1.0
 */
public final class SchedulerConstants {

	/** Topic for a job request notification. */
	public static final String TOPIC_JOB_REQUEST = "net/solarnetwork/central/scheduler/JOB_REQUEST";

	/** Topic for a job complete notification. */
	public static final String TOPIC_JOB_COMPLETE = "net/solarnetwork/central/scheduler/JOB_COMPLETE";

	/** Topic for a job failure notification. */
	public static final String TOPIC_JOB_FAILURE = "net/solarnetwork/central/scheduler/JOB_FAILURE";

	/** Topic of event sent when scheduler ready. */
	public static final String TOPIC_SCHEDULER_READY = "net/solarnetwork/central/scheduler/SCHEDULER_READY";

	///** Topic for a job notification. */
	//public static final String TOPIC_JOB = "net/solarnetwork/central/scheduler/JOB";

	/** A unique ID for the job. */
	public static final String JOB_ID = "JobId";

	/** An optional group to schedule the job with. */
	public static final String JOB_GROUP = "JobGroup";

	/** The OSGi Event topic to use for the job notification. */
	public static final String JOB_TOPIC = "JobTopic";

	/** A Long that represents the exact date to run the job at. */
	public static final String JOB_DATE = "JobDate";

	/** A java.util.Map of properties to associate with the job. */
	public static final String JOB_PROPERTIES = "JobProperties";

	/** A Throwable to associate with a (failed) job. */
	public static final String JOB_EXCEPTION = "JobException";

	/**
	 * A Long representing the maximum number of milliseconds to wait for the
	 * job to complete.
	 */
	public static final String JOB_MAX_WAIT = "JobMaxWait";

	/** A cron expression to run the job with, instead of a specific date. */
	public static final String JOB_CRON_EXPRESSION = "JobCron";

	// you can't create me
	private SchedulerConstants() {
		super();
	}

}
