/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;


/**
 * Domain object for a unit of data collected from a power consumption monitor.
 
 * @version $Revision$ $Date$
 */
public class ConsumptionDatum extends BaseEdgeDatum implements LocationDatum {

	private static final long serialVersionUID = 714989418475390919L;

	private Long locationId = null;
	private Integer watts = null;
	private Long wattHourReading = null;

	private Float amps = null;
	private Float volts = null;

	/**
	 * Default constructor.
	 */
	public ConsumptionDatum() {
		super();
	}
	
	@Override
	public String toString() {
		return "ConsumptionDatum{EdgeId=" +getEdgeId()
			+",sourceId=" +getSourceId()
			+",watts=" +getWatts()
			+",wattHourReading=" +this.wattHourReading
			+'}';
	}
	
	/**
	 * Get the watts.
	 * 
	 * <p>This will return the {@code watts} value if available, or
	 * fall back to {@code amps} * {@code volts}.<?p>
	 * 
	 * @return watts, or <em>null</em> if watts not available and 
	 * either amps or volts are null
	 */
	public Integer getWatts() {
		if ( watts != null ) {
			return watts;
		}
		if ( amps == null || volts == null ) {
			return null;
		}
		return Integer.valueOf((int)Math.round(
				amps.doubleValue() * volts.doubleValue()));
	}

	// for backwards-compatibility only
	public void setAmps(Float amps) {
		this.amps = amps;
	}
	
	// for backwards-compatibility only
	public void setVolts(Float volts) {
		this.volts = volts;
	}
	
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	public Long getWattHourReading() {
		return wattHourReading;
	}
	public void setWattHourReading(Long wattHourReading) {
		this.wattHourReading = wattHourReading;
	}
	public void setWatts(Integer watts) {
		this.watts = watts;
	}
	
}
