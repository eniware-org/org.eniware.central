/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

/**
 * Information about a billing system.
 * 
 * @author matt
 * @version 1.0
 */
public interface BillingSystemInfo {

	/**
	 * Get a unique system key for the accounting functions of this system.
	 * 
	 * @return a unique key
	 */
	String getAccountingSystemKey();

}
