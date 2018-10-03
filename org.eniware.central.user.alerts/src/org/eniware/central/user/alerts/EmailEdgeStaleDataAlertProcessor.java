/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.alerts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.eniware.central.RepeatableTaskException;
import org.eniware.central.dao.EniwareEdgeDao;
import org.eniware.central.datum.dao.GeneralEdgeDatumDao;
import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.datum.domain.GeneralEdgeDatumFilterMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.EniwareEdge;
import org.eniware.central.mail.MailService;
import org.eniware.central.mail.support.BasicMailAddress;
import org.eniware.central.mail.support.ClasspathResourceMessageTemplateDataSource;
import org.eniware.central.user.dao.UserAlertDao;
import org.eniware.central.user.dao.UserAlertSituationDao;
import org.eniware.central.user.dao.UserDao;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserAlert;
import org.eniware.central.user.domain.UserAlertOptions;
import org.eniware.central.user.domain.UserAlertSituation;
import org.eniware.central.user.domain.UserAlertSituationStatus;
import org.eniware.central.user.domain.UserAlertStatus;
import org.eniware.central.user.domain.UserAlertType;
import org.eniware.central.user.domain.UserEdge;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

/**
 * Process stale data alerts for Edges.
 * 
 * @version 1.2
 */
public class EmailEdgeStaleDataAlertProcessor implements UserAlertBatchProcessor {

	/** The default value for {@link #getBatchSize()}. */
	public static final Integer DEFAULT_BATCH_SIZE = 50;

	/** The default value for {@link #getMailTemplateResource()}. */
	public static final String DEFAULT_MAIL_TEMPLATE_RESOURCE = "/net/eniwarenetwork/central/user/alerts/user-alert-EdgeStaleData.txt";

	/** The default value for {@link #getMailTemplateResolvedResource()}. */
	public static final String DEFAULT_MAIL_TEMPLATE_RESOLVED_RESOURCE = "/net/eniwarenetwork/central/user/alerts/user-alert-EdgeStaleData-Resolved.txt";

	/**
	 * A {@code UserAlertSituation} {@code info} key for an associated Edge ID.
	 * 
	 * @since 1.1
	 */
	public static final String SITUATION_INFO_Edge_ID = "EdgeId";

	/**
	 * A {@code UserAlertSituation} {@code info} key for an associated source
	 * ID.
	 * 
	 * @since 1.1
	 */
	public static final String SITUATION_INFO_SOURCE_ID = "sourceId";

	/**
	 * A {@code UserAlertSituation} {@code info} key for an associated datum
	 * creation date.
	 * 
	 * @since 1.1
	 */
	public static final String SITUATION_INFO_DATUM_CREATED = "datumCreated";

	private final EniwareEdgeDao eniwareEdgeDao;
	private final UserDao userDao;
	private final UserEdgeDao userEdgeDao;
	private final UserAlertDao userAlertDao;
	private final UserAlertSituationDao userAlertSituationDao;
	private final GeneralEdgeDatumDao generalEdgeDatumDao;
	private final MailService mailService;
	private Integer batchSize = DEFAULT_BATCH_SIZE;
	private final MessageSource messageSource;
	private String mailTemplateResource = DEFAULT_MAIL_TEMPLATE_RESOURCE;
	private String mailTemplateResolvedResource = DEFAULT_MAIL_TEMPLATE_RESOLVED_RESOURCE;
	private DateTimeFormatter timestampFormat = DateTimeFormat.forPattern("d MMM yyyy HH:mm z");
	private int initialAlertReminderDelayMinutes = 60;
	private int alertReminderFrequencyMultiplier = 4;

