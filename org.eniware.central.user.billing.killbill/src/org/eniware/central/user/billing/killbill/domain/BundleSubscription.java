/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * A bundle and subscription combo, for setting up a new bundle.
 * 
 * @author matt
 * @version 1.0
 */
public class BundleSubscription {

	private final Bundle bundle;

	/**
	 * Constructor.
	 */
	public BundleSubscription(Bundle bundle) {
		super();
		this.bundle = bundle;
	}

	/**
	 * Get the bundle.
	 * 
	 * @return the bundle
	 */
	@JsonIgnore
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * Get the bundle account ID.
	 * 
	 * @return the bundle account ID
	 */
	public String getAccountId() {
		return bundle.getAccountId();
	}

	/**
	 * Get the bundle external key.
	 * 
	 * @return the bundle external key
	 */
	public String getExternalKey() {
		return bundle.getExternalKey();
	}

	/**
	 * Get the first subscription.
	 * 
	 * @return the first subscription, or {@literal null}
	 */
	@JsonUnwrapped
	public Subscription getSubscription() {
		List<Subscription> subs = bundle.getSubscriptions();
		return (subs != null && !subs.isEmpty() ? subs.get(0) : null);
	}

}
