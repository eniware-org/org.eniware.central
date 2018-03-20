/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.jobs;

import org.eniware.central.instructor.dao.NodeInstructionDao;
import org.eniware.central.scheduler.JobSupport;
import org.joda.time.DateTime;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * Job to periodically clean out old, completed instructions.
 * 
 * @author matt
 * @version 1.0
 */
public class NodeInstructionCleaner extends JobSupport {

	/** The default value for the {@code daysOlder} property. */
	public static final int DEFAULT_DAYS_OLDER = 30;

	private final NodeInstructionDao dao;
	private int daysOlder = DEFAULT_DAYS_OLDER;

	/**
	 * Constructor.
	 * 
	 * @param eventAdmin
	 *        The EventAdmin to use.
	 * @param dao
	 *        The NodeInstructionDao to use.
	 */
	public NodeInstructionCleaner(EventAdmin eventAdmin, NodeInstructionDao dao) {
		super(eventAdmin);
		this.dao = dao;
	}

	/**
	 * Purge completed instructions by calling
	 * {@link NodeInstructionDao#purgeCompletedInstructions(org.joda.time.DateTime)}
	 * .
	 */
	@Override
	protected boolean handleJob(Event job) throws Exception {
		DateTime date = new DateTime().minusDays(daysOlder);
		long result = dao.purgeCompletedInstructions(date);
		log.info("Purged {} node instructions older than {} ({} days ago)", result, date, daysOlder);
		return true;
	}

	/**
	 * Get the number of days old an instruction must be in order to be
	 * considered for purging.
	 * 
	 * @return The number of days old.
	 */
	public int getDaysOlder() {
		return daysOlder;
	}

	/**
	 * Set the maximum number of days old an instruction can be in order to be
	 * considered for purging.
	 * 
	 * @param daysOlder
	 *        The number of days old instructions can be before they can be
	 *        purged.
	 */
	public void setDaysOlder(int daysOlder) {
		this.daysOlder = daysOlder;
	}

}
