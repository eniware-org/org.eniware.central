/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.dao;

import java.util.Set;

import org.eniware.central.dao.FilterableDao;
import org.eniware.central.dao.GenericDao;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadata;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilter;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.datum.domain.LocationSourcePK;

/**
 * DAO API for {@link GeneralLocationDatumMetadata}.
 * 
 * @author matt
 * @version 1.1
 */
public interface GeneralLocationDatumMetadataDao
		extends GenericDao<GeneralLocationDatumMetadata, LocationSourcePK>,
		FilterableDao<GeneralLocationDatumMetadataFilterMatch, LocationSourcePK, GeneralLocationDatumMetadataFilter> {

	/**
	 * Get all available location + source ID combinations for a given set of
	 * location IDs matching a metadata search filter.
	 * 
	 * The metadata filter must be expressed in LDAP search filter style, using
	 * JSON pointer style paths for keys, for example {@code (/m/foo=bar)},
	 * {@code (t=foo)}, or {@code (&(&#47;**&#47;foo=bar)(t=special))}.
	 * 
	 * @param locationIds
	 *        the location IDs to search for
	 * @param metadataFilter
	 *        A metadata search filter, in LDAP search filter syntax.
	 * @return the distinct location ID and source IDs combinations that match
	 *         the given filter (never <em>null</em>)
	 * @since 1.1
	 */
	Set<LocationSourcePK> getFilteredSources(Long[] locationIds, String metadataFilter);

}
