/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Domain object for weather related data.
 * 
 * <p>
 * Note a {@code WeatherDatum} is not directly related to a {@code EniwareEdge},
 * and the {@code EdgeId} value may actually be <em>null</em>. This class
 * implements both {@link EdgeDatum} and {@link LocationDatum} for ease of use,
 * although strictly speaking it is only a {@link LocationDatum}.
 * </p>
 *
 * @version 1.1
 */
public class WeatherDatum extends BaseEdgeDatum implements LocationDatum {

	private static final long serialVersionUID = 5090759003465856008L;

	private Long locationId;
	private DateTime infoDate; // date weather info current as
	private String skyConditions;
	private Float temperatureCelsius;
	private Float humidity;
	private Float barometricPressure;
	private String barometerDelta;
	private Float visibility;
	private Integer uvIndex;
	private Float dewPoint;
	private SkyCondition condition;

	/**
	 * Default constructor.
	 */
	public WeatherDatum() {
		super();
	}

	@Override
	public String toString() {
		return "WeatherDatum{EdgeId=" + getEdgeId() + ",locationId=" + this.locationId + ",infoDate="
				+ this.infoDate + ",temp=" + this.temperatureCelsius + ",humidity=" + this.humidity
				+ ",barometricPressure=" + this.barometricPressure + ",barometerDelta="
				+ this.barometerDelta + '}';
	}

	/**
	 * Test if another WeatherDatum's {@code infoDate} value is the same as this
	 * instance's {@code infoDate} value.
	 * 
	 * <p>
	 * Only non-null values are compared, so if both {@code infoDate} values are
	 * <em>null</em> this method will return <em>false</em>.
	 * </p>
	 * 
	 * @param other
	 *        the WeatherDatum to compare to
	 * @return <em>true</em> if both {@code infoDate} values are equal
	 */
	public boolean isSameInfoDate(WeatherDatum other) {
		return this.infoDate != null && other != null && other.infoDate != null
				&& this.infoDate.isEqual(other.infoDate);
	}

	@Override
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getSkyConditions() {
		return skyConditions;
	}

	public void setSkyConditions(String skyConditions) {
		this.skyConditions = skyConditions;
	}

	@Deprecated
	@JsonIgnore
	@SerializeIgnore
	public Float getTemperatureCelcius() {
		return getTemperatureCelsius();
	}

	@Deprecated
	public void setTemperatureCelcius(Float temperatureCelcius) {
		setTemperatureCelsius(temperatureCelcius);
	}

	public Float getTemperatureCelsius() {
		return temperatureCelsius;
	}

	public void setTemperatureCelsius(Float temperatureCelcius) {
		this.temperatureCelsius = temperatureCelcius;
	}

	public Float getHumidity() {
		return humidity;
	}

	public void setHumidity(Float humidity) {
		this.humidity = humidity;
	}

	public Float getBarometricPressure() {
		return barometricPressure;
	}

	public void setBarometricPressure(Float barometricPressure) {
		this.barometricPressure = barometricPressure;
	}

	public String getBarometerDelta() {
		return barometerDelta;
	}

	public void setBarometerDelta(String barometerDelta) {
		this.barometerDelta = barometerDelta;
	}

	public Float getVisibility() {
		return visibility;
	}

	public void setVisibility(Float visibility) {
		this.visibility = visibility;
	}

	public Integer getUvIndex() {
		return uvIndex;
	}

	public void setUvIndex(Integer uvIndex) {
		this.uvIndex = uvIndex;
	}

	public Float getDewPoint() {
		return dewPoint;
	}

	public void setDewPoint(Float dewPoint) {
		this.dewPoint = dewPoint;
	}

	public DateTime getInfoDate() {
		return infoDate;
	}

	public void setInfoDate(DateTime infoDate) {
		this.infoDate = infoDate;
	}

	public SkyCondition getCondition() {
		return condition;
	}

	public void setCondition(SkyCondition condition) {
		this.condition = condition;
	}

}
