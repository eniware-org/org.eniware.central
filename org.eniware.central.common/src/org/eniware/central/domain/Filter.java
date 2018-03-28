/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 * $Revision$
 * ==================================================================
 */

package org.eniware.central.domain;

import java.util.Map;

/**
 * API for a "filter-able" object, that is something that can be searched
 * for by specifying filter keys and associated match values.
 * @version $Revision$
 */
public interface Filter {

	/**
	 * Get a mapping of filter keys and associated filter values.
	 * 
	 * @return a filter map
	 */
	Map<String, ?> getFilter();
	
}
