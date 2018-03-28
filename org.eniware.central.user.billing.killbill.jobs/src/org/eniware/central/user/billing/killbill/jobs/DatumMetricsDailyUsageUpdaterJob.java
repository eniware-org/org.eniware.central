/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.jobs;

import org.eniware.central.scheduler.JobSupport;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * Job to execute the {@link DatumMetricsDailyUsageUpdaterService}.
 * 
 * @version 1.0
 */
public class DatumMetricsDailyUsageUpdaterJob extends JobSupport {

	private final DatumMetricsDailyUsageUpdaterService service;

	/**
	 * Constructor.
	 * 
	 * @param eventAdmin
	 *        the {@link EventAdmin} to use
	 * @param service
	 *        the service to use
	 */
	public DatumMetricsDailyUsageUpdaterJob(EventAdmin eventAdmin,
			DatumMetricsDailyUsageUpdaterService service) {
		super(eventAdmin);
		setJobGroup("Billing");
		setMaximumWaitMs(3600000L);
		this.service = service;
	}

	@Override
	protected boolean handleJob(Event job) throws Exception {
		service.execute();
		return true;
	}

}