	// maintain a cache of Edge data during the execution of the job (cleared after each invocation)
	private final Map<Long, EniwareEdge> EdgeCache = new HashMap<Long, EniwareEdge>(64);
	private final Map<Long, List<GeneralEdgeDatumFilterMatch>> EdgeDataCache = new HashMap<Long, List<GeneralEdgeDatumFilterMatch>>(
			64);
	private final Map<Long, List<GeneralEdgeDatumFilterMatch>> userDataCache = new HashMap<Long, List<GeneralEdgeDatumFilterMatch>>(
			16);

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Construct with properties.
	 * 
	 * @param eniwareEdgeDao
	 *        The {@link EniwareEdgeDao} to use.
	 * @param userDao
	 *        The {@link UserDao} to use.
	 * @param userEdgeDao
	 *        The {@link UserEdgeDao} to use.
	 * @param userAlertDao
	 *        The {@link UserAlertDao} to use.
	 * @param userAlertSituationDao
	 *        The {@link UserAlertSituationDao} to use.
	 * @param generalEdgeDatumDao
	 *        The {@link GeneralEdgeDatumDao} to use.
	 * @param mailService
	 *        The {@link MailService} to use.
	 * @param messageSource
	 *        The {@link MessageSource} to use.
	 */
	public EmailEdgeStaleDataAlertProcessor(EniwareEdgeDao eniwareEdgeDao, UserDao userDao,
			UserEdgeDao userEdgeDao, UserAlertDao userAlertDao,
			UserAlertSituationDao userAlertSituationDao, GeneralEdgeDatumDao generalEdgeDatumDao,
			MailService mailService, MessageSource messageSource) {
		super();
		this.eniwareEdgeDao = eniwareEdgeDao;
		this.userDao = userDao;
		this.userEdgeDao = userEdgeDao;
		this.userAlertDao = userAlertDao;
		this.userAlertSituationDao = userAlertSituationDao;
		this.generalEdgeDatumDao = generalEdgeDatumDao;
		this.mailService = mailService;
		this.messageSource = messageSource;
	}

	/**
	 * Get the current system time. Exposed to support testing.
	 * 
	 * @return The current system time.
	 * @since 1.2
	 */
	protected long getCurrentTime() {
		return System.currentTimeMillis();
	}

