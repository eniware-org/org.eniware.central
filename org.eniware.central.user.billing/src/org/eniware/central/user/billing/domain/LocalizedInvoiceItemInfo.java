/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

import java.util.List;

/**
 * API for invoice item information that has been localized.
 * 
 * <p>
 * This API does not provide a way to localize an invoice instance. Rather, it
 * is a marker for an instance that has already been localized. This is designed
 * to support APIs that can localize objects based on a requested locale.
 * </p>
 * 
 * @version 1.0
 */
public interface LocalizedInvoiceItemInfo {

	/**
	 * Get a localized description of the invoice item.
	 * 
	 * @return the description
	 */
	String getLocalizedDescription();

	/**
	 * Get the invoice item start date, as a formatted and localized string.
	 * 
	 * @return the invoice creation date
	 */
	String getLocalizedStartDate();

	/**
	 * Get the invoice item end date, as a formatted and localized string.
	 * 
	 * @return the invoice creation date
	 */
	String getLocalizedEndDate();

	/**
	 * Get the amount charged on this invoice item, as a formatted and localized
	 * string.
	 * 
	 * @return the amount
	 */
	String getLocalizedAmount();

	/**
	 * Get the localized invoice item usage records.
	 * 
	 * @return the localized usage records
	 */
	List<LocalizedInvoiceItemUsageRecordInfo> getLocalizedInvoiceItemUsageRecords();
}
