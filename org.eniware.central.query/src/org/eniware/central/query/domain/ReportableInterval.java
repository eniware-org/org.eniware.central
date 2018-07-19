/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.domain;

import java.util.TimeZone;

import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The overall interval range something can be reported on.
 * 
 * <p>
 * This represents the overall date range of available data within some query
 * set.
 * </p>
 * 
 * @version 1.1
 */
public final class ReportableInterval {

	private final ReadableInterval interval;
	private final TimeZone tz;

	/**
	 * Constructor.
	 * 
	 * @param interval
	 *        the interval
	 * @param tz
	 *        the Edge's time zone (may be <em>null</em>)
	 */
	public ReportableInterval(ReadableInterval interval, TimeZone tz) {
		this.interval = interval;
		this.tz = tz;
	}

	/**
	 * Get a count of days this interval spans (inclusive).
	 * 
	 * <p>
	 * This is the complete number of calendar days the data is present in, so
	 * partial days are counted as full days. For example, the interval
	 * {@code 2008-08-11/2009-08-05} returns 360.
	 * </p>
	 * 
	 * @return count of days within the interval
	 */
	public Long getDayCount() {
		if ( interval == null ) {
			return Long.valueOf(0);
		}
		return Long.valueOf(interval.toPeriod(PeriodType.days()).getDays() + 1);
	}

	/**
	 * Get a count of months this interval spans (inclusive).
	 * 
	 * <p>
	 * This is the complete number of calendar months the data is present in, so
	 * partial months are counted as full months. For example, the interval
	 * {@code 2008-08-11/2009-08-05} returns 13.
	 * </p>
	 * 
	 * @return count of months within the interval
	 */
	public Long getMonthCount() {
		if ( interval == null ) {
			return Long.valueOf(0);
		}
		ReadableInstant s = interval.getStart().withDayOfMonth(
				interval.getStart().dayOfMonth().getMinimumValue());
		ReadableInstant e = interval.getEnd().withDayOfMonth(
				interval.getEnd().dayOfMonth().getMaximumValue());
		Period p = new Period(s, e, PeriodType.months());
		return Long.valueOf(p.getMonths() + 1);
	}

	/**
	 * Get a count of years this interval spans (inclusive).
	 * 
	 * <p>
	 * This is the complete number of calendar years the data is present in, so
	 * partial years are counted as full years. For example, the interval
	 * {@code 2008-08-11/2009-08-05} returns 2.
	 * </p>
	 * 
	 * @return count of months within the interval
	 */
	public Long getYearCount() {
		if ( interval == null ) {
			return Long.valueOf(0);
		}
		int s = interval.getStart().getYear();
		int e = interval.getEnd().getYear();
		return Long.valueOf((e - s) + 1);
	}

	/**
	 * Get the start date.
	 * 
	 * @return the startDate
	 */
	public LocalDateTime getStartDate() {
		if ( interval == null ) {
			return null;
		}
		return tz == null ? interval.getStart().toLocalDateTime() : interval.getStart()
				.toDateTime(DateTimeZone.forTimeZone(tz)).toLocalDateTime();
	}

	/**
	 * Get the end date.
	 * 
	 * @return the endDate
	 */
	public LocalDateTime getEndDate() {
		if ( interval == null ) {
			return null;
		}
		return tz == null ? interval.getEnd().toLocalDateTime() : interval.getEnd()
				.toDateTime(DateTimeZone.forTimeZone(tz)).toLocalDateTime();
	}

	/**
	 * Get the end date, in milliseconds since the epoch.
	 * 
	 * @return the end date in milliseconds
	 */
	public Long getEndDateMillis() {
		return interval.getEndMillis();
	}

	/**
	 * Get the start date, in milliseconds since the epoch.
	 * 
	 * @return the start date in milliseconds
	 */
	public Long getStartDateMillis() {
		return interval.getStartMillis();
	}

	@JsonIgnore
	@SerializeIgnore
	public ReadableInterval getInterval() {
		return this.interval;
	}

	public TimeZone getTimeZone() {
		return this.tz;
	}

}
