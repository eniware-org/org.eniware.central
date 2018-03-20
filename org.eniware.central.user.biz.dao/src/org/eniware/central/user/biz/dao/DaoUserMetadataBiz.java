/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.biz.dao;

import java.util.List;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.user.biz.UserMetadataBiz;
import org.eniware.central.user.dao.UserMetadataDao;
import org.eniware.central.user.domain.UserMetadataEntity;
import org.eniware.central.user.domain.UserMetadataFilter;
import org.eniware.central.user.domain.UserMetadataFilterMatch;
import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO-based implementation of {@link UserMetadataBiz}.
 * 
 * @author matt
 * @version 1.0
 */
public class DaoUserMetadataBiz implements UserMetadataBiz {

	private final UserMetadataDao userMetadataDao;

	/**
	 * Constructor.
	 * 
	 * @param userMetadataDao
	 *        the DAO to use
	 */
	public DaoUserMetadataBiz(UserMetadataDao userMetadataDao) {
		super();
		this.userMetadataDao = userMetadataDao;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addUserMetadata(Long userId, GeneralDatumMetadata meta) {
		assert userId != null;
		assert meta != null;
		UserMetadataEntity um = userMetadataDao.get(userId);
		GeneralDatumMetadata newMeta = meta;
		if ( um == null ) {
			um = new UserMetadataEntity();
			um.setCreated(new DateTime());
			um.setId(userId);
			newMeta = meta;
		} else if ( um.getMeta() != null && um.getMeta().equals(meta) == false ) {
			newMeta = new GeneralDatumMetadata(um.getMeta());
			newMeta.merge(meta, true);
		}
		if ( newMeta != null && newMeta.equals(um.getMeta()) == false ) {
			// have changes, so persist
			um.setMeta(newMeta);
			userMetadataDao.store(um);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void storeUserMetadata(Long userId, GeneralDatumMetadata meta) {
		assert userId != null;
		assert meta != null;
		UserMetadataEntity um = userMetadataDao.get(userId);
		if ( um == null ) {
			um = new UserMetadataEntity();
			um.setCreated(new DateTime());
			um.setId(userId);
			um.setMeta(meta);
		} else {
			um.setMeta(meta);
		}
		userMetadataDao.store(um);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void removeUserMetadata(Long userId) {
		UserMetadataEntity meta = userMetadataDao.get(userId);
		if ( meta != null ) {
			userMetadataDao.delete(meta);
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public FilterResults<UserMetadataFilterMatch> findUserMetadata(UserMetadataFilter criteria,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max) {
		return userMetadataDao.findFiltered(criteria, sortDescriptors, offset, max);
	}

}
