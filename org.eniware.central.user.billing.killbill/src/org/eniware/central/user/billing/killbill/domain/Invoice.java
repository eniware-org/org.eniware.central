/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.eniware.central.domain.BaseObjectEntity;
import org.eniware.central.user.billing.domain.InvoiceMatch;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * A Killbill invoice.
 * 
 * @version 1.0
 */
public class Invoice extends BaseObjectEntity<String>
		implements org.eniware.central.user.billing.domain.Invoice, InvoiceMatch {

	private static final long serialVersionUID = -8352526186945752646L;

	private String accountId;
	private String timeZoneId = "UTC";
	private LocalDate invoiceDate;
	private String invoiceNumber;
	private BigDecimal amount;
	private BigDecimal balance;
	private String currencyCode;

	private List<InvoiceItem> items;

	/**
	 * Default constructor.
	 */
	public Invoice() {
		super();
	}

	/**
	 * Construct with an ID.
	 * 
	 * @param id
	 *        the ID
	 */
	public Invoice(String id) {
		super();
		setId(id);
	}

	/**
	 * Set the invoice ID.
	 * 
	 * <p>
	 * This is an alias for {@link #setId(String)} passing
	 * {@link UUID#toString()}.
	 * </p>
	 * 
	 * @param invoiceId
	 *        the invoice ID to set
	 */
	public void setInvoiceId(UUID invoiceId) {
		setId(invoiceId.toString());
	}

	/**
	 * Get the invoice number.
	 * 
	 * @return the invoice number
	 */
	@Override
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	/**
	 * Set the invoice number.
	 * 
	 * @param invoiceNumber
	 *        the invoiceNumber to set
	 */
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
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
		if ( this.invoiceDate != null ) {
			setCreated(invoiceDate.toDateTimeAtStartOfDay(DateTimeZone.forID(this.timeZoneId)));
		}
		if ( this.items != null ) {
			for ( InvoiceItem item : this.items ) {
				item.setTimeZoneId(timeZoneId);
			}
		}
	}

	/**
	 * Get the invoice date.
	 * 
	 * @return the invoice date
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public LocalDate getInvoiceDate() {
		return invoiceDate;
	}

	/**
	 * Set the invoice date.
	 * 
	 * @param invoiceDate
	 *        the invoice date to set
	 */
	public void setInvoiceDate(LocalDate invoiceDate) {
		this.invoiceDate = invoiceDate;
		DateTime created = null;
		if ( invoiceDate != null ) {
			created = invoiceDate.toDateTimeAtStartOfDay(DateTimeZone.forID(this.timeZoneId));
		}
		setCreated(created);
	}

	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public BigDecimal getTaxAmount() {
		List<InvoiceItem> list = this.items;
		if ( list == null ) {
			list = Collections.emptyList();
		}
		return list.stream().filter(i -> "TAX".equals(i.getItemType())).map(i -> i.getAmount())
				.reduce(BigDecimal.ZERO, (a, n) -> a.add(n));
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

	@Override
	public BigDecimal getBalance() {
		return balance;
	}

	/**
	 * Set the balance.
	 * 
	 * @param balance
	 *        the balance to set
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	@JsonGetter("currency")
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Set the currency code.
	 * 
	 * @param currencyCode
	 *        the currencyCode to set
	 */
	@JsonSetter("currency")
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<org.eniware.central.user.billing.domain.InvoiceItem> getInvoiceItems() {
		return (List) getItems();
	}

	/**
	 * Get the invoice items.
	 * 
	 * @return the invoice items
	 */
	@JsonIgnore
	public List<InvoiceItem> getItems() {
		return items;
	}

	/**
	 * Set the invoice items
	 * 
	 * @param items
	 *        the invoice items to set
	 */
	@JsonSetter("items")
	public void setItems(List<InvoiceItem> items) {
		this.items = items;
	}

	/**
	 * Get the account ID that owns the invoice.
	 * 
	 * @return the account ID
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * Set the account ID that owns the invoice.
	 * 
	 * @param accountId
	 *        the account ID to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

}
