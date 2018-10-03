/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eniware.central.domain.BaseObjectEntity;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * A Killbill invoice item.
 *
 * @version 1.0
 */
public class InvoiceItem extends BaseObjectEntity<String>
		implements org.eniware.central.user.billing.domain.InvoiceItem {

	private static final long serialVersionUID = 8438942092302679074L;

	private String bundleId;
	private String subscriptionId;
	private String planName;
	private String phaseName;
	private String usageName;
	private String itemType;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private DateTime ended;
	private BigDecimal amount;
	private String currencyCode;
	private String timeZoneId = "UTC";

	// internal fields not explicit in KB API
	private List<UnitRecord> usageRecords;
	private Map<String, CustomField> customFields;

	/**
	 * Default constructor.
	 */
	public InvoiceItem() {
		super();
	}

	/**
	 * Construct with an ID.
	 * 
	 * @param id
	 *        the ID
	 */
	public InvoiceItem(String id) {
		super();
		setId(id);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *        the item to copy
	 */
	public InvoiceItem(InvoiceItem item) {
		super();
		setAmount(item.getAmount());
		setBundleId(item.getBundleId());
		setCreated(item.getCreated());
		setCurrencyCode(item.getCurrencyCode());
		setCustomFields(item.getCustomFields());
		setDescription(item.getDescription());
		setEndDate(item.getEndDate());
		setEnded(item.getEnded());
		setId(item.getId());
		setItemType(item.getItemType());
		setModified(item.getModified());
		setPhaseName(item.getPhaseName());
		setPlanName(item.getPlanName());
		setStartDate(item.getStartDate());
		setSubscriptionId(item.getSubscriptionId());
		setTimeZoneId(item.getTimeZoneId());
		setUsageName(item.getUsageName());
		setUsageRecords(item.getUsageRecords());
	}

	/**
	 * Set the invoice item ID.
	 * 
	 * <p>
	 * This is an alias for {@link #setId(String)} passing
	 * {@link UUID#toString()}.
	 * </p>
	 * 
	 * @param invoiceItemId
	 *        the invoice item ID to set
	 */
	public void setInvoiceItemId(UUID invoiceItemId) {
		setId(invoiceItemId.toString());
	}

	/**
	 * Get the invoice time zone.
	 * 
	 * @return the invoice time zone
	 */
	@Override
	public String getTimeZoneId() {
		return timeZoneId;
	}

	/**
	 * Set the invoice time zone.
	 * 
	 * @param timeZoneId
	 *        the time zone ID to set
	 */
	public void setTimeZoneId(String timeZoneId) {
		if ( timeZoneId != null && !timeZoneId.equals(this.timeZoneId) ) {
			this.timeZoneId = timeZoneId;
			applyTimeZone(timeZoneId);
		}
	}

	private void applyTimeZone(String timeZoneId) {
		if ( timeZoneId == null ) {
			timeZoneId = "UTC";
		}
		if ( this.startDate != null ) {
			setCreated(startDate.toDateTimeAtStartOfDay(DateTimeZone.forID(timeZoneId)));
		}
		if ( this.endDate != null ) {
			setEnded(endDate.toDateTimeAtStartOfDay(DateTimeZone.forID(timeZoneId)));
		}
	}

	/**
	 * Get the bundle ID.
	 * 
	 * @return the bundle ID
	 */
	public String getBundleId() {
		return bundleId;
	}

	/**
	 * Set the bundle ID.
	 * 
	 * @param bundleId
	 *        the bundle ID to set
	 */
	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	/**
	 * Get the subscription ID.
	 * 
	 * @return the subscription ID
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
	 * Get the plan name.
	 * 
	 * @return the plan name
	 */
	@Override
	public String getPlanName() {
		return planName;
	}

	/**
	 * Set the plan name.
	 * 
	 * @param planName
	 *        the plan name to set
	 */
	public void setPlanName(String planName) {
		this.planName = planName;
	}

	/**
	 * Get the phase name.
	 * 
	 * @return the phase name
	 */
	public String getPhaseName() {
		return phaseName;
	}

	/**
	 * Set the phase name.
	 * 
	 * @param phaseName
	 *        the phase name to set
	 */
	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	/**
	 * Get the usage name.
	 * 
	 * @return the usage name
	 */
	public String getUsageName() {
		return usageName;
	}

	/**
	 * Set the usage name.
	 * 
	 * @param usageName
	 *        the usage name to set
	 */
	public void setUsageName(String usageName) {
		this.usageName = usageName;
	}

	/**
	 * Get the item type.
	 * 
	 * @return the item type
	 */
	@Override
	public String getItemType() {
		return itemType;
	}

	/**
	 * Set the item type.
	 * 
	 * @param itemType
	 *        the item type to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * 
	 * @param description
	 *        the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the start date.
	 * 
	 * @return the start date
	 */
	@Override
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
		DateTime created = null;
		if ( startDate != null ) {
			created = startDate.toDateTimeAtStartOfDay(DateTimeZone.forID(this.timeZoneId));
		}
		setCreated(created);
	}

	/**
	 * Get the ended date.
	 * 
	 * @return the ended date
	 */
	@Override
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * Set the ended date.
	 * 
	 * @param endDate
	 *        the ended date to set
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get the amount.
	 * 
	 * @return the amount
	 */
	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Set the amount.
	 * 
	 * @param amount
	 *        the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Get the currency code.
	 * 
	 * @return the currencyCode
	 */
	@Override
	@JsonGetter("currency")
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Set the currency code.
	 * 
	 * @param currencyCode
	 *        the currency code to set
	 */
	@JsonSetter("currency")
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * Get the item ended date (with time zone).
	 * 
	 * @return the ended date
	 */
	@Override
	public DateTime getEnded() {
		return ended;
	}

	/**
	 * Set the item ended date (with time zone).
	 * 
	 * @param ended
	 *        the ended to set
	 */
	public void setEnded(DateTime end) {
		this.ended = end;
	}

	/**
	 * Get the usage records.
	 * 
	 * @return the usage records
	 */
	@JsonIgnore
	public List<UnitRecord> getUsageRecords() {
		return usageRecords;
	}

	/**
	 * Set the usage records.
	 * 
	 * @param usageRecords
	 *        the usage records to set
	 */
	public void setUsageRecords(List<UnitRecord> usageRecords) {
		this.usageRecords = usageRecords;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<org.eniware.central.user.billing.domain.InvoiceItemUsageRecord> getItemUsageRecords() {
		return (List) getUsageRecords();
	}

	@Override
	public Map<String, Object> getMetadata() {
		Map<String, CustomField> fieldMap = customFields;
		return (fieldMap != null
				? fieldMap.entrySet().stream().collect(
						Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getValue()))
				: null);
	}

	/**
	 * Get all available custom fields.
	 * 
	 * @return the custom fields
	 */
	@JsonIgnore
	public Collection<CustomField> getCustomFields() {
		return (customFields != null ? customFields.values() : null);
	}

	/**
	 * Set all custom fields.
	 * 
	 * @param fields
	 *        the fields to set
	 */
	public void setCustomFields(Collection<CustomField> fields) {
		Map<String, CustomField> fieldMap = null;
		if ( fields != null ) {
			fieldMap = fields.stream()
					.collect(Collectors.toMap(CustomField::getName, Function.identity()));
		}
		this.customFields = fieldMap;
	}

}