	@Override
	public Long processAlerts(Long lastProcessedAlertId, DateTime validDate) {
		if ( validDate == null ) {
			validDate = new DateTime();
		}
		List<UserAlert> alerts = userAlertDao.findAlertsToProcess(UserAlertType.EdgeStaleData,
				lastProcessedAlertId, validDate, batchSize);
		Long lastAlertId = null;
		final long now = getCurrentTime();
		final DateTime nowDateTime = new DateTime(now);
		final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("H:mm");
		try {
			loadMostRecentEdgeData(alerts);
			for ( UserAlert alert : alerts ) {
				Map<String, Object> alertOptions = alert.getOptions();
				if ( alertOptions == null ) {
					continue;
				}

				// extract options
				Number age;
				String[] sourceIds = null;
				try {
					age = (Number) alertOptions.get(UserAlertOptions.AGE_THRESHOLD);
					@SuppressWarnings("unchecked")
					List<String> sources = (List<String>) alertOptions.get(UserAlertOptions.SOURCE_IDS);
					if ( sources != null ) {
						sourceIds = sources.toArray(new String[sources.size()]);
					}
				} catch ( ClassCastException e ) {
					log.warn("Unexpected option data type in alert {}: {}", alert, e.getMessage());
					continue;
				}

				if ( age == null ) {
					log.debug("Skipping alert {} that does not include {} option", alert,
							UserAlertOptions.AGE_THRESHOLD);
					continue;
				}

				if ( sourceIds != null ) {
					// sort so we can to binarySearch later
					Arrays.sort(sourceIds);
				}

				// look for first stale data matching age + source criteria
				final List<Interval> timePeriods = new ArrayList<Interval>(2);
				GeneralEdgeDatumFilterMatch stale = getFirstStaleDatum(alert, nowDateTime, age,
						sourceIds, timeFormatter, timePeriods);

				Map<String, Object> staleInfo = new HashMap<String, Object>(4);
				if ( stale != null ) {
					staleInfo.put(SITUATION_INFO_DATUM_CREATED,
							Long.valueOf(stale.getId().getCreated().getMillis()));
					staleInfo.put(SITUATION_INFO_Edge_ID, stale.getId().getEdgeId());
					staleInfo.put(SITUATION_INFO_SOURCE_ID, stale.getId().getSourceId());
				}

				// get UserAlertSitutation for this alert
				UserAlertSituation sit = userAlertSituationDao
						.getActiveAlertSituationForAlert(alert.getId());
				if ( stale != null ) {
					long notifyOffset = 0;
					if ( sit == null ) {
						sit = new UserAlertSituation();
						sit.setCreated(new DateTime(now));
						sit.setAlert(alert);
						sit.setStatus(UserAlertSituationStatus.Active);
						sit.setNotified(new DateTime(now));
						sit.setInfo(staleInfo);

					} else if ( sit.getNotified().equals(sit.getCreated()) ) {
						notifyOffset = (initialAlertReminderDelayMinutes * 60L * 1000L);
					} else {
						notifyOffset = ((sit.getNotified().getMillis() - sit.getCreated().getMillis())
								* alertReminderFrequencyMultiplier);
					}

					// taper off the alerts so the become less frequent over time
					if ( (sit.getNotified().getMillis() + notifyOffset) <= now ) {
						sendAlertMail(alert, "user.alert.EdgeStaleData.mail.subject",
								mailTemplateResource, stale);
						sit.setNotified(new DateTime(now));
					}
					if ( sit.getNotified().getMillis() == now || sit.getInfo() == null
							|| !staleInfo.equals(sit.getInfo()) ) {
						userAlertSituationDao.store(sit);
					}
				} else {
					// not stale, so mark valid for age span
					final boolean withinTimePeriods = withinIntervals(now, timePeriods);
					DateTime newValidTo;
					if ( !timePeriods.isEmpty() && !withinTimePeriods ) {
						// we're not in valid to the start of the next time period
						newValidTo = startOfNextTimePeriod(now, timePeriods);
					} else {
						newValidTo = validDate.plusSeconds(age.intValue());
					}
					log.debug("Marking alert {} valid to {}", alert.getId(), newValidTo);
					userAlertDao.updateValidTo(alert.getId(), newValidTo);
					alert.setValidTo(newValidTo);
					if ( sit != null && withinTimePeriods ) {
						// make Resolved
						sit.setStatus(UserAlertSituationStatus.Resolved);
						sit.setNotified(new DateTime(now));
						userAlertSituationDao.store(sit);

						GeneralEdgeDatumFilterMatch nonStale = getFirstNonStaleDatum(alert, now, age,
								sourceIds);

						sendAlertMail(alert, "user.alert.EdgeStaleData.Resolved.mail.subject",
								mailTemplateResolvedResource, nonStale);
					}
				}
				lastAlertId = alert.getId();
			}
		} catch ( RuntimeException e ) {
			throw new RepeatableTaskException("Error processing user alerts", e, lastAlertId);
		} finally {
			EdgeCache.clear();
			EdgeDataCache.clear();
			userDataCache.clear();
		}

		// short-circuit performing batch for no results if obvious
		if ( alerts.size() < batchSize && lastAlertId != null
				&& lastAlertId.equals(alerts.get(alerts.size() - 1).getId()) ) {
			// we've finished our batch
			lastAlertId = null;
		}

		return lastAlertId;
	}

