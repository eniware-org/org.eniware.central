/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.nim.jobs;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.eniware.central.biz.MaintenanceSubscriber;
import org.eniware.central.scheduler.JobSupport;
import org.eniware.central.user.nim.biz.EniwareEdgeImageMakerBiz;
import org.eniware.util.OptionalServiceCollection;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * Periodic job to perform maintenance on registered NIM services.
 * 
 * @version 1.0
 */
public class EniwareEdgeImageMakerMaintenanceJob extends JobSupport {

	private final OptionalServiceCollection<EniwareEdgeImageMakerBiz> services;

	/**
	 * Constructor.
	 * 
	 * @param eventAdmin
	 *        the {@link EventAdmin} to use
	 * @param services
	 *        the services to manage
	 */
	public EniwareEdgeImageMakerMaintenanceJob(EventAdmin eventAdmin,
			OptionalServiceCollection<EniwareEdgeImageMakerBiz> services) {
		super(eventAdmin);
		setJobGroup("NIM");
		setMaximumWaitMs(TimeUnit.MINUTES.toMillis(10));
		this.services = services;
	}

	@Override
	protected boolean handleJob(Event job) throws Exception {
		for ( EniwareEdgeImageMakerBiz service : services.services() ) {
			if ( service instanceof MaintenanceSubscriber ) {
				((MaintenanceSubscriber) service).performServiceMaintenance(Collections.emptyMap());
			}
		}
		return true;
	}

}
