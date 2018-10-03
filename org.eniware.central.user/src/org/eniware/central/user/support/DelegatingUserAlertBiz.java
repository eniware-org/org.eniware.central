/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.support;

import java.util.List;

import org.eniware.central.user.biz.UserAlertBiz;
import org.eniware.central.user.domain.UserAlert;
import org.eniware.central.user.domain.UserAlertSituationStatus;

/**
 * Delegating implementation of {@link UserAlertBiz}, mostly to help with AOP.
 * 
 * @version 1.1
 */
public class DelegatingUserAlertBiz implements UserAlertBiz {

	private final UserAlertBiz delegate;

	/**
	 * Construct with a delegate.
	 * 
	 * @param delegate
	 *        The delegate to use.
	 */
	public DelegatingUserAlertBiz(UserAlertBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public List<UserAlert> userAlertsForUser(Long userId) {
		return delegate.userAlertsForUser(userId);
	}

	@Override
	public Long saveAlert(UserAlert alert) {
		return delegate.saveAlert(alert);
	}

	@Override
	public UserAlert alertSituation(Long alertId) {
		return delegate.alertSituation(alertId);
	}

	@Override
	public int alertSituationCountForUser(Long userId) {
		return delegate.alertSituationCountForUser(userId);
	}

	@Override
	public UserAlert updateSituationStatus(Long alertId, UserAlertSituationStatus status) {
		return delegate.updateSituationStatus(alertId, status);
	}

	@Override
	public List<UserAlert> alertSituationsForUser(Long userId) {
		return delegate.alertSituationsForUser(userId);
	}

	@Override
	public List<UserAlert> alertSituationsForEdge(Long EdgeId) {
		return delegate.alertSituationsForEdge(EdgeId);
	}

	@Override
	public void deleteAlert(Long alertId) {
		delegate.deleteAlert(alertId);
	}

}
