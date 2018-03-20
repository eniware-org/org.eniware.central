/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.support;

import org.eniware.central.user.billing.domain.BillingSystemInfo;

/**
 * Basic implementation of {@link BillingSystemInfo}.
 * 
 * @author matt
 * @version 1.0
 */
public class BasicBillingSystemInfo implements BillingSystemInfo {

	private final String accountingSystemKey;

	/**
	 * Constructor.
	 * 
	 * @param accountingSystemKey
	 *        the accounting system key to use
	 */
	public BasicBillingSystemInfo(String accountingSystemKey) {
		super();
		this.accountingSystemKey = accountingSystemKey;
	}

	@Override
	public String getAccountingSystemKey() {
		return accountingSystemKey;
	}

}
