/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.Event;

/**
 * Utility methods for working with scheduled jobs.
 * @version $Revision$
 */
public final class SchedulerUtils {

	/**
	 * Create an Event suitable for a successful job completion acknowledgment.
	 * 
	 * @param job the event job
	 * @return the acknowledgment event
	 */
	public static Event createJobCompleteEvent(Event job) {
		return createJobFinishedEvent(
				SchedulerConstants.TOPIC_JOB_COMPLETE,
				(String)job.getProperty(SchedulerConstants.JOB_ID), 
				(String)job.getProperty(SchedulerConstants.JOB_GROUP),
				null);
	}
	
	/**
	 * Create an Event suitable for a job failure acknowledgment.
	 * 
	 * @param job the event job
	 * @return the acknowledgment event
	 */
	public static Event createJobFailureEvent(Event job, Throwable t) {
		return createJobFinishedEvent(
				SchedulerConstants.TOPIC_JOB_FAILURE,
				(String)job.getProperty(SchedulerConstants.JOB_ID), 
				(String)job.getProperty(SchedulerConstants.JOB_GROUP),
				t);
	}
	
	/**
	 * Create an Event suitable for a job acknowledgment.
	 * 
	 * @param topic the job topic
	 * @param jobId the job ID
	 * @param jobGroup the optional job group
	 * @param e the optional exception
	 * @return the acknowledgment event
	 */
	public static Event createJobFinishedEvent(String topic, String jobId, 
			String jobGroup, Throwable e) {
		Map<String, Object> props = new HashMap<String, Object>(2);
		props.put(SchedulerConstants.JOB_ID, jobId);
		if ( jobGroup != null ) {
			props.put(SchedulerConstants.JOB_GROUP, jobGroup);
		}
		if ( e != null ) {
			props.put(SchedulerConstants.JOB_EXCEPTION, e);
		}
		return new Event(topic, props);
	}
	
	// can't create me
	private SchedulerUtils() {
		super();
	}
	
}
