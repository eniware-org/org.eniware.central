/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import javax.cache.Cache;
import org.springframework.context.support.AbstractMessageSource;

/**
 * Resolve Killbill catalog messages.
 * 
 * @version 1.0
 */
public class KillbillCatalogMessageSource extends AbstractMessageSource {

	private final KillbillClient client;
	private final Cache<String, Properties> cache;

	public KillbillCatalogMessageSource(KillbillClient client, Cache<String, Properties> cache) {
		super();
		this.client = client;
		this.cache = cache;
	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		final String origLocaleCode = locale.toString();

		// try full version first
		Properties props = getPropsForLocale(origLocaleCode);

		// try lang/country
		if ( props == null && locale.getLanguage() != null && locale.getCountry() != null ) {
			String localeCode = locale.getLanguage() + "_" + locale.getCountry();
			if ( !localeCode.equals(origLocaleCode) ) {
				props = getPropsForLocale(localeCode);
			}
		}

		// try lang
		if ( props == null && locale.getLanguage() != null ) {
			String localeCode = locale.getLanguage();
			if ( !localeCode.equals(origLocaleCode) ) {
				props = getPropsForLocale(localeCode);
			}
		}

		if ( props == null ) {
			return null;
		}

		String msg = props.getProperty(code);
		if ( msg == null ) {
			return null;
		}
		return new MessageFormat(msg, locale);
	}

	private Properties getPropsForLocale(String locale) {
		// check cache first
		Properties props = cache.get(locale);
		if ( props == null ) {
			// try server
			props = client.invoiceCatalogTranslation(locale);

			// if not available, still cache the results
			if ( props == null ) {
				props = new Properties();
			}
			cache.putIfAbsent(locale, props);
		}
		return (props.isEmpty() ? null : props);
	}

}
