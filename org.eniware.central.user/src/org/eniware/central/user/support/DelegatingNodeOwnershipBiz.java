/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.support;

import java.util.List;

import org.eniware.central.security.AuthorizationException;
import org.eniware.central.user.biz.NodeOwnershipBiz;
import org.eniware.central.user.domain.UserNodeTransfer;

/**
 * Delegating implementation of {@link NodeOwnershipBiz}, mostly to help with
 * AOP.
 * 
 * @version 1.0
 */
public class DelegatingNodeOwnershipBiz implements NodeOwnershipBiz {

	private final NodeOwnershipBiz delegate;

	/**
	 * Construct with a delegate.
	 * 
	 * @param delegate
	 *        The delegate.
	 */
	public DelegatingNodeOwnershipBiz(NodeOwnershipBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public UserNodeTransfer getNodeOwnershipTransfer(Long userId, Long nodeId) {
		return delegate.getNodeOwnershipTransfer(userId, nodeId);
	}

	@Override
	public List<UserNodeTransfer> pendingNodeOwnershipTransfersForEmail(String email) {
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
	public UserNodeTransfer confirmNodeOwnershipTransfer(Long userId, Long nodeId, boolean accept)
			throws AuthorizationException {
		return delegate.confirmNodeOwnershipTransfer(userId, nodeId, accept);
	}

}
