/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.eniware.central.domain.Entity;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * A line item on an invoice.
 * 
 * <p>
 * The {@link #getCreated()} value on this API represents the start date of the
 * invoice item, i.e. the time-zone specific equivalent of
 * {@link #getStartDate()}.
 * </p>
 * 
 * @version 1.1
 */
public interface InvoiceItem extends Entity<String> {

	/**
	 * The invoice item type for tax items.
	 * 
	 * @since 1.1
	 */
	public static final String TYPE_TAX = "TAX";

	/**
	 * Get metadata associated with this item.
	 * 
	 * @return the metadata, or {@literal null} if none
	 */
	Map<String, Object> getMetadata();

	/**
	 * Get the time zone of the invoice item.
	 * 
	 * @return the time zone ID
	 */
	String getTimeZoneId();

	/**
	 * Get the plan name.
	 * 
	 * @return the plan name
	 */
	String getPlanName();

	/**
	 * Get the item type.
	 * 
	 * @return the item type
	 */
	String getItemType();

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	String getDescription();

	/**
	 * Get the start date.
	 * 
	 * @return the start date
	 */
	LocalDate getStartDate();

	/**
	 * Get the ended date.
	 * 
	 * @return the ended date
	 */
	LocalDate getEndDate();

	/**
	 * Get the amount.
	 * 
	 * @return the amount
	 */
	BigDecimal getAmount();

	/**
	 * Get the currency code.
	 * 
	 * @return the currencyCode
	 */
	String getCurrencyCode();

	/**
	 * Get the item ended date (with time zone).
	 * 
	 * @return the ended date
	 */
	DateTime getEnded();

	/**
	 * Get any usage records associated with this invoice.
	 * 
	 * @return the usage records
	 */
	List<InvoiceItemUsageRecord> getItemUsageRecords();

}
