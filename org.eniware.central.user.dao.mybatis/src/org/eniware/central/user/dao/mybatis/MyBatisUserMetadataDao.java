/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao.mybatis;

import org.eniware.central.dao.mybatis.support.BaseMyBatisFilterableDao;
import org.eniware.central.user.dao.UserMetadataDao;
import org.eniware.central.user.domain.UserMetadataEntity;
import org.eniware.central.user.domain.UserMetadataFilter;
import org.eniware.central.user.domain.UserMetadataFilterMatch;
import org.eniware.central.user.domain.UserMetadataMatch;

/**
 * MyBatis implementation of {@link UserMetadataDao}.
 * 
 * @author matt
 * @version 1.1
 * @since 1.8
 */
public class MyBatisUserMetadataDao extends
		BaseMyBatisFilterableDao<UserMetadataEntity, UserMetadataFilterMatch, UserMetadataFilter, Long>
		implements UserMetadataDao {

	/**
	 * The query parameter for a general {@link Filter} object value.
	 * 
	 * @deprecated use {@link BaseMyBatisFilterableDao#FILTER_PROPERTY}
	 */
	@Deprecated
	public static final String PARAM_FILTER = BaseMyBatisFilterableDao.FILTER_PROPERTY;

	/**
	 * Default constructor.
	 */
	public MyBatisUserMetadataDao() {
		super(UserMetadataEntity.class, Long.class, UserMetadataMatch.class);
	}

}
