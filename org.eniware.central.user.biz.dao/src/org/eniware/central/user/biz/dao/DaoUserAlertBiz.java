/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.biz.dao;

import java.util.List;

import org.eniware.central.user.biz.UserAlertBiz;
import org.eniware.central.user.dao.UserAlertDao;
import org.eniware.central.user.dao.UserAlertSituationDao;
import org.eniware.central.user.domain.UserAlert;
import org.eniware.central.user.domain.UserAlertSituationStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO-based implementation of {@link UserAlertBiz}.
 * 
 * @version 1.0
 */
public class DaoUserAlertBiz implements UserAlertBiz {

	private final UserAlertDao userAlertDao;
	private final UserAlertSituationDao userAlertSituationDao;

	public DaoUserAlertBiz(UserAlertDao userAlertDao, UserAlertSituationDao userAlertSituationDao) {
		super();
		this.userAlertDao = userAlertDao;
		this.userAlertSituationDao = userAlertSituationDao;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserAlert> userAlertsForUser(Long userId) {
		return userAlertDao.findAlertsForUser(userId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Long saveAlert(UserAlert alert) {
		return userAlertDao.store(alert);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserAlert alertSituation(Long alertId) {
		return userAlertDao.getAlertSituation(alertId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public int alertSituationCountForUser(Long userId) {
		return userAlertDao.alertSituationCountForUser(userId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserAlert updateSituationStatus(Long alertId, UserAlertSituationStatus status) {
		UserAlert alert = alertSituation(alertId);
		if ( alert != null && alert.getSituation() != null
				&& !alert.getSituation().getStatus().equals(status) ) {
			alert.getSituation().setStatus(status);
			userAlertSituationDao.store(alert.getSituation());
		}
		return alert;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserAlert> alertSituationsForUser(Long userId) {
		return userAlertDao.findActiveAlertSituationsForUser(userId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserAlert> alertSituationsForNode(Long nodeId) {
		return userAlertDao.findActiveAlertSituationsForNode(nodeId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteAlert(Long alertId) {
		UserAlert alert = new UserAlert();
		alert.setId(alertId);
		userAlertDao.delete(alert);
	}

}