	private List<Interval> parseAlertTimeWindows(final DateTime nowDateTime,
			final DateTimeFormatter timeFormatter, final UserAlert alert, final Long EdgeId) {
		Map<String, Object> alertOptions = alert.getOptions();
		if ( alertOptions == null ) {
			return null;
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> windows = (List<Map<String, Object>>) alertOptions
				.get(UserAlertOptions.TIME_WINDOWS);
		if ( windows == null ) {
			return null;
		}
		final Long intervalEdgeId = (EdgeId != null ? EdgeId : alert.getEdgeId());
		List<Interval> timePeriods = new ArrayList<Interval>(windows.size());
		for ( Map<String, Object> window : windows ) {
			Object s = window.get("timeStart");
			Object e = window.get("timeEnd");
			if ( s != null && e != null ) {
				try {
					LocalTime start = timeFormatter.parseLocalTime(s.toString());
					LocalTime end = timeFormatter.parseLocalTime(e.toString());
					EniwareEdge Edge = EdgeCache.get(intervalEdgeId);
					DateTimeZone tz = DateTimeZone.UTC;
					if ( Edge != null ) {
						TimeZone EdgeTz = Edge.getTimeZone();
						if ( EdgeTz != null ) {
							tz = DateTimeZone.forTimeZone(EdgeTz);
						}
					} else {
						log.warn("Edge {} not available, defaulting to UTC time zone", intervalEdgeId);
					}
					DateTime startTimeToday = start.toDateTime(nowDateTime.toDateTime(tz));
					DateTime endTimeToday = end.toDateTime(nowDateTime.toDateTime(tz));
					timePeriods.add(new Interval(startTimeToday, endTimeToday));
				} catch ( IllegalArgumentException t ) {
					log.warn("Error parsing time window time: {}", t.getMessage());
				}
			}
		}
		if ( timePeriods.size() > 0 ) {
			// sort by start dates if there is more than one interval
			Collections.sort(timePeriods, new Comparator<Interval>() {

				@Override
				public int compare(Interval o1, Interval o2) {
					return o1.getStart().compareTo(o2.getStart());
				}
			});
		} else {
			timePeriods = null;
		}
		return timePeriods;
	}

	private void loadMostRecentEdgeData(List<UserAlert> alerts) {
		// reset cache
		EdgeCache.clear();
		EdgeDataCache.clear();
		userDataCache.clear();

		// keep a reverse Edge ID -> user ID mapping
		Map<Long, Long> EdgeUserMapping = new HashMap<Long, Long>();

		// get set of unique user IDs and/or Edge IDs
		Set<Long> EdgeIds = new HashSet<Long>(alerts.size());
		Set<Long> userIds = new HashSet<Long>(alerts.size());
		for ( UserAlert alert : alerts ) {
			if ( alert.getEdgeId() != null ) {
				EdgeIds.add(alert.getEdgeId());
			} else {
				userIds.add(alert.getUserId());

				// need to associate all possible Edge IDs to this user ID
				List<UserEdge> Edges = userEdgeDao
						.findUserEdgesForUser(new User(alert.getUserId(), null));
				for ( UserEdge userEdge : Edges ) {
					EdgeCache.put(userEdge.getEdge().getId(), userEdge.getEdge());
					EdgeUserMapping.put(userEdge.getEdge().getId(), alert.getUserId());
				}
			}
		}

		// load up data for users first, as that might pull in all Edge data already
		if ( userIds.isEmpty() == false ) {
			DatumFilterCommand filter = new DatumFilterCommand();
			filter.setUserIds(userIds.toArray(new Long[userIds.size()]));
			filter.setMostRecent(true);
			FilterResults<GeneralEdgeDatumFilterMatch> latestEdgeData = generalEdgeDatumDao
					.findFiltered(filter, null, null, null);
			for ( GeneralEdgeDatumFilterMatch match : latestEdgeData.getResults() ) {
				// first add to Edge list
				List<GeneralEdgeDatumFilterMatch> datumMatches = EdgeDataCache
						.get(match.getId().getEdgeId());
				if ( datumMatches == null ) {
					datumMatches = new ArrayList<GeneralEdgeDatumFilterMatch>();
					EdgeDataCache.put(match.getId().getEdgeId(), datumMatches);
				}
				datumMatches.add(match);

				// now add match to User list
				Long userId = EdgeUserMapping.get(match.getId().getEdgeId());
				if ( userId == null ) {
					log.warn("No user ID found for Edge ID: {}", match.getId().getEdgeId());
					continue;
				}
				datumMatches = userDataCache.get(userId);
				if ( datumMatches == null ) {
					datumMatches = new ArrayList<GeneralEdgeDatumFilterMatch>();
					userDataCache.put(userId, datumMatches);
				}
				datumMatches.add(match);
			}
			log.debug("Loaded most recent datum for users {}: {}", userIds, userDataCache);
		}

		// we can remove any Edges already fetched via user query
		EdgeIds.removeAll(EdgeUserMapping.keySet());

		// for any Edge IDs still around, query for them now
		if ( EdgeIds.isEmpty() == false ) {
			DatumFilterCommand filter = new DatumFilterCommand();
			filter.setEdgeIds(EdgeIds.toArray(new Long[EdgeIds.size()]));
			filter.setMostRecent(true);
			FilterResults<GeneralEdgeDatumFilterMatch> latestEdgeData = generalEdgeDatumDao
					.findFiltered(filter, null, null, null);
			for ( GeneralEdgeDatumFilterMatch match : latestEdgeData.getResults() ) {
				List<GeneralEdgeDatumFilterMatch> datumMatches = EdgeDataCache
						.get(match.getId().getEdgeId());
				if ( datumMatches == null ) {
					datumMatches = new ArrayList<GeneralEdgeDatumFilterMatch>();
					EdgeDataCache.put(match.getId().getEdgeId(), datumMatches);
				}
				if ( !EdgeCache.containsKey(match.getId().getEdgeId()) ) {
					EdgeCache.put(match.getId().getEdgeId(),
							eniwareEdgeDao.get(match.getId().getEdgeId()));
				}
				datumMatches.add(match);
			}
			log.debug("Loaded most recent datum for Edges {}: {}", EdgeIds, EdgeDataCache);
		}
	}

	/**
	 * Get list of most recent datum associated with an alert. Depends on
	 * {@link #loadMostRecentEdgeData(List)} having been already called.
	 * 
	 * @param alert
	 *        The alert to get the most recent data for.
	 * @return The associated data, never <em>null</em>.
	 */
	private List<GeneralEdgeDatumFilterMatch> getLatestEdgeData(final UserAlert alert) {
		List<GeneralEdgeDatumFilterMatch> results;
		if ( alert.getEdgeId() != null ) {
			results = EdgeDataCache.get(alert.getEdgeId());
		} else {
			results = userDataCache.get(alert.getUserId());
		}
		return (results == null ? Collections.<GeneralEdgeDatumFilterMatch> emptyList() : results);
	}

	private boolean withinIntervals(final long now, List<Interval> intervals) {
		if ( intervals == null ) {
			return true;
		}
		for ( Interval i : intervals ) {
			if ( !i.contains(now) ) {
				return false;
			}
		}
		return true;
	}

	private DateTime startOfNextTimePeriod(final long now, List<Interval> intervals) {
		if ( intervals == null || intervals.size() < 1 ) {
			return new DateTime();
		}
		Interval found = null;
		Interval earliest = null;
		for ( Interval i : intervals ) {
			if ( i.isAfter(now) && (found == null || found.isAfter(i.getStartMillis())) ) {
				// this time period starts later than now, so that is the next period to work with
				found = i;
			}
			if ( earliest == null || earliest.isAfter(i.getStartMillis()) ) {
				earliest = i;
			}
		}

		if ( found != null ) {
			return found.getStart();
		}

		// no time period later than now, so make the next period the start of the earliest interval, tomorrow
		return earliest.getStart().plusDays(1);
	}

	private GeneralEdgeDatumFilterMatch getFirstStaleDatum(final UserAlert alert, final DateTime now,
			final Number age, final String[] sourceIds, final DateTimeFormatter timeFormatter,
			final List<Interval> outputIntervals) {
		GeneralEdgeDatumFilterMatch stale = null;
		List<GeneralEdgeDatumFilterMatch> latestEdgeData = getLatestEdgeData(alert);
		List<Interval> intervals = new ArrayList<Interval>(2);
		if ( alert.getEdgeId() != null ) {
			try {
				intervals = parseAlertTimeWindows(now, timeFormatter, alert, alert.getEdgeId());
			} catch ( ClassCastException e ) {
				log.warn("Unexpected option data type in alert {}: {}", alert, e.getMessage());
			}
		}

		for ( GeneralEdgeDatumFilterMatch datum : latestEdgeData ) {
			List<Interval> EdgeIntervals = intervals;
			if ( alert.getEdgeId() == null ) {
				try {
					EdgeIntervals = parseAlertTimeWindows(now, timeFormatter, alert,
							datum.getId().getEdgeId());
					if ( EdgeIntervals != null ) {
						for ( Interval interval : EdgeIntervals ) {
							if ( !intervals.contains(interval) ) {
								intervals.add(interval);
							}
						}
					}
				} catch ( ClassCastException e ) {
					log.warn("Unexpected option data type in alert {}: {}", alert, e.getMessage());
					continue;
				}
			}
			if ( datum.getId().getCreated().getMillis() + (long) (age.doubleValue() * 1000) < now
					.getMillis()
					&& (sourceIds == null
							|| Arrays.binarySearch(sourceIds, datum.getId().getSourceId()) >= 0)
					&& withinIntervals(now.getMillis(), EdgeIntervals) ) {
				stale = datum;
				break;
			}
		}
		if ( intervals != null && outputIntervals != null ) {
			outputIntervals.addAll(intervals);
		}
		return stale;
	}

	private GeneralEdgeDatumFilterMatch getFirstNonStaleDatum(final UserAlert alert, final long now,
			final Number age, final String[] sourceIds) {
		GeneralEdgeDatumFilterMatch nonStale = null;
		List<GeneralEdgeDatumFilterMatch> latestEdgeData = getLatestEdgeData(alert);
		for ( GeneralEdgeDatumFilterMatch datum : latestEdgeData ) {
			if ( datum.getId().getCreated().getMillis() + (long) (age.doubleValue() * 1000) >= now
					&& (sourceIds == null
							|| Arrays.binarySearch(sourceIds, datum.getId().getSourceId()) >= 0) ) {
				nonStale = datum;
				break;
			}
		}
		return nonStale;
	}

	private void sendAlertMail(UserAlert alert, String subjectKey, String resourcePath,
			GeneralEdgeDatumFilterMatch datum) {
		if ( alert.getStatus() == UserAlertStatus.Suppressed ) {
			// no emails for this alert
			log.debug("Alert email suppressed: {}; datum {}; subject {}", alert, datum, subjectKey);
			return;
		}
		User user = userDao.get(alert.getUserId());
		EniwareEdge Edge = (datum != null ? EdgeCache.get(datum.getId().getEdgeId()) : null);
		if ( user != null && Edge != null ) {
			BasicMailAddress addr = new BasicMailAddress(user.getName(), user.getEmail());
			Locale locale = Locale.US; // TODO: get Locale from User entity
			Map<String, Object> model = new HashMap<String, Object>(4);
			model.put("alert", alert);
			model.put("user", user);
			model.put("datum", datum);

			// add a formatted datum date to model
			DateTimeFormatter dateFormat = timestampFormat.withLocale(locale);
			if ( Edge != null && Edge.getTimeZone() != null ) {
				dateFormat = dateFormat.withZone(DateTimeZone.forTimeZone(Edge.getTimeZone()));
			}
			model.put("datumDate", dateFormat.print(datum.getId().getCreated()));

			String subject = messageSource.getMessage(subjectKey,
					new Object[] { datum.getId().getEdgeId() }, locale);

			log.debug("Sending EdgeStaleData alert {} to {} with model {}", subject, user.getEmail(),
					model);
			ClasspathResourceMessageTemplateDataSource msg = new ClasspathResourceMessageTemplateDataSource(
					locale, subject, resourcePath, model);
			msg.setClassLoader(getClass().getClassLoader());
			mailService.sendMail(addr, msg);
		}
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public String getMailTemplateResource() {
		return mailTemplateResource;
	}

	public void setMailTemplateResource(String mailTemplateResource) {
		this.mailTemplateResource = mailTemplateResource;
	}

	public DateTimeFormatter getTimestampFormat() {
		return timestampFormat;
	}

	public void setTimestampFormat(DateTimeFormatter timestampFormat) {
		this.timestampFormat = timestampFormat;
	}

	public String getMailTemplateResolvedResource() {
		return mailTemplateResolvedResource;
	}

	public void setMailTemplateResolvedResource(String mailTemplateResolvedResource) {
		this.mailTemplateResolvedResource = mailTemplateResolvedResource;
	}

	public int getInitialAlertReminderDelayMinutes() {
		return initialAlertReminderDelayMinutes;
	}

	public void setInitialAlertReminderDelayMinutes(int initialAlertReminderDelayMinutes) {
		this.initialAlertReminderDelayMinutes = initialAlertReminderDelayMinutes;
	}

	public int getAlertReminderFrequencyMultiplier() {
		return alertReminderFrequencyMultiplier;
	}

	public void setAlertReminderFrequencyMultiplier(int alertReminderFrequencyMultiplier) {
		this.alertReminderFrequencyMultiplier = alertReminderFrequencyMultiplier;
	}

}
