/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Basic reporting-level Datum API.
 *
 * @version $Revision$ $Date$
 */
public interface ReportingDatum {

	/**
	 * Get a "local" date for this datum, local to the Edge or location
	 * the datum is associated with.
	 * 
	 * @return local date
	 */
	public LocalDate getLocalDate();
	
	/**
	 * Get a "local" time for this datum, local to the Edge or location
	 * the datum is associated with.
	 * 
	 * @return local time
	 */
	public LocalTime getLocalTime();
	
}
