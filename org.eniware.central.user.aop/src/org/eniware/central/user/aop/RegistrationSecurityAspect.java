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
	 * @param userNodeDao
	 *        the UserNodeDao
	 */
	public RegistrationSecurityAspect(UserEdgeDao userNodeDao) {
		super(userNodeDao);
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*RegistrationBiz.renewNodeCertificate(..)) && args(userNode, ..)")
	public void renewNodeCertificate(UserEdge userNode) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.user.biz.*RegistrationBiz.getPendingNodeCertificateRenewal(..)) && args(userNode, ..)")
	public void getPendingNodeCertificateRenewal(UserEdge userNode) {
	}

	@Before("renewNodeCertificate(userNode) || getPendingNodeCertificateRenewal(userNode)")
	public void processNodeCertificateCheck(UserEdge userNode) {
		requireNodeWriteAccess(userNode.getNode().getId());
	}

}
