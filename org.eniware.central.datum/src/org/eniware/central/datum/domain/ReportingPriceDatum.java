/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

/**
 * Extension of {@link PriceDatum} with some additional properties
 * geared towards reporting.
 *
 * @version $Revision$ $Date$
 */
public class ReportingPriceDatum extends PriceDatum implements ReportingDatum {

	private static final long serialVersionUID = 2705678590330735224L;

	private LocalDateTime localDateTime;
	private String currency = null;
	private String unit = null;
	
	/* (non-Javadoc)
	 * @see net.sf.solarnetwork.central.domain.ReportingDatum#getLocalDate()
	 */
	public LocalDate getLocalDate() {
		if ( localDateTime == null ) {
			return null;
		}
		return localDateTime.toLocalDate();
	}

	/* (non-Javadoc)
	 * @see net.sf.solarnetwork.central.domain.ReportingDatum#getLocalTime()
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

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
