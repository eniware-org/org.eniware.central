/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.support;

import java.util.List;

import org.eniware.central.security.AuthorizationException;
import org.eniware.central.user.biz.EdgeOwnershipBiz;
import org.eniware.central.user.domain.UserEdgeTransfer;

/**
 * Delegating implementation of {@link EdgeOwnershipBiz}, mostly to help with
 * AOP.
 * 
 * @version 1.0
 */
public class DelegatingEdgeOwnershipBiz implements EdgeOwnershipBiz {

	private final EdgeOwnershipBiz delegate;

	/**
	 * Construct with a delegate.
	 * 
	 * @param delegate
	 *        The delegate.
	 */
	public DelegatingEdgeOwnershipBiz(EdgeOwnershipBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public UserEdgeTransfer getEdgeOwnershipTransfer(Long userId, Long EdgeId) {
		return delegate.getEdgeOwnershipTransfer(userId, EdgeId);
	}

	@Override
	public List<UserEdgeTransfer> pendingEdgeOwnershipTransfersForEmail(String email) {
		return delegate.pendingEdgeOwnershipTransfersForEmail(email);
	}

	@Override
	public void requestEdgeOwnershipTransfer(Long userId, Long EdgeId, String newOwnerEmail)
			throws AuthorizationException {
		delegate.requestEdgeOwnershipTransfer(userId, EdgeId, newOwnerEmail);
	}

	@Override
	public void cancelEdgeOwnershipTransfer(Long userId, Long EdgeId) throws AuthorizationException {
		delegate.cancelEdgeOwnershipTransfer(userId, EdgeId);
	}

	@Override
	public UserEdgeTransfer confirmEdgeOwnershipTransfer(Long userId, Long EdgeId, boolean accept)
			throws AuthorizationException {
		return delegate.confirmEdgeOwnershipTransfer(userId, EdgeId, accept);
	}

}
