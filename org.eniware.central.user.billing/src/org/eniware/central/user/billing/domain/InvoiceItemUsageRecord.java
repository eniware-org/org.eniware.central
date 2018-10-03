/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

import java.math.BigDecimal;

/**
 * A usage record attached to an invoice item.
 * 
 * @version 1.0
 */
public interface InvoiceItemUsageRecord {

	/**
	 * Get the usage unit type.
	 * 
	 * @return the usage unit type
	 */
	String getUnitType();

	/**
	 * Get the usage amount.
	 * 
	 * @return the amount
	 */
	BigDecimal getAmount();

}
