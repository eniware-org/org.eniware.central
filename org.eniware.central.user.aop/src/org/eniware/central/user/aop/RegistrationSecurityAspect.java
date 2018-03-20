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
import org.eniware.central.user.dao.UserNodeDao;
import org.eniware.central.user.domain.UserNode;
import org.eniware.central.user.support.AuthorizationSupport;

/**
 * Security enforcing AOP aspect for {@link RegistrationBiz}.
 * 
 * @author matt
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
	public RegistrationSecurityAspect(UserNodeDao userNodeDao) {
		super(userNodeDao);
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*RegistrationBiz.renewNodeCertificate(..)) && args(userNode, ..)")
	public void renewNodeCertificate(UserNode userNode) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*RegistrationBiz.getPendingNodeCertificateRenewal(..)) && args(userNode, ..)")
	public void getPendingNodeCertificateRenewal(UserNode userNode) {
	}

	@Before("renewNodeCertificate(userNode) || getPendingNodeCertificateRenewal(userNode)")
	public void processNodeCertificateCheck(UserNode userNode) {
		requireNodeWriteAccess(userNode.getNode().getId());
	}

}
