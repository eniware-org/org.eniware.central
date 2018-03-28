/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.util.List;

import org.eniware.central.user.billing.domain.LocalizedInvoiceItemInfo;
import org.eniware.central.user.billing.domain.LocalizedInvoiceItemUsageRecordInfo;

/**
 * Localized Killbill {@link InvoiceItem}.
 * 
 * @version 1.0
 */
public class LocalizedInvoiceItem extends InvoiceItem implements LocalizedInvoiceItemInfo {

	private static final long serialVersionUID = 8400188959819683313L;

	private final LocalizedInvoiceItemInfo delegate;

	/**
	 * Constructor.
	 */
	public LocalizedInvoiceItem(InvoiceItem item, LocalizedInvoiceItemInfo delegate) {
		super(item);
		this.delegate = delegate;
	}

	@Override
	public String getLocalizedDescription() {
		return delegate.getLocalizedDescription();
	}

	@Override
	public String getLocalizedStartDate() {
		return delegate.getLocalizedStartDate();
	}

	@Override
	public String getLocalizedEndDate() {
		return delegate.getLocalizedEndDate();
	}

	@Override
	public String getLocalizedAmount() {
		return delegate.getLocalizedAmount();
	}

	@Override
	public List<LocalizedInvoiceItemUsageRecordInfo> getLocalizedInvoiceItemUsageRecords() {
		return delegate.getLocalizedInvoiceItemUsageRecords();
	}

}
