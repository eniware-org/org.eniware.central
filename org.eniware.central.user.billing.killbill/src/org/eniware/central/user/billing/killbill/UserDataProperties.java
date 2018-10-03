/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill;

import org.eniware.central.user.domain.User;

/**
 * Constants for {@link User} internal properties.
 * 
 * @version 1.0
 */
public final class UserDataProperties {

	/**
	 * The billing data key that holds the Killbill account external key to use.
	 */
	public static final String KILLBILL_ACCOUNT_KEY_DATA_PROP = "kb_accountKey";

	private UserDataProperties() {
		// don't constrcut me
	}

}
