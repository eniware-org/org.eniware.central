/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

import org.eniware.central.domain.Filter;

/**
 * Filter for Invoice.
 * 
 * @version 1.0
 */
public interface InvoiceFilter extends Filter {

	/**
	 * Get the ID of the user to find invoices for.
	 * 
	 * @return the user ID
	 */
	Long getUserId();

	/**
	 * Flag to filter based on the invoice being unpaid or not.
	 * 
	 * @return {@literal true} for only unpaid invoices; {@literal false} for
	 *         only fully paid invoices; {@code null} for both
	 */
	Boolean getUnpaid();

}
