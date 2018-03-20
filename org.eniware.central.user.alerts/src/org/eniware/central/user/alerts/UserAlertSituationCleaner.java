/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.alerts;

import org.eniware.central.scheduler.JobSupport;
import org.eniware.central.user.dao.UserAlertSituationDao;
import org.joda.time.DateTime;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * Job to periodically clean out old, resolved user alert situations.
 * 
 * @author matt
 * @version 1.0
 */
public class UserAlertSituationCleaner extends JobSupport {

	/** The default value for the {@code daysOlder} property. */
	public static final int DEFAULT_DAYS_OLDER = 30;

	private final UserAlertSituationDao dao;
	private int daysOlder = DEFAULT_DAYS_OLDER;

	/**
	 * Constructor.
	 * 
	 * @param eventAdmin
	 *        The {@link EventAdmin} to use.
	 * @param dao
	 *        The {@link UserAlertSituationDao} to use.
	 */
	public UserAlertSituationCleaner(EventAdmin eventAdmin, UserAlertSituationDao userAlertSituationDao) {
		super(eventAdmin);
		this.dao = userAlertSituationDao;
	}

	/**
	 * Purge completed situations by calling
	 * {@link UserAlertSituationDao#purgeResolvedSituations(DateTime)}.
	 */
	@Override
	protected boolean handleJob(Event job) throws Exception {
		DateTime date = new DateTime().minusDays(daysOlder);
		long result = dao.purgeResolvedSituations(date);
		log.info("Purged {} user alert situations older than {} ({} days ago)", result, date, daysOlder);
		return true;
	}

	public int getDaysOlder() {
		return daysOlder;
	}

	public void setDaysOlder(int daysOlder) {
		this.daysOlder = daysOlder;
	}

}
