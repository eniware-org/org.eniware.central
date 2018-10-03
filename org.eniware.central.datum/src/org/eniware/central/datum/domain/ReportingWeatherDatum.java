/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

/**
 * Extension of {@link WeatherDatum} with some additional properties
 * geared towards reporting.
 *
 * @version $Revision$ $Date$
 */
public class ReportingWeatherDatum extends WeatherDatum implements ReportingDatum {

	private static final long serialVersionUID = 440192236630501003L;

	private LocalDateTime localDateTime;
	
	/* (non-Javadoc)
	 * @see net.sf.eniwarenetwork.central.domain.ReportingDatum#getLocalDate()
	 */
	public LocalDate getLocalDate() {
		if ( localDateTime == null ) {
			return null;
		}
		return localDateTime.toLocalDate();
	}

	/* (non-Javadoc)
	 * @see net.sf.eniwarenetwork.central.domain.ReportingDatum#getLocalTime()
	 */
	public LocalTime getLocalTime() {
		if ( localDateTime == null ) {
			return null;
		}
		return localDateTime.toLocalTime();
	}
	
	/**
	 * @param localDateTime the localDateTime to set
	 */
	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

}
