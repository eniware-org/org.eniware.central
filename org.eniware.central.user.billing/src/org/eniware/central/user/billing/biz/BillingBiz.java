/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.biz;

import java.util.List;
import java.util.Locale;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.user.billing.domain.Invoice;
import org.eniware.central.user.billing.domain.InvoiceFilter;
import org.eniware.central.user.billing.domain.InvoiceMatch;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;

/**
 * API for billing business logic.
 * 
 * @author matt
 * @version 1.0
 */
public interface BillingBiz {

	/**
	 * Get the billing system configured for a given user.
	 * 
	 * @param userId
	 *        the ID of the user to get the billing system for
	 * @return the billing system, or {@literal null} if no system is configured
	 *         or available
	 */
	BillingSystem billingSystemForUser(Long userId);

	/**
	 * Get an invoice by ID.
	 * 
	 * @param userId
	 *        the user ID to get the invoice for
	 * @param invoiceId
	 *        the invoice ID to get
	 * @return the invoice, or {@literal null} if not available
	 */
	Invoice getInvoice(Long userId, String invoiceId, Locale locale);

	/**
	 * Search for invoices.
	 * 
	 * @param filter
	 *        the query filter
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * @return the results, never {@literal null}
	 */
	FilterResults<InvoiceMatch> findFilteredInvoices(InvoiceFilter filter,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max);

	/**
	 * Render an invoice.
	 * 
	 * @param userId
	 *        the ID of the user to render the invoice for
	 * @param invoiceId
	 *        the ID of the invoice to render
	 * @param outputType
	 *        the desired output type, e.g. {@literal text/html}
	 * @param locale
	 *        the desired output locale
	 * @return a resource with the result data, or {@literal null} if the
	 *         invoice is not available
	 * @throws IllegalArgumentException
	 *         if {@code outputType} is not supported
	 */
	Resource renderInvoice(Long userId, String invoiceId, MimeType outputType, Locale locale);
}
