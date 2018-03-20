/* ===================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ===================================================================
 */

package org.eniware.central.domain;

/**
 * An aggregation level enumeration.
 * 
 * @version 1.5
 */
public enum Aggregation {

	/** Minute level aggregation. */
	Minute(60),

	/** Five minute level aggregation. */
	FiveMinute(60 * 5),

	/** Ten minute level aggregation. */
	TenMinute(60 * 10),

	/** Fifteen minute level aggregation. */
	FifteenMinute(60 * 15),

	/**
	 * Thirty minute level aggregation.
	 * 
	 * @since 1.5
	 */
	ThirtyMinute(60 * 30),

	/** Hour level aggregation. */
	Hour(3600),

	/** Aggregate by hour of the day, e.g. compare 12-1pm across multiple days. */
	HourOfDay(3600),

	/** Aggregate by hour of the day per season. */
	SeasonalHourOfDay(3600),

	/** Day level aggregation. */
	Day(86400),

	/**
	 * Aggregate by day of the week, e.g. compare Mondays against Tuesdays
	 * across multiple weeks.
	 */
	DayOfWeek(86400),

	/** Aggregate by day of the week per season. */
	SeasonalDayOfWeek(86400),

	/** Week level aggregation. */
	Week(604800),

	/**
	 * Aggregate by week of the year, e.g. compare Week 1's against Week 2's
	 * across multiple years.
	 */
	WeekOfYear(604800),

	/** Month level aggregation. */
	Month(2419200),

	/**
	 * Aggregate all values into a single total result.
	 * 
	 * @since 1.4
	 */
	RunningTotal(Integer.MAX_VALUE);

	private final Integer level;

	private Aggregation(int level) {
		this.level = level;
	}

	/**
	 * Compare the level of this to another.
	 * 
	 * @param other
	 *        the other
	 * @return -1 if this level less than other level, 0 if levels are equal, or
	 *         1 if this level is greater than other level
	 */
	public int compareLevel(Aggregation other) {
		return this.level.compareTo(other.level);
	}

	/**
	 * Get the number of seconds the aggregation level represents.
	 * 
	 * <p>
	 * For aggregation levels higher than {@link #Day} the number of seconds are
	 * approximate, based on the following standardized periods:
	 * </p>
	 * 
	 * <ul>
	 * <li><b>Week</b> - 7 days</li>
	 * <li><b>Month</b> - 4 weeks (28 days)</li>
	 * </ul>
	 * 
	 * @return the aggregation level
	 */
	public Integer getLevel() {
		return level;
	}

}
