/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill;

import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import javax.cache.Cache;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.AuthorizationException.Reason;
import org.eniware.central.support.BasicFilterResults;
import org.eniware.central.user.billing.biz.BillingSystem;
import org.eniware.central.user.billing.domain.BillingSystemInfo;
import org.eniware.central.user.billing.domain.Invoice;
import org.eniware.central.user.billing.domain.InvoiceFilter;
import org.eniware.central.user.billing.domain.InvoiceMatch;
import org.eniware.central.user.billing.domain.LocalizedInvoiceItemInfo;
import org.eniware.central.user.billing.domain.LocalizedInvoiceItemUsageRecordInfo;
import org.eniware.central.user.billing.killbill.domain.Account;
import org.eniware.central.user.billing.killbill.domain.CustomField;
import org.eniware.central.user.billing.killbill.domain.InvoiceItem;
import org.eniware.central.user.billing.killbill.domain.LocalizedInvoiceItem;
import org.eniware.central.user.billing.killbill.domain.LocalizedUnitRecord;
import org.eniware.central.user.billing.killbill.domain.Subscription;
import org.eniware.central.user.billing.killbill.domain.SubscriptionUsageRecords;
import org.eniware.central.user.billing.killbill.domain.UnitRecord;
import org.eniware.central.user.billing.support.BasicBillingSystemInfo;
import org.eniware.central.user.billing.support.LocalizedInvoiceItemUsageRecord;
import org.eniware.central.user.dao.UserDao;
import org.eniware.central.user.domain.User;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;

/**
 * Killbill implementation of {@link BillingSystem}.
 * 
 * @version 1.1
 */
public class KillbillBillingSystem implements BillingSystem {

	/** The {@literal accounting} billing data value for Killbill. */
	public static final String ACCOUNTING_SYSTEM_KEY = "kb";

	private final UserDao userDao;
	private final KillbillClient client;
	private final MessageSource messageSource;

	private Cache<String, Subscription> subscriptionCache;

	/**
	 * Constructor.
	 * 
	 * @param client
	 *        the client to use
	 * @param userDao
	 *        the User DAO to use
	 */
	public KillbillBillingSystem(KillbillClient client, UserDao userDao, MessageSource messageSource) {
		super();
		this.userDao = userDao;
		this.client = client;
		this.messageSource = messageSource;
	}

	@Override
	public String getAccountingSystemKey() {
		return ACCOUNTING_SYSTEM_KEY;
	}

	@Override
	public boolean supportsAccountingSystemKey(String key) {
		return ACCOUNTING_SYSTEM_KEY.equals(key);
	}

	@Override
	public BillingSystemInfo getInfo(Locale locale) {
		return new BasicBillingSystemInfo(getAccountingSystemKey());
	}

	private Account accountForUser(Long userId) {
		User user = userDao.get(userId);
		if ( user == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_OBJECT, userId);
		}
		String accountKey = (String) user
				.getInternalDataValue(UserDataProperties.KILLBILL_ACCOUNT_KEY_DATA_PROP);
		if ( accountKey == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_OBJECT, userId);
		}
		return client.accountForExternalKey(accountKey);
	}

	@Override
	public FilterResults<InvoiceMatch> findFilteredInvoices(InvoiceFilter filter,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max) {
		Account account = accountForUser(filter.getUserId());
		if ( account == null ) {
			return new BasicFilterResults<InvoiceMatch>(null);
		}
		FilterResults<InvoiceMatch> results;
		if ( filter.getUnpaid() != null && filter.getUnpaid().booleanValue() ) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			List<InvoiceMatch> invoices = (List) client.listInvoices(account, true);
			results = new BasicFilterResults<>(invoices, (long) invoices.size(), 0, invoices.size());
		} else {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			FilterResults<InvoiceMatch> filterResults = (FilterResults) client.findInvoices(account,
					filter, sortDescriptors, offset, max);
			results = filterResults;
		}
		return results;
	}

	private Subscription getSubscription(String subscriptionId) {
		Subscription result = null;
		Cache<String, Subscription> cache = subscriptionCache;
		if ( cache != null ) {
			result = cache.get(subscriptionId);
		}
		if ( result == null ) {
			result = client.getSubscription(subscriptionId);
			if ( result != null ) {
				// populate custom fields
				List<CustomField> fields = client.customFieldsForSubscription(subscriptionId);
				result.setCustomFields(fields);
				if ( cache != null ) {
					cache.putIfAbsent(subscriptionId, result);
				}
			}
		}
		return result;
	}

	@Override
	public Invoice getInvoice(Long userId, String invoiceId, Locale locale) {
		Account account = accountForUser(userId);
		org.eniware.central.user.billing.killbill.domain.Invoice invoice = client
				.getInvoice(account, invoiceId, true, false);

		// populate usage records for appropriate items
		if ( invoice.getInvoiceItems() != null ) {
			for ( ListIterator<InvoiceItem> itr = invoice.getItems().listIterator(); itr.hasNext(); ) {
				InvoiceItem item = itr.next();

				// populate usage
				if ( "USAGE".equals(item.getItemType()) && item.getSubscriptionId() != null ) {
					SubscriptionUsageRecords records = client.usageRecordsForSubscription(
							item.getSubscriptionId(), item.getStartDate(), item.getEndDate());
					if ( records != null ) {
						item.setUsageRecords(records.getRolledUpUnits());
						if ( locale != null ) {
							for ( ListIterator<UnitRecord> recItr = item.getUsageRecords()
									.listIterator(); recItr.hasNext(); ) {
								UnitRecord rec = recItr.next();
								String unitType = messageSource.getMessage(rec.getUnitType(), null, null,
										locale);
								LocalizedInvoiceItemUsageRecordInfo locInfo = new LocalizedInvoiceItemUsageRecord(
										rec, locale, unitType);
								rec = new LocalizedUnitRecord(rec, locInfo);
								recItr.set(rec);
							}
						}
					}
				}

				// poplate metadata
				Subscription subscription = getSubscription(item.getSubscriptionId());
				if ( subscription != null ) {
					item.setCustomFields(subscription.getCustomFields());
				}

				if ( locale != null ) {
					String desc = messageSource.getMessage(item.getPlanName(), null, null, locale);
					LocalizedInvoiceItemInfo locInfo = new org.eniware.central.user.billing.support.LocalizedInvoiceItem(
							item, locale, desc);
					item = new LocalizedInvoiceItem(item, locInfo);
					itr.set(item);
				}
			}
		}
		return invoice;
	}

	@Override
	public Resource renderInvoice(Long userId, String invoiceId, MimeType outputType, Locale locale) {
		// verify first that account owns the requested invoice
		Account account = accountForUser(userId);
		org.eniware.central.user.billing.killbill.domain.Invoice invoice = client
				.getInvoice(account, invoiceId, false, false);
		if ( invoice == null || !account.getAccountId().equals(invoice.getAccountId()) ) {
			return null;
		}
		return client.renderInvoice(invoiceId, outputType, locale);
	}

	/**
	 * Set a cache to use for subscriptions.
	 * 
	 * @param subscriptionCache
	 *        the cache to set
	 */
	public void setSubscriptionCache(Cache<String, Subscription> subscriptionCache) {
		this.subscriptionCache = subscriptionCache;
	}

}
