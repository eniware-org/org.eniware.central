/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Object to associate usage with a subscription.
 * 
 * @author matt
 * @version 1.0
 */
@JsonPropertyOrder({ "subscriptionId", "trackingId", "unitUsageRecords" })
public class SubscriptionUsage {

	private final Subscription subscription;
	private final String trackingId;
	private final List<UsageUnitRecord> unitUsageRecords;

	/**
	 * Constructor.
	 * 
	 * @param subscription
	 *        the subscription
	 * @param usage
	 *        the usage
	 */
	public SubscriptionUsage(Subscription subscription, String trackingId,
			List<UsageUnitRecord> unitUsageRecords) {
		super();
		this.subscription = subscription;
		this.trackingId = trackingId;
		this.unitUsageRecords = unitUsageRecords;
	}

	/**
	 * Get the subscription.
	 * 
	 * @return the subscription
	 */
	@JsonIgnore
	public Subscription getSubscription() {
		return subscription;
	}

	/**
	 * Get the subscription ID.
	 * 
	 * @return the subscription ID
	 */
	public String getSubscriptionId() {
		return subscription.getSubscriptionId();
	}

	/**
	 * Get the unit usage records.
	 * 
	 * @return the unit usage records
	 */
	public List<UsageUnitRecord> getUnitUsageRecords() {
		return unitUsageRecords;
	}

	/**
	 * Get the tracking ID.
	 * 
	 * @return the tracking ID
	 */
	public String getTrackingId() {
		return trackingId;
	}

}
