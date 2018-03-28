/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.EntityMatch;
import org.eniware.central.domain.PriceSource;
import org.eniware.central.domain.SourceLocation;

/**
 * DAO API for PriceSource.
 * @version $Revision$
 */
public interface PriceSourceDao extends GenericDao<PriceSource, Long>,
FilterableDao<EntityMatch, Long, SourceLocation> {

	/**
	 * Find a unique PriceSource for a given name.
	 * 
	 * @param name the PriceSource name
	 * @return the PriceSource, or <em>null</em> if not found
	 */
	PriceSource getPriceSourceForName(String sourceName);

}
