/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.user.biz.NodeOwnershipBiz;
import org.eniware.central.user.dao.UserDao;
import org.eniware.central.user.dao.UserNodeDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserNodePK;
import org.eniware.central.user.domain.UserNodeTransfer;
import org.eniware.central.user.support.AuthorizationSupport;

/**
 * Security enforcing AOP aspect for {@link NodeOwnershipBiz}.
 * 
 * @version 1.0
 */
@Aspect
public class NodeOwnershipSecurityAspect extends AuthorizationSupport {

	private final UserDao userDao;

	/**
	 * Constructor.
	 * 
	 * @param userNodeDao
	 *        The {@link UserNodeDao} to use.
	 */
	public NodeOwnershipSecurityAspect(UserNodeDao userNodeDao, UserDao userDao) {
		super(userNodeDao);
		this.userDao = userDao;
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*NodeOwnershipBiz.pending*(..)) && args(email)")
	public void pendingRequestsForEmail(String email) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*NodeOwnershipBiz.requestNodeOwnershipTransfer(..)) && args(userId,nodeId,..)")
	public void requestTransfer(Long userId, Long nodeId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*NodeOwnershipBiz.getNodeOwnershipTransfer(..)) && args(userId,nodeId)")
	public void getTransfer(Long userId, Long nodeId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*NodeOwnershipBiz.cancel*(..)) && args(userId,nodeId,..)")
	public void cancelTransferRequest(Long userId, Long nodeId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*NodeOwnershipBiz.confirm*(..)) && args(userId,nodeId,..)")
	public void confirmTransferRequest(Long userId, Long nodeId) {
	}

	@Before("pendingRequestsForEmail(email)")
	public void checkPendingRequestsForEmail(String email) {
		User recipient = userDao.getUserByEmail(email);
		if ( recipient == null ) {
			log.warn("Access DENIED to transfer recipient {}; not found", email);
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, email);
		}
		requireUserReadAccess(recipient.getId());
	}

	@Before("requestTransfer(userId, nodeId) || getTransfer(userId, nodeId) || cancelTransferRequest(userId, nodeId)")
	public void checkUserNodeRequestOrCancelTransferRequest(Long userId, Long nodeId) {
		// the active user must have write-access to the given node
		requireNodeWriteAccess(nodeId);
	}

	@Before("confirmTransferRequest(userId, nodeId)")
	public void checkUserNodeConfirmTransferAccess(Long userId, Long nodeId) {
		// the active user must be the recipient of the transfer request
		final UserNodePK userNodePK = new UserNodePK(userId, nodeId);
		UserNodeTransfer xfer = getUserNodeDao().getUserNodeTransfer(userNodePK);
		if ( xfer == null ) {
			log.warn("Access DENIED to transfer {}; not found", userNodePK);
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, userNodePK);
		}
		User recipient = userDao.getUserByEmail(xfer.getEmail());
		if ( recipient == null ) {
			log.warn("Access DENIED to transfer recipient {}; not found", xfer.getEmail());
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, nodeId);
		}
		requireUserWriteAccess(recipient.getId());
	}
}
