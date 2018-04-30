/* ==================================================================
*  Eniware Open sorce:Nikolai Manchev
*  Apache License 2.0
* ==================================================================
*/

package org.eniware.central.dao;

import org.eniware.central.domain.EniwareEdgeMetadata;
import org.eniware.central.domain.EniwareEdgeMetadataFilter;
import org.eniware.central.domain.EniwareEdgeMetadataFilterMatch;

/**
 * DAO API for {@link EniwareEdgeMetadata}.
 * @version 1.0
 * @since 1.32
 */
public interface EniwareEdgeMetadataDao extends GenericDao<EniwareEdgeMetadata, Long>,
		FilterableDao<EniwareEdgeMetadataFilterMatch, Long, EniwareEdgeMetadataFilter> {

}
