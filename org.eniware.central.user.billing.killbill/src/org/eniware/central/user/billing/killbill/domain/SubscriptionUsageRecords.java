/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.util.List;
import org.joda.time.LocalDate;

/**
 * Usage records associated with a subscription time range.
 * 
 * @version 1.0
 */
public class SubscriptionUsageRecords {

	private String subscriptionId;
	private LocalDate startDate;
	private LocalDate endDate;
	private List<UnitRecord> rolledUpUnits;

	/**
	 * Get the subscription ID.
	 * 
	 * @return the subscriptionId
	 */
	public String getSubscriptionId() {
		return subscriptionId;
	}

	/**
	 * Set the subscription ID.
	 * 
	 * @param subscriptionId
	 *        the subscription ID to set
	 */
	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	/**
	 * Get the start date.
	 * 
	 * @return the startDate
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * Set the start date.
	 * 
	 * @param startDate
	 *        the start date to set
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the end date.
	 * 
	 * @return the endDate
	 */
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * Set the end date.
	 * 
	 * @param endDate
	 *        the end date to set
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get the usage records.
	 * 
	 * @return the records
	 */
	public List<UnitRecord> getRolledUpUnits() {
		return rolledUpUnits;
	}

	/**
	 * Set the usage records.
	 * 
	 * @param rolledUpUnits
	 *        the usage records to set
	 */
	public void setRolledUpUnits(List<UnitRecord> rolledUpUnits) {
		this.rolledUpUnits = rolledUpUnits;
	}

}
