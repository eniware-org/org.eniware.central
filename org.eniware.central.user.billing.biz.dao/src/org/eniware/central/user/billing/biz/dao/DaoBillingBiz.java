/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.biz.dao;

import java.util.List;
import java.util.Locale;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.support.BasicFilterResults;
import org.eniware.central.user.billing.biz.BillingBiz;
import org.eniware.central.user.billing.biz.BillingSystem;
import org.eniware.central.user.billing.domain.BillingDataConstants;
import org.eniware.central.user.billing.domain.Invoice;
import org.eniware.central.user.billing.domain.InvoiceFilter;
import org.eniware.central.user.billing.domain.InvoiceMatch;
import org.eniware.central.user.dao.UserDao;
import org.eniware.central.user.domain.User;
import org.eniware.util.OptionalServiceCollection;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;

/**
 * DAO based implementation of {@link BillingBiz} that delegates responsibility
 * to the {@link BillingSystem} configured for each user.
 *
 * @version 1.1
 */
public class DaoBillingBiz implements BillingBiz {

	private final UserDao userDao;
	private final OptionalServiceCollection<BillingSystem> billingSystems;

	/**
	 * Constructor.
	 * 
	 * @param userDao
	 *        the UserDao to use
	 */
	public DaoBillingBiz(UserDao userDao, OptionalServiceCollection<BillingSystem> billingSystems) {
		super();
		this.userDao = userDao;
		this.billingSystems = billingSystems;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation returns the first available system where
	 * {@link BillingSystem#supportsAccountingSystemKey(String)} returns
	 * {@literal true} for the user's internal data
	 * {@link BillingDataConstants#ACCOUNTING_DATA_PROP} value.
	 * </p>
	 */
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public BillingSystem billingSystemForUser(Long userId) {
		User user = userDao.get(userId);
		if ( user != null ) {
			Object systemKey = user.getInternalDataValue(BillingDataConstants.ACCOUNTING_DATA_PROP);
			if ( systemKey != null ) {
				for ( BillingSystem bs : billingSystems.services() ) {
					if ( bs.supportsAccountingSystemKey(systemKey.toString()) ) {
						return bs;
					}
				}
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public FilterResults<InvoiceMatch> findFilteredInvoices(InvoiceFilter filter,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max) {
		BillingSystem system = billingSystemForUser(filter.getUserId());
		if ( system == null ) {
			return new BasicFilterResults<>(null, 0L, 0, 0);
		}
		return system.findFilteredInvoices(filter, sortDescriptors, offset, max);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Invoice getInvoice(Long userId, String invoiceId, Locale locale) {
		BillingSystem system = billingSystemForUser(userId);
		if ( system == null ) {
			return null;
		}
		return system.getInvoice(userId, invoiceId, locale);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Resource renderInvoice(Long userId, String invoiceId, MimeType outputType, Locale locale) {
		BillingSystem system = billingSystemForUser(userId);
		if ( system == null ) {
			return null;
		}
		return system.renderInvoice(userId, invoiceId, outputType, locale);
	}

}
