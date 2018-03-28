/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

import java.math.BigDecimal;
import java.util.List;

import org.eniware.central.domain.Entity;

/**
 * API for an invoice.
 * 
 * @version 1.1
 */
public interface Invoice extends Entity<String> {

	/**
	 * Get the time zone this invoice was created in.
	 * 
	 * @return the time zone ID
	 */
	String getTimeZoneId();

	/**
	 * Get a reference invoice "number".
	 * 
	 * @return the invoice number
	 */
	String getInvoiceNumber();

	/**
	 * Get the amount charged on this invoice.
	 * 
	 * @return the amount
	 */
	BigDecimal getAmount();

	/**
	 * Get the current invoice balance (unpaid amount).
	 * 
	 * <p>
	 * If this is positive then the invoice has outstanding payment due.
	 * </p>
	 * 
	 * @return the invoice balance
	 */
	BigDecimal getBalance();

	/**
	 * Get the total amount of all tax invoice items.
	 * 
	 * @return the total tax amount
	 * @since 1.1
	 */
	BigDecimal getTaxAmount();

	/**
	 * Get the currency this invoice is in, as a string currency code like
	 * {@literal NZD} or {@literal USD}.
	 * 
	 * @return the currency code
	 */
	String getCurrencyCode();

	/**
	 * Get the invoice items.
	 * 
	 * @return the invoice items
	 */
	List<InvoiceItem> getInvoiceItems();

}
