/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Extension of {@link DayDatum} with some additional properties
 * geared towards reporting.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
public class ReportingDayDatum extends DayDatum implements ReportingDatum {

	private static final long serialVersionUID = 8461923146681980795L;

	private String timeZoneId = TimeZone.getDefault().getID();
	private Float latitude;
	private Float longitude;

	/* (non-Javadoc)
	 * @see net.sf.solarnetwork.central.domain.ReportingDatum#getLocalDate()
	 */
	public LocalDate getLocalDate() {
		if ( super.getCreated() == null ) {
			return null;
		}
		return super.getCreated().toLocalDate();
	}

	@Override
	public DateTime getCreated() {
		return null; // force localDate
	}

	/* (non-Javadoc)
	 * @see net.sf.solarnetwork.central.domain.ReportingDatum#getLocalTime()
	 */
	public LocalTime getLocalTime() {
		return null;
	}

	/**
	 * @return the latitude
	 */
	public Float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the timeZoneId
	 */
	public String getTimeZoneId() {
		return timeZoneId;
	}

	/**
	 * @param timeZoneId the timeZoneId to set
	 */
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

}
