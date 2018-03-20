/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.central.domain.Filter;
import org.joda.time.DateTime;

/**
 * Filter for Datum entities.
 * 
 * @author matt
 * @version 1.1
 */
public interface DatumFilter extends Filter {

	/**
	 * Get a type of datum.
	 * 
	 * @return the datum type
	 */
	String getType();

	/**
	 * Get a start date.
	 * 
	 * @return the start date
	 */
	public DateTime getStartDate();

	/**
	 * Get an end date.
	 * 
	 * @return the end date
	 */
	public DateTime getEndDate();

}
