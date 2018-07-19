/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.user.biz.RegistrationBiz;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.support.AuthorizationSupport;

/**
 * Security enforcing AOP aspect for {@link RegistrationBiz}.
 * 
 * @version 1.0
 */
@Aspect
public class RegistrationSecurityAspect extends AuthorizationSupport {

	/**
	 * Constructor.
	 * 
	 * @param userEdgeDao
	 *        the UserEdgeDao
	 */
	public RegistrationSecurityAspect(UserEdgeDao userEdgeDao) {
		super(userEdgeDao);
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*RegistrationBiz.renewEdgeCertificate(..)) && args(userEdge, ..)")
	public void renewEdgeCertificate(UserEdge userEdge) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*RegistrationBiz.getPendingEdgeCertificateRenewal(..)) && args(userEdge, ..)")
	public void getPendingEdgeCertificateRenewal(UserEdge userEdge) {
	}

	@Before("renewEdgeCertificate(userEdge) || getPendingEdgeCertificateRenewal(userEdge)")
	public void processEdgeCertificateCheck(UserEdge userEdge) {
		requireEdgeWriteAccess(userEdge.getEdge().getId());
	}

}
