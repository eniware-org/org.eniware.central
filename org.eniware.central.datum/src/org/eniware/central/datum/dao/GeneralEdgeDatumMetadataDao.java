/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.dao;

import java.util.Set;

import org.eniware.central.dao.FilterableDao;
import org.eniware.central.dao.GenericDao;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadata;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.EdgeSourcePK;

/**
 * DAO API for {@link GeneralEdgeDatumMetadata}.
 
 * @version 1.1
 */
public interface GeneralEdgeDatumMetadataDao extends GenericDao<GeneralEdgeDatumMetadata, EdgeSourcePK>,
		FilterableDao<GeneralEdgeDatumMetadataFilterMatch, EdgeSourcePK, GeneralEdgeDatumMetadataFilter> {

	/**
	 * Get all available Edge + source ID combinations for a given set of Edge
	 * IDs matching a metadata search filter.
	 * 
	 * The metadata filter must be expressed in LDAP search filter style, using
	 * JSON pointer style paths for keys, for example {@code (/m/foo=bar)},
	 * {@code (t=foo)}, or {@code (&(&#47;**&#47;foo=bar)(t=special))}.
	 * 
	 * @param EdgeIds
	 *        the Edge IDs to search for
	 * @param metadataFilter
	 *        A metadata search filter, in LDAP search filter syntax.
	 * @return the distinct Edge ID and source IDs combinations that match the
	 *         given filter (never <em>null</em>)
	 * @since 1.1
	 */
	Set<EdgeSourcePK> getFilteredSources(Long[] EdgeIds, String metadataFilter);

}
