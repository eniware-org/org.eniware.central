/* ==================================================================
*  Eniware Open sorce:Nikolai Manchev
*  Apache License 2.0
* ==================================================================
*/

package org.eniware.central.dao;

import org.eniware.central.domain.SolarNodeMetadata;
import org.eniware.central.domain.SolarNodeMetadataFilter;
import org.eniware.central.domain.SolarNodeMetadataFilterMatch;

/**
 * DAO API for {@link SolarNodeMetadata}.
 * 
 * @author matt
 * @version 1.0
 * @since 1.32
 */
public interface SolarNodeMetadataDao extends GenericDao<SolarNodeMetadata, Long>,
		FilterableDao<SolarNodeMetadataFilterMatch, Long, SolarNodeMetadataFilter> {

}
