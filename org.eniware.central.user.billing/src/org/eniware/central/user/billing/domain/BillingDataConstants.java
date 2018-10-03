/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

import java.util.Collections;
import org.eniware.central.user.domain.UserFilterCommand;

/**
 * Constants related to billing data.
 *
 * @version 1.0
 */
public final class BillingDataConstants {

	private BillingDataConstants() {
		// don't construct me
	}

	/** The billing data property that holds the accounting integration name. */
	public static final String ACCOUNTING_DATA_PROP = "accounting";

	/**
	 * Create a new filter for searching for a specific accounting type.
	 * 
	 * @param type
	 *        the type of accounting to search for
	 * @return the filter
	 */
	public static UserFilterCommand filterForAccountingType(String type) {
		UserFilterCommand criteria = new UserFilterCommand();
		criteria.setInternalData(Collections.singletonMap(ACCOUNTING_DATA_PROP, type));
		return criteria;
	}
}
