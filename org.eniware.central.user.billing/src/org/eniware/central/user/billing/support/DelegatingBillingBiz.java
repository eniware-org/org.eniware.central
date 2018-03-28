/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.support;

import java.util.List;
import java.util.Locale;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.user.billing.biz.BillingBiz;
import org.eniware.central.user.billing.biz.BillingSystem;
import org.eniware.central.user.billing.domain.Invoice;
import org.eniware.central.user.billing.domain.InvoiceFilter;
import org.eniware.central.user.billing.domain.InvoiceMatch;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;

/**
 * Delegating implementation of {@link BillingBiz}, mostly to help with AOP.
 * 
 * @version 1.1
 */
public class DelegatingBillingBiz implements BillingBiz {

	private final BillingBiz delegate;

	/**
	 * Constructor.
	 * 
	 * @param delegate
	 *        the delgate instance
	 */
	public DelegatingBillingBiz(BillingBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public BillingSystem billingSystemForUser(Long userId) {
		return delegate.billingSystemForUser(userId);
	}

	@Override
	public FilterResults<InvoiceMatch> findFilteredInvoices(InvoiceFilter filter,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max) {
		return delegate.findFilteredInvoices(filter, sortDescriptors, offset, max);
	}

	@Override
	public Invoice getInvoice(Long userId, String invoiceId, Locale locale) {
		return delegate.getInvoice(userId, invoiceId, locale);
	}

	@Override
	public Resource renderInvoice(Long userId, String invoiceId, MimeType outputType, Locale locale) {
		return delegate.renderInvoice(userId, invoiceId, outputType, locale);
	}

}
