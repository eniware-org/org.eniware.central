/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityPolicy;
import org.eniware.central.security.SecurityPolicyMetadataType;
import org.eniware.central.user.biz.UserMetadataBiz;
import org.eniware.central.user.dao.UserNodeDao;
import org.eniware.central.user.domain.UserMetadataFilter;
import org.eniware.central.user.support.AuthorizationSupport;
import org.springframework.util.AntPathMatcher;

/**
 * Security enforcing AOP aspect for {@link UserMetadataBiz}.
 * 
 * @author matt
 * @version 1.0
 * @since 1.7
 */
@Aspect
public class UserMetadataSecurityAspect extends AuthorizationSupport {

	/**
	 * Constructor.
	 * 
	 * @param userNodeDao
	 *        the UserNodeDao to use
	 */
	public UserMetadataSecurityAspect(UserNodeDao userNodeDao) {
		super(userNodeDao);
		AntPathMatcher antMatch = new AntPathMatcher();
		antMatch.setCachePatterns(false);
		antMatch.setCaseSensitive(true);
		setPathMatcher(antMatch);
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.UserMetadata*.addUser*(..)) && args(userId,..)")
	public void addMetadata(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.UserMetadata*.storeUser*(..)) && args(userId,..)")
	public void storeMetadata(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.UserMetadata*.removeUser*(..)) && args(userId)")
	public void removeMetadata(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.UserMetadata*.findUser*(..)) && args(filter,..)")
	public void findMetadata(UserMetadataFilter filter) {
	}

	/**
	 * Check access to modifying user metadata.
	 * 
	 * @param userId
	 *        the ID of the user to verify
	 */
	@Before("addMetadata(userId) || storeMetadata(userId) || removeMetadata(userId)")
	public void updateMetadataCheck(Long userId) {
		requireUserWriteAccess(userId);
	}

	/**
	 * Check access to reading user metadata.
	 * 
	 * @param filter
	 *        the filter to verify
	 */
	@Around("findMetadata(filter)")
	public Object readMetadataCheck(ProceedingJoinPoint pjp, UserMetadataFilter filter)
			throws Throwable {
		Long[] userIds = (filter == null ? null : filter.getUserIds());
		if ( userIds == null ) {
			log.warn("Access DENIED to metadata without user ID filter");
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, null);
		}
		for ( Long userId : userIds ) {
			requireUserReadAccess(userId);
		}

		// node ID passes, execute query and then filter based on security policy if necessary
		Object result = pjp.proceed();

		SecurityPolicy policy = getActiveSecurityPolicy();
		if ( policy == null || policy.getUserMetadataPaths() == null
				|| policy.getUserMetadataPaths().isEmpty() ) {
			return result;
		}

		return policyEnforcerCheck(result, SecurityPolicyMetadataType.User);
	}

}
