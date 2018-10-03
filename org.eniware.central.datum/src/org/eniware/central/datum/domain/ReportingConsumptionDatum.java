/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.central.domain.EntityMatch;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

/**
 * Extension of {@link ConsumptionDatum} with some additional properties geared
 * towards reporting.
 *
 * @version 1.1
 */
public class ReportingConsumptionDatum extends ConsumptionDatum implements WattHourDatum,
		ReportingDatum, EntityMatch {

	private static final long serialVersionUID = -6376812878462350574L;

	private Double wattHours;
	private Double cost;
	private String currency;
	private LocalDateTime localDateTime;

	@Override
	public String toString() {
		return "ReportingConsumptionDatum{sourceId=" + getSourceId() + ",watts=" + getWatts()
				+ ",wattHours=" + getWattHours() + ",cost=" + getCost() + ",currency=" + getCurrency()
				+ '}';
	}

	@Override
	public LocalDate getLocalDate() {
		if ( localDateTime == null ) {
			return null;
		}
		return localDateTime.toLocalDate();
	}

	@Override
	public LocalTime getLocalTime() {
		if ( localDateTime == null ) {
			return null;
		}
		return localDateTime.toLocalTime();
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	@Override
	public Double getWattHours() {
		return wattHours;
	}

	public void setWattHours(Double wattHours) {
		this.wattHours = wattHours;
	}

	@Override
	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	@Override
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
