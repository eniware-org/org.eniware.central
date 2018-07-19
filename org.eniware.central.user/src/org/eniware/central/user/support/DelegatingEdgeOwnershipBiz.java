/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
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
	public UserEdgeTransfer getNodeOwnershipTransfer(Long userId, Long nodeId) {
		return delegate.getNodeOwnershipTransfer(userId, nodeId);
	}

	@Override
	public List<UserEdgeTransfer> pendingNodeOwnershipTransfersForEmail(String email) {
		return delegate.pendingNodeOwnershipTransfersForEmail(email);
	}

	@Override
	public void requestNodeOwnershipTransfer(Long userId, Long nodeId, String newOwnerEmail)
			throws AuthorizationException {
		delegate.requestNodeOwnershipTransfer(userId, nodeId, newOwnerEmail);
	}

	@Override
	public void cancelNodeOwnershipTransfer(Long userId, Long nodeId) throws AuthorizationException {
		delegate.cancelNodeOwnershipTransfer(userId, nodeId);
	}

	@Override
	public UserEdgeTransfer confirmNodeOwnershipTransfer(Long userId, Long nodeId, boolean accept)
			throws AuthorizationException {
		return delegate.confirmNodeOwnershipTransfer(userId, nodeId, accept);
	}

}
