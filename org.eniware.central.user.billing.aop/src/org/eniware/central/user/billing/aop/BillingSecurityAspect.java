/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.user.billing.biz.BillingBiz;
import org.eniware.central.user.billing.domain.InvoiceFilter;

import net.solarnetwork.central.user.dao.UserNodeDao;
import net.solarnetwork.central.user.support.AuthorizationSupport;

/**
 * Security enforcing AOP aspect for {@link BillingBiz}.
 * 
 * @version 1.1
 */
@Aspect
public class BillingSecurityAspect extends AuthorizationSupport {

	/**
	 * Constructor.
	 * 
	 * @param userNodeDao
	 *        the UserNodeDao to use
	 */
	public BillingSecurityAspect(UserNodeDao userNodeDao) {
		super(userNodeDao);
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.billing.biz.*BillingBiz.*ForUser(..)) && args(userId, ..)")
	public void forUserAccess(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.billing.biz.*BillingBiz.getInvoice(..)) && args(userId, ..)")
	public void getInvoice(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.billing.biz.*BillingBiz.renderInvoice(..)) && args(userId, ..)")
	public void renderInvoice(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.billing.biz.*BillingBiz.findFilteredInvoices(..)) && args(filter, ..)")
	public void findFilteredInvoices(InvoiceFilter filter) {
	}

	@Before("forUserAccess(userId) || getInvoice(userId) || renderInvoice(userId)")
	public void checkForUserAccess(Long userId) {
		requireUserReadAccess(userId);
	}

	@Before("findFilteredInvoices(filter)")
	public void checkFindFilteredInvoices(InvoiceFilter filter) {
		Long userId = (filter != null ? filter.getUserId() : null);
		requireUserReadAccess(userId);
	}

}
