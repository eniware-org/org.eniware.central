/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.dao;

import java.util.Set;

import org.eniware.central.dao.FilterableDao;
import org.eniware.central.dao.GenericDao;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadata;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.NodeSourcePK;

/**
 * DAO API for {@link GeneralNodeDatumMetadata}.
 * 
 * @author matt
 * @version 1.1
 */
public interface GeneralNodeDatumMetadataDao extends GenericDao<GeneralNodeDatumMetadata, NodeSourcePK>,
		FilterableDao<GeneralNodeDatumMetadataFilterMatch, NodeSourcePK, GeneralNodeDatumMetadataFilter> {

	/**
	 * Get all available node + source ID combinations for a given set of node
	 * IDs matching a metadata search filter.
	 * 
	 * The metadata filter must be expressed in LDAP search filter style, using
	 * JSON pointer style paths for keys, for example {@code (/m/foo=bar)},
	 * {@code (t=foo)}, or {@code (&(&#47;**&#47;foo=bar)(t=special))}.
	 * 
	 * @param nodeIds
	 *        the node IDs to search for
	 * @param metadataFilter
	 *        A metadata search filter, in LDAP search filter syntax.
	 * @return the distinct node ID and source IDs combinations that match the
	 *         given filter (never <em>null</em>)
	 * @since 1.1
	 */
	Set<NodeSourcePK> getFilteredSources(Long[] nodeIds, String metadataFilter);

}
