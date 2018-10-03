/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.user.biz.EdgeOwnershipBiz;
import org.eniware.central.user.dao.UserDao;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserEdgePK;
import org.eniware.central.user.domain.UserEdgeTransfer;
import org.eniware.central.user.support.AuthorizationSupport;

/**
 * Security enforcing AOP aspect for {@link EdgeOwnershipBiz}.
 * 
 * @version 1.0
 */
@Aspect
public class EdgeOwnershipSecurityAspect extends AuthorizationSupport {

	private final UserDao userDao;

	/**
	 * Constructor.
	 * 
	 * @param userEdgeDao
	 *        The {@link UserEdgeDao} to use.
	 */
	public EdgeOwnershipSecurityAspect(UserEdgeDao userEdgeDao, UserDao userDao) {
		super(userEdgeDao);
		this.userDao = userDao;
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*EdgeOwnershipBiz.pending*(..)) && args(email)")
	public void pendingRequestsForEmail(String email) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*EdgeOwnershipBiz.requestEdgeOwnershipTransfer(..)) && args(userId,EdgeId,..)")
	public void requestTransfer(Long userId, Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*EdgeOwnershipBiz.getEdgeOwnershipTransfer(..)) && args(userId,EdgeId)")
	public void getTransfer(Long userId, Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*EdgeOwnershipBiz.cancel*(..)) && args(userId,EdgeId,..)")
	public void cancelTransferRequest(Long userId, Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*EdgeOwnershipBiz.confirm*(..)) && args(userId,EdgeId,..)")
	public void confirmTransferRequest(Long userId, Long EdgeId) {
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

	@Before("requestTransfer(userId, EdgeId) || getTransfer(userId, EdgeId) || cancelTransferRequest(userId, EdgeId)")
	public void checkUserEdgeRequestOrCancelTransferRequest(Long userId, Long EdgeId) {
		// the active user must have write-access to the given Edge
		requireEdgeWriteAccess(EdgeId);
	}

	@Before("confirmTransferRequest(userId, EdgeId)")
	public void checkUserEdgeConfirmTransferAccess(Long userId, Long EdgeId) {
		// the active user must be the recipient of the transfer request
		final UserEdgePK userEdgePK = new UserEdgePK(userId, EdgeId);
		UserEdgeTransfer xfer = getUserEdgeDao().getUserEdgeTransfer(userEdgePK);
		if ( xfer == null ) {
			log.warn("Access DENIED to transfer {}; not found", userEdgePK);
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, userEdgePK);
		}
		User recipient = userDao.getUserByEmail(xfer.getEmail());
		if ( recipient == null ) {
			log.warn("Access DENIED to transfer recipient {}; not found", xfer.getEmail());
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, EdgeId);
		}
		requireUserWriteAccess(recipient.getId());
	}
}
