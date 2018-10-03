/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

/**
 * API for invoice match information that has been localized.
 * 
 * <p>
 * This API does not provide a way to localize an invoice instance. Rather, it
 * is a marker for an instance that has already been localized. This is designed
 * to support APIs that can localize objects based on a requested locale.
 * </p>
 * 
 * @version 1.0
 */
public interface LocalizedInvoiceMatchInfo {

	/**
	 * Get the invoice date, as a formatted and localized string.
	 * 
	 * @return the invoice creation date
	 */
	String getLocalizedDate();

	/**
	 * Get the amount charged on this invoice, as a formatted and localized
	 * string.
	 * 
	 * @return the amount
	 */
	String getLocalizedAmount();

	/**
	 * Get the current invoice balance (unpaid amount), as a formatted and
	 * localized string.
	 * 
	 * <p>
	 * If this is positive then the invoice has outstanding payment due.
	 * </p>
	 * 
	 * @return the invoice balance
	 */
	String getLocalizedBalance();

}
