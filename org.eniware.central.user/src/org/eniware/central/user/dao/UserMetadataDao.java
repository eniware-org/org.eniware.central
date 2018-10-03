/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import org.eniware.central.dao.FilterableDao;
import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.UserMetadataEntity;
import org.eniware.central.user.domain.UserMetadataFilter;
import org.eniware.central.user.domain.UserMetadataFilterMatch;

/**
 * DAO API for {@link UserMetadataEntity}.
 * 
 * @version 1.0
 * @since 1.23
 */
public interface UserMetadataDao extends GenericDao<UserMetadataEntity, Long>,
		FilterableDao<UserMetadataFilterMatch, Long, UserMetadataFilter> {

}
