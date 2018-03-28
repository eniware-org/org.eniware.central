/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.support;

import java.math.BigDecimal;
import java.util.Locale;

import org.eniware.central.user.billing.domain.InvoiceMatch;
import org.eniware.central.user.billing.domain.LocalizedInvoiceMatchInfo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import net.solarnetwork.javax.money.MoneyUtils;

/**
 * Localized version of {@link InvoiceMatch}.
 * 
 * @version 1.0
 */
public class LocalizedInvoiceMatch implements InvoiceMatch, LocalizedInvoiceMatchInfo {

	private final InvoiceMatch match;
	private final Locale locale;

	/**
	 * Convenience builder.
	 * 
	 * @param match
	 *        the match to localize
	 * @param locale
	 *        the locale to localize to
	 * @return the localized match
	 */
	public static LocalizedInvoiceMatch of(InvoiceMatch match, Locale locale) {
		return new LocalizedInvoiceMatch(match, locale);
	}

	/**
	 * Constructor.
	 * 
	 * @param match
	 *        the match to localize
	 * @param locale
	 *        the locale to localize to
	 */
	public LocalizedInvoiceMatch(InvoiceMatch match, Locale locale) {
		super();
		this.match = match;
		this.locale = locale;
	}

	@Override
	public String getLocalizedDate() {
		DateTimeFormatter fmt = DateTimeFormat.fullDate().withLocale(locale);
		String tz = getTimeZoneId();
		if ( tz != null ) {
			fmt = fmt.withZone(DateTimeZone.forID(tz));
		}
		return fmt.print(getCreated());
	}

	@Override
	public String getLocalizedAmount() {
		return MoneyUtils.formattedMoneyAmountFormatWithSymbolCurrencyStyle(locale, getCurrencyCode(),
				getAmount());
	}

	@Override
	public String getLocalizedBalance() {
		return MoneyUtils.formattedMoneyAmountFormatWithSymbolCurrencyStyle(locale, getCurrencyCode(),
				getBalance());
	}

	@Override
	public DateTime getCreated() {
		return match.getCreated();
	}

	@Override
	public String getTimeZoneId() {
		return match.getTimeZoneId();
	}

	@Override
	public String getInvoiceNumber() {
		return match.getInvoiceNumber();
	}

	@Override
	public BigDecimal getAmount() {
		return match.getAmount();
	}

	@Override
	public String getId() {
		return match.getId();
	}

	@Override
	public BigDecimal getBalance() {
		return match.getBalance();
	}

	@Override
	public String getCurrencyCode() {
		return match.getCurrencyCode();
	}

	@Override
	public int compareTo(String o) {
		return match.compareTo(o);
	}

}
