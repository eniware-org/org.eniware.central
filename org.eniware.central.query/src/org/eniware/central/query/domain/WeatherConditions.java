/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.domain;

import java.util.TimeZone;

import org.eniware.central.datum.domain.DayDatum;
import org.eniware.central.datum.domain.WeatherDatum;

/**
 * A snapshot of weather conditions at a particular time.
 * 
 * @author matt
 * @version 1.0
 */
public class WeatherConditions {

	private final WeatherDatum weather;
	private final DayDatum day;
	private final TimeZone timeZone;

	/**
	 * Construct with values.
	 * 
	 * @param weather
	 *        the weather
	 * @param day
	 *        the day
	 * @param timeZone
	 *        the time zone
	 */
	public WeatherConditions(WeatherDatum weather, DayDatum day, TimeZone timeZone) {
		super();
		this.weather = weather;
		this.day = day;
		this.timeZone = timeZone;
	}

	public WeatherDatum getWeather() {
		return weather;
	}

	public DayDatum getDay() {
		return day;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

}
