/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.util.List;

/**
 * A subscription bundle.
 * 
 * @version 1.0
 */
public class Bundle implements Cloneable {

	private String accountId;
	private String bundleId;
	private String externalKey;
	private List<Subscription> subscriptions;

	/**
	 * Get a subscription based on a plan name.
	 * 
	 * @param planName
	 *        the plan name to look for
	 * @return the first matching subscription, or {@literal null} if not found
	 */
	public Subscription subscriptionWithPlanName(String planName) {
		if ( subscriptions != null ) {
			for ( Subscription subscription : subscriptions ) {
				if ( planName.equals(subscription.getPlanName()) ) {
					return subscription;
				}
			}
		}
		return null;
	}

	/**
	 * Clone the bundle.
	 * 
	 * <p>
	 * <b>Note</b> the {@code subscriptions} list is <b>not</b> cloned, so the
	 * returned instance shares the same list as this object.
	 * </p>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch ( CloneNotSupportedException e ) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the bundleId
	 */
	public String getBundleId() {
		return bundleId;
	}

	/**
	 * @param bundleId
	 *        the bundleId to set
	 */
	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	/**
	 * @return the externalKey
	 */
	public String getExternalKey() {
		return externalKey;
	}

	/**
	 * @param externalKey
	 *        the externalKey to set
	 */
	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

	/**
	 * @return the subscriptions
	 */
	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	/**
	 * @param subscriptions
	 *        the subscriptions to set
	 */
	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId
	 *        the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

}
