/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.util.SerializeIgnore;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Domain object for day related data.
 * 
 * <p>
 * Note a {@code DayDatum} is not directly related to a {@code EniwareEdge}, and
 * the {@code EdgeId} value may actually be <em>null</em>. This class implements
 * both {@link EdgeDatum} and {@link LocationDatum} for ease of use, although
 * strictly speaking it is only a {@link LocationDatum}.
 * </p>
 * 
 * <p>
 * The {@code day} property reflects the year/month/day of this datum (e.g. a
 * SQL date value). The {@code sunrise} and {@code sunset} properties reflect
 * only time values for this day (e.g. a SQL time value).
 * </p>
 * 
 * <p>
 * The {@code latitude} and {@long longitude} may or may not be used, it depends
 * on how granular the Edge wants to track day information.
 * </p>
 *
 * @version 1.1
 */
public class DayDatum extends BaseEdgeDatum implements LocationDatum {

	private static final long serialVersionUID = 2802754315725736855L;

	private Long locationId;
	private LocalDate day;
	private LocalTime sunrise;
	private LocalTime sunset;
	private Float temperatureHighCelsius;
	private Float temperatureLowCelsius;
	private Float temperatureStartCelsius;
	private Float temperatureEndCelsius;
	private String skyConditions;
	private SkyCondition condition;

	/**
	 * Default constructor.
	 */
	public DayDatum() {
		super();
	}

	/**
	 * Test if another DayDatum's {@code day} value is the same as this
	 * instance's {@code day} value.
	 * 
	 * <p>
	 * Only non-null values are compared, so if both {@code day} values are
	 * <em>null</em> this method will return <em>false</em>.
	 * </p>
	 * 
	 * @param other
	 *        the DayDatum to compare to
	 * @return <em>true</em> if both {@code day} values are equal
	 */
	public boolean isSameDay(DayDatum other) {
		return this.day != null && other != null && other.day != null && this.day.equals(other.day);
	}

	@Override
	public String toString() {
		return "DayDatum{EdgeId=" + getEdgeId() + ",locationId=" + this.locationId + ",day=" + this.day
				+ ",sunrize=" + this.sunrise + ",sunset=" + this.sunset + '}';
	}

	@Override
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}

	public LocalTime getSunrise() {
		return sunrise;
	}

	public void setSunrise(LocalTime sunrise) {
		this.sunrise = sunrise;
	}

	public LocalTime getSunset() {
		return sunset;
	}

	public void setSunset(LocalTime sunset) {
		this.sunset = sunset;
	}

	@Deprecated
	@JsonIgnore
	@SerializeIgnore
	public Float getTemperatureHighCelcius() {
		return temperatureHighCelsius;
	}

	public Float getTemperatureHighCelsius() {
		return temperatureHighCelsius;
	}

	public void setTemperatureHighCelsius(Float temperatureHighCelcius) {
		this.temperatureHighCelsius = temperatureHighCelcius;
	}

	@Deprecated
	@JsonIgnore
	@SerializeIgnore
	public Float getTemperatureLowCelcius() {
		return temperatureLowCelsius;
	}

	public Float getTemperatureLowCelsius() {
		return temperatureLowCelsius;
	}

	public void setTemperatureLowCelsius(Float temperatureLowCelcius) {
		this.temperatureLowCelsius = temperatureLowCelcius;
	}

	public String getSkyConditions() {
		return skyConditions;
	}

	public void setSkyConditions(String skyConditions) {
		this.skyConditions = skyConditions;
	}

	public SkyCondition getCondition() {
		return condition;
	}

	public void setCondition(SkyCondition condition) {
		this.condition = condition;
	}

	@Deprecated
	@JsonIgnore
	@SerializeIgnore
	public Float getTemperatureStartCelcius() {
		return temperatureStartCelsius;
	}

	public Float getTemperatureStartCelsius() {
		return temperatureStartCelsius;
	}

	public void setTemperatureStartCelsius(Float temperatureStartCelcius) {
		this.temperatureStartCelsius = temperatureStartCelcius;
	}

	@Deprecated
	@JsonIgnore
	@SerializeIgnore
	public Float getTemperatureEndCelcius() {
		return temperatureEndCelsius;
	}

	public Float getTemperatureEndCelsius() {
		return temperatureEndCelsius;
	}

	public void setTemperatureEndCelsius(Float temperatureEndCelcius) {
		this.temperatureEndCelsius = temperatureEndCelcius;
	}

}
