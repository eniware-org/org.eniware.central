/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.alerts;

import java.util.HashMap;
import java.util.Map;

import org.eniware.central.scheduler.JobSupport;
import org.eniware.central.scheduler.SchedulerConstants;
import org.eniware.central.user.domain.UserAlertSituation;
import org.eniware.central.user.domain.UserAlertType;
import org.joda.time.DateTime;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * Job to look for {@link UserAlertType#EdgeStaleData} needing of creating /
 * updating a {@link UserAlertSituation} for.
 * 
 * @version 1.0
 */
public class UserAlertBatchJob extends JobSupport {

	/**
	 * The job property for the starting alert ID to use. If not specified,
	 * start with the smallest alert ID available.
	 */
	public static final String JOB_PROP_STARTING_ID = "AlertIdStart";

	/**
	 * The job property for the valid date to use, as milliseconds since the
	 * epoch. If not specified, use the current date.
	 */
	public static final String JOB_PROP_VALID_DATE = "AlertValidDate";

	private final UserAlertBatchProcessor processor;

	private static final ThreadLocal<Map<String, Object>> props = new ThreadLocal<Map<String, Object>>() {

		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>(2);
		}

	};

	/**
	 * Construct with properties.
	 * 
	 * @param eventAdmin
	 *        the EventAdmin
	 * @param userAlertDao
	 *        the UserAlertDao to use
	 */
	public UserAlertBatchJob(EventAdmin eventAdmin, UserAlertBatchProcessor processor) {
		super(eventAdmin);
		this.processor = processor;
		setJobGroup("UserAlert");
		setMaximumWaitMs(1800000L);
	}

	@Override
	protected boolean handleJob(Event job) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, ?> inputProperties = (Map<String, ?>) job
				.getProperty(SchedulerConstants.JOB_PROPERTIES);

		// reset thread-local
		props.get().clear();

		Long startingId = null;
		Long validDateMs = null;
		if ( inputProperties != null ) {
			startingId = (Long) inputProperties.get(JOB_PROP_STARTING_ID);
			validDateMs = (Long) inputProperties.get(JOB_PROP_VALID_DATE);
		}
		DateTime validDate = (validDateMs == null ? new DateTime() : new DateTime(validDateMs));
		if ( processor != null ) {
			startingId = processor.processAlerts(startingId, validDate);
			if ( startingId != null ) {
				props.get().put(JOB_PROP_STARTING_ID, startingId);
				props.get().put(JOB_PROP_VALID_DATE, validDate.getMillis());
			}
		}

		return true;
	}

	@Override
	protected Event handleJobCompleteEvent(Event jobEvent, boolean complete, Throwable thrown) {
		Event ack = super.handleJobCompleteEvent(jobEvent, complete, thrown);

		// add JOB_PROPERTIES Map with JOB_PROP_STARTING_Edge_ID to save with job
		Map<String, Object> jobProps = new HashMap<String, Object>();
		for ( String key : ack.getPropertyNames() ) {
			jobProps.put(key, ack.getProperty(key));
		}

		jobProps.put(SchedulerConstants.JOB_PROPERTIES, new HashMap<String, Object>(props.get()));

		ack = new Event(ack.getTopic(), jobProps);

		return ack;
	}

	public UserAlertBatchProcessor getUserAlertDao() {
		return processor;
	}

}
