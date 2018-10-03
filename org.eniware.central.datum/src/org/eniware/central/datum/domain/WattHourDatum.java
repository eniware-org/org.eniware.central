/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * EdgeDatum API for watt-hour related data.
 *
 * @version $Revision$ $Date$
 */
public interface WattHourDatum {

	/**
	 * Get the watt-hour value.
	 * 
	 * @return the wattHours
	 */
	public Double getWattHours();
	
	/**
	 * Get a cost for the watt hours.
	 * 
	 * @return the cost
	 */
	public Double getCost();
	
	/**
	 * Get the currency of the cost for the watt hours.
	 * 
	 * @return the currency
	 */
	public String getCurrency();

}
