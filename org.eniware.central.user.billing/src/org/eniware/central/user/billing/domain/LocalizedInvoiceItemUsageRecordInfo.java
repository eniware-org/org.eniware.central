/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

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
public interface LocalizedInvoiceItemUsageRecordInfo {

	/**
	 * Get a localized unit type of this usage record.
	 * 
	 * @return the localized unit type
	 */
	String getLocalizedUnitType();

	/**
	 * Get the usage amount, as a formatted and localized string.
	 * 
	 * @return the amount
	 */
	String getLocalizedAmount();

}
