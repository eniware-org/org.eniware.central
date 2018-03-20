/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

/**
 * Killbill account info.
 * 
 * @author matt
 * @version 1.0
 */
public class Account {

	private String externalKey;
	private String accountId;
	private String name;
	private String email;
	private String timeZone;
	private String country;
	private String currency;
	private String locale;
	private String paymentMethodId;
	private Integer billCycleDayLocal;
	private Boolean isNotifiedForInvoices;

	/**
	 * Default constructor.
	 */
	public Account() {
		super();
	}

	/**
	 * Construct with an ID.
	 * 
	 * @param accountId
	 *        the account ID
	 */
	public Account(String accountId) {
		super();
		this.accountId = accountId;
	}

	/**
	 * Construct with an ID and time zone.
	 * 
	 * @param accountId
	 *        the account ID
	 * @param timeZone
	 *        the account time zone
	 */
	public Account(String accountId, String timeZone) {
		super();
		this.accountId = accountId;
		this.timeZone = timeZone;
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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *        the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *        the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone
	 *        the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *        the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *        the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the paymentMethodId
	 */
	public String getPaymentMethodId() {
		return paymentMethodId;
	}

	/**
	 * @param paymentMethodId
	 *        the paymentMethodId to set
	 */
	public void setPaymentMethodId(String paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	/**
	 * @return the billCycleDayLocal
	 */
	public Integer getBillCycleDayLocal() {
		return billCycleDayLocal;
	}

	/**
	 * @param billCycleDayLocal
	 *        the billCycleDayLocal to set
	 */
	public void setBillCycleDayLocal(Integer billCycleDayLocal) {
		this.billCycleDayLocal = billCycleDayLocal;
	}

	/**
	 * Get the locale.
	 * 
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Set the locale.
	 * 
	 * @param locale
	 *        the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Get the invoice notification flag.
	 * 
	 * @return the notifiedForInvoices
	 */
	public Boolean getIsNotifiedForInvoices() {
		return isNotifiedForInvoices;
	}

	/**
	 * Set the invoice notification flag.
	 * 
	 * @param notifiedForInvoices
	 *        the notifiedForInvoices to set
	 */
	public void setIsNotifiedForInvoices(Boolean notifiedForInvoices) {
		this.isNotifiedForInvoices = notifiedForInvoices;
	}

}
