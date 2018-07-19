/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.aop;

import java.util.Set;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.dao.UserAuthTokenDao;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.domain.UserAuthToken;
import org.eniware.central.user.domain.UserAuthTokenType;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.support.AuthorizationSupport;

/**
 * Security enforcing AOP aspect for {@link UserBiz}.
 * 
 * @version 1.1
 */
@Aspect
public class UserSecurityAspect extends AuthorizationSupport {

	private final UserAuthTokenDao userAuthTokenDao;

	/**
	 * Constructor.
	 * 
	 * @param userEdgeDao
	 *        the UserEdgeDao
	 * @param userAuthTokenDao
	 *        the UserAuthTokenDao
	 */
	public UserSecurityAspect(UserEdgeDao userEdgeDao, UserAuthTokenDao userAuthTokenDao) {
		super(userEdgeDao);
		this.userAuthTokenDao = userAuthTokenDao;
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.getUser*(..)) && args(userId,..)")
	public void readUser(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.getPendingUserEdgeConfirmations(..)) && args(userId,..)")
	public void readUserEdgeConfirmations(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.getAllUserAuthTokens(..)) && args(userId,..)")
	public void readerUserAuthTokens(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.getUserEdge(..)) && args(userId,EdgeId)")
	public void readUserEdge(Long userId, Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.getArchivedUserEdges(..)) && args(userId)")
	public void readArchivedUserEdges(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.updateUserEdgeArchivedStatus(..)) && args(userId,EdgeIds,..)")
	public void updateArchivedUserEdges(Long userId, Long[] EdgeIds) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.generateUserAuthToken(..)) && args(userId,type,EdgeIds)")
	public void generateAuthToken(Long userId, UserAuthTokenType type, Set<Long> EdgeIds) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.deleteUserAuthToken(..)) && args(userId,token)")
	public void deleteAuthToken(Long userId, String token) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.updateUserAuthToken*(..)) && args(userId,token,..)")
	public void updateAuthToken(Long userId, String token) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.createAuthorizationV2Builder(..)) && args(userId,token,..)")
	public void createAuthorizationV2Builder(Long userId, String token) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*UserBiz.saveUserEdge(..)) && args(entry)")
	public void updateUserEdge(UserEdge entry) {
	}

	@Before("readUser(userId) || readUserEdgeConfirmations(userId) || readerUserAuthTokens(userId) || readArchivedUserEdges(userId)")
	public void userReadAccessCheck(Long userId) {
		requireUserReadAccess(userId);
	}

	@Before("readUserEdge(userId, EdgeId)")
	public void userEdgeReadAccessCheck(Long userId, Long EdgeId) {
		// the userReadAccessCheck method will also be called
		requireEdgeWriteAccess(EdgeId);
	}

	@Before("generateAuthToken(userId, type, EdgeIds)")
	public void userReadAccessCheck(Long userId, UserAuthTokenType type, Set<Long> EdgeIds) {
		requireUserWriteAccess(userId);
		if ( EdgeIds != null ) {
			for ( Long EdgeId : EdgeIds ) {
				requireEdgeWriteAccess(EdgeId);
			}
		}
	}

	@Before("deleteAuthToken(userId, tokenId) || updateAuthToken(userId, tokenId) || createAuthorizationV2Builder(userId, tokenId)")
	public void updateAuthTokenAccessCheck(Long userId, String tokenId) {
		requireUserWriteAccess(userId);
		UserAuthToken token = userAuthTokenDao.get(tokenId);
		if ( token == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, tokenId);
		}
		if ( userId.equals(token.getUserId()) == false ) {
			log.warn("Access DENIED to user {} for token {}; wrong user", userId, tokenId);
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, tokenId);
		}
	}

	@Before("updateUserEdge(entry)")
	public void updateUserEdgeAccessCheck(UserEdge entry) {
		if ( entry.getUser() == null ) {
			log.warn("Access DENIED to user Edge; no user ID");
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		requireUserWriteAccess(entry.getUser().getId());
		if ( entry.getEdge() == null ) {
			log.warn("Access DENIED to user Edge; no Edge ID");
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		requireEdgeWriteAccess(entry.getEdge().getId());
	}

	@Before("updateArchivedUserEdges(userId, EdgeIds)")
	public void updateArchivedUserEdgesAccessCheck(Long userId, Long[] EdgeIds) {
		if ( userId == null ) {
			log.warn("Access DENIED to user Edge; no user ID");
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		if ( EdgeIds == null || EdgeIds.length < 1 ) {
			log.warn("Access DENIED to user Edges; no Edge IDs");
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		requireUserWriteAccess(userId);
		for ( Long EdgeId : EdgeIds ) {
			requireEdgeWriteAccess(EdgeId);
		}
	}

}
