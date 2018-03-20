/* ==================================================================
*  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import org.joda.time.DateTime;

/**
 * Base domain object API.
 * 
 * @author matt
 * @version $Id$
 */
public interface Entity<PK> extends Identity<PK> {

	/**
	 * Get the date this datum was created.
	 * 
	 * @return the created date
	 */
	public DateTime getCreated();

}
