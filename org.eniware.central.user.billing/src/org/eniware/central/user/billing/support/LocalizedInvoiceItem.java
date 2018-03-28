/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.support;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.eniware.central.user.billing.domain.InvoiceItem;
import org.eniware.central.user.billing.domain.InvoiceItemUsageRecord;
import org.eniware.central.user.billing.domain.LocalizedInvoiceItemInfo;
import org.eniware.central.user.billing.domain.LocalizedInvoiceItemUsageRecordInfo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import net.solarnetwork.javax.money.MoneyUtils;

/**
 * Localized version of {@link InvoiceItem}.
 *
 * @version 1.0
 */
public class LocalizedInvoiceItem implements InvoiceItem, LocalizedInvoiceItemInfo {

	private final String localizedDescription;
	private final InvoiceItem item;
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
	public static LocalizedInvoiceItem of(InvoiceItem item, Locale locale) {
		return new LocalizedInvoiceItem(item, locale);
	}

	/**
	 * Constructor.
	 * 
	 * @param item
	 *        the item to localize
	 * @param locale
	 *        the locale to localize to
	 */
	public LocalizedInvoiceItem(InvoiceItem item, Locale locale) {
		this(item, locale, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param item
	 *        the item to localize
	 * @param locale
	 *        the locale to localize to
	 */
	public LocalizedInvoiceItem(InvoiceItem item, Locale locale, String localizedDescription) {
		super();
		this.item = item;
		this.locale = locale;
		this.localizedDescription = localizedDescription;
	}

	@Override
	public String getLocalizedDescription() {
		return localizedDescription;
	}

	@Override
	public String getLocalizedStartDate() {
		DateTimeFormatter fmt = DateTimeFormat.mediumDate().withLocale(locale);
		String tz = getTimeZoneId();
		if ( tz != null ) {
			fmt = fmt.withZone(DateTimeZone.forID(tz));
		}
		return fmt.print(getCreated());
	}

	@Override
	public String getLocalizedEndDate() {
		DateTimeFormatter fmt = DateTimeFormat.mediumDate().withLocale(locale);
		String tz = getTimeZoneId();
		if ( tz != null ) {
			fmt = fmt.withZone(DateTimeZone.forID(tz));
		}
		return fmt.print(getEnded());
	}

	@Override
	public String getLocalizedAmount() {
		return MoneyUtils.formattedMoneyAmountFormatWithSymbolCurrencyStyle(locale, getCurrencyCode(),
				getAmount());
	}

	@Override
	public DateTime getCreated() {
		return item.getCreated();
	}

	@Override
	public String getTimeZoneId() {
		return item.getTimeZoneId();
	}

	@Override
	public String getId() {
		return item.getId();
	}

	@Override
	public Map<String, Object> getMetadata() {
		return item.getMetadata();
	}

	@Override
	public String getPlanName() {
		return item.getPlanName();
	}

	@Override
	public String getItemType() {
		return item.getItemType();
	}

	@Override
	public String getDescription() {
		return item.getDescription();
	}

	@Override
	public LocalDate getStartDate() {
		return item.getStartDate();
	}

	@Override
	public LocalDate getEndDate() {
		return item.getEndDate();
	}

	@Override
	public BigDecimal getAmount() {
		return item.getAmount();
	}

	@Override
	public String getCurrencyCode() {
		return item.getCurrencyCode();
	}

	@Override
	public DateTime getEnded() {
		return item.getEnded();
	}

	@Override
	public int compareTo(String o) {
		return item.compareTo(o);
	}

	@Override
	public List<InvoiceItemUsageRecord> getItemUsageRecords() {
		return item.getItemUsageRecords();
	}

	@Override
	public List<LocalizedInvoiceItemUsageRecordInfo> getLocalizedInvoiceItemUsageRecords() {
		List<InvoiceItemUsageRecord> recs = getItemUsageRecords();
		if ( recs == null ) {
			return null;
		} else if ( recs.isEmpty() ) {
			return Collections.emptyList();
		}
		return recs.stream().map(record -> {
			if ( record instanceof LocalizedInvoiceItemUsageRecordInfo ) {
				return (LocalizedInvoiceItemUsageRecordInfo) record;
			}
			return LocalizedInvoiceItemUsageRecord.of(record, locale);
		}).collect(Collectors.toList());
	}

}
