/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

import java.math.BigDecimal;

import org.eniware.central.domain.FilterMatch;
import org.joda.time.DateTime;

/**
 * Search resulut match for an Invoice.
 * 
 * @version 1.0
 */
public interface InvoiceMatch extends FilterMatch<String> {

	/**
	 * Get the date this invoice was created.
	 * 
	 * @return the created date
	 */
	DateTime getCreated();

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
	 * Get the currency this invoice is in, as a string currency code like
	 * {@literal NZD} or {@literal USD}.
	 * 
	 * @return the currency code
	 */
	String getCurrencyCode();

}
