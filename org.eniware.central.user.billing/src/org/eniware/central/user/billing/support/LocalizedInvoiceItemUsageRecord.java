/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.support;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.eniware.central.user.billing.domain.InvoiceItemUsageRecord;
import org.eniware.central.user.billing.domain.LocalizedInvoiceItemUsageRecordInfo;

/**
 * Localized version of {@link InvoiceItemUsageRecord}.
 * 
 * @version 1.0
 */
public class LocalizedInvoiceItemUsageRecord
		implements InvoiceItemUsageRecord, LocalizedInvoiceItemUsageRecordInfo {

	private final String localizedUnitType;
	private final InvoiceItemUsageRecord item;
	private final Locale locale;

	/**
	 * Convenience builder.
	 * 
	 * @param item
	 *        the item to localize
	 * @param locale
	 *        the locale to localize to
	 * @return the localized invoice
	 */
	public static LocalizedInvoiceItemUsageRecord of(InvoiceItemUsageRecord item, Locale locale) {
		return new LocalizedInvoiceItemUsageRecord(item, locale, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param item
	 *        the item to localize
	 * @param locale
	 *        the locale to localize to
	 */
	public LocalizedInvoiceItemUsageRecord(InvoiceItemUsageRecord item, Locale locale,
			String localizedUnitType) {
		super();
		this.item = item;
		this.locale = locale;
		this.localizedUnitType = localizedUnitType;
	}

	@Override
	public String getLocalizedUnitType() {
		return localizedUnitType;
	}

	@Override
	public String getLocalizedAmount() {
		NumberFormat fmt = DecimalFormat.getNumberInstance(locale);
		return fmt.format(getAmount());
	}

	@Override
	public String getUnitType() {
		return item.getUnitType();
	}

	@Override
	public BigDecimal getAmount() {
		return item.getAmount();
	}

}
