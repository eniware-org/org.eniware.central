/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.biz.EniwareEdgeMetadataBiz;
import org.eniware.central.domain.EniwareEdgeMetadataFilter;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityPolicy;
import org.eniware.central.security.SecurityPolicyMetadataType;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.support.AuthorizationSupport;
import org.springframework.util.AntPathMatcher;

/**
 * Security AOP support for {@link EniwareEdgeMetadataBiz}.
 * @version 1.0
 */
@Aspect
public class EniwareEdgeMetadataSecurityAspect extends AuthorizationSupport {

	/**
	 * Constructor.
	 * 
	 * @param userEdgeDao
	 *        the UserEdgeDao to use
	 */
	public EniwareEdgeMetadataSecurityAspect(UserEdgeDao userEdgeDao) {
		super(userEdgeDao);
		AntPathMatcher antMatch = new AntPathMatcher();
		antMatch.setCachePatterns(false);
		antMatch.setCaseSensitive(true);
		setPathMatcher(antMatch);
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.biz.EniwareEdgeMetadata*.addEniwareEdge*(..)) && args(EdgeId,..)")
	public void addMetadata(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.biz.EniwareEdgeMetadata*.storeEniwareEdge*(..)) && args(EdgeId,..)")
	public void storeMetadata(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.biz.EniwareEdgeMetadata*.removeEniwareEdge*(..)) && args(EdgeId)")
	public void removeMetadata(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.biz.EniwareEdgeMetadata*.findEniwareEdge*(..)) && args(filter,..)")
	public void findMetadata(EniwareEdgeMetadataFilter filter) {
	}

	/**
	 * Check access to modifying Edge metadata.
	 * 
	 * @param EdgeId
	 *        the ID of the Edge to verify
	 */
	@Before("addMetadata(EdgeId) || storeMetadata(EdgeId) || removeMetadata(EdgeId)")
	public void updateMetadataCheck(Long EdgeId) {
		requireEdgeWriteAccess(EdgeId);
	}

	/**
	 * Check access to reading Edge metadata.
	 * 
	 * @param pjp
	 *        the join point
	 * @param filter
	 *        the filter to verify
	 */
	@Around("findMetadata(filter)")
	public Object readMetadataCheck(ProceedingJoinPoint pjp, EniwareEdgeMetadataFilter filter)
			throws Throwable {
		Long[] EdgeIds = (filter == null ? null : filter.getEdgeIds());
		if ( EdgeIds == null ) {
			log.warn("Access DENIED to Edge metadata without Edge ID filter");
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, null);
		}
		for ( Long EdgeId : EdgeIds ) {
			requireEdgeReadAccess(EdgeId);
		}

		// Edge ID passes, execute query and then filter based on security policy if necessary
		Object result = pjp.proceed();

		SecurityPolicy policy = getActiveSecurityPolicy();
		if ( policy == null || policy.getEdgeMetadataPaths() == null
				|| policy.getEdgeMetadataPaths().isEmpty() ) {
			return result;
		}

		return policyEnforcerCheck(result, SecurityPolicyMetadataType.Edge);
	}

}
