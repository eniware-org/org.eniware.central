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
import org.eniware.central.user.billing.domain.BillingSystemInfo;
import org.eniware.central.user.billing.domain.Invoice;
import org.eniware.central.user.billing.domain.InvoiceFilter;
import org.eniware.central.user.billing.domain.InvoiceMatch;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;

/**
 * API for interacting with a billing system.
 * 
 * @author matt
 * @version 1.1
 */
public interface BillingSystem {

	/**
	 * Get a unique system key for the accounting functions of this system.
	 * 
	 * @return a unique key
	 */
	String getAccountingSystemKey();

	/**
	 * Test if an accounting key is supported by this system.
	 * 
	 * @param key
	 *        the key to test
	 * @return {@literal true} if the key is supported
	 */
	boolean supportsAccountingSystemKey(String key);

	/**
	 * Get information about this system.
	 * 
	 * @param locale
	 *        the desired locale of the information, or {@literal null} for the
	 *        default locale
	 * @return the info
	 */
	BillingSystemInfo getInfo(Locale locale);

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
	 * Get an invoice by ID.
	 * 
	 * @param userId
	 *        the ID of the user to get the invoice for
	 * @param invoiceId
	 *        the ID of the invoice to get
	 * @param locale
	 *        a locale to show the invoice details in
	 * @return the invoice, or {@literal null} if not available
	 */
	Invoice getInvoice(Long userId, String invoiceId, Locale locale);

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
