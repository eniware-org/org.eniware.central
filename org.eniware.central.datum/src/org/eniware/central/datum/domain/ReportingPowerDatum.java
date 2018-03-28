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
 * Extension of {@link PowerDatum} with some additional properties
 * geared towards reporting.
 *
 * @version $Revision$ $Date$
 */
public class ReportingPowerDatum extends PowerDatum implements WattHourDatum, ReportingDatum {

	private static final long serialVersionUID = 1172177394787945782L;

	private Double wattHours;
	private Double cost;
	private String currency;
	private LocalDateTime localDateTime;
	
	@Override
	public String toString() {
		return "ReportingPowerDatum{nodeId=" +getNodeId()
			+",watts=" +getWatts()
			+",batVolts=" +getBatteryVolts()
			+",wattHours=" +getWattHours()
			+",cost=" +getCost()
			+",currency=" +getCurrency()
			+'}';
	}

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
	 * @return the wattHours
	 */
	public Double getWattHours() {
		return wattHours;
	}
	
	/**
	 * @param wattHours the wattHours to set
	 */
	public void setWattHours(Double wattHours) {
		this.wattHours = wattHours;
	}
	
	/**
	 * @return the cost
	 */
	public Double getCost() {
		return cost;
	}
	
	/**
	 * @param cost the cost to set
	 */
	public void setCost(Double cost) {
		this.cost = cost;
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

}
