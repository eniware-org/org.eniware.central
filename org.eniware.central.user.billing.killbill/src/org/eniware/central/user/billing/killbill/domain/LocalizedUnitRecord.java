/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import org.eniware.central.user.billing.domain.LocalizedInvoiceItemUsageRecordInfo;

/**
 * Localized {@link UnitRecord}.
 * 
 * @version 1.0
 */
public class LocalizedUnitRecord extends UnitRecord implements LocalizedInvoiceItemUsageRecordInfo {

	private final LocalizedInvoiceItemUsageRecordInfo delegate;

	/**
	 * Constructor.
	 * 
	 * @param record
	 *        to record to initialize from
	 * @param delegate
	 *        the delegate to delegate localization to
	 */
	public LocalizedUnitRecord(UnitRecord record, LocalizedInvoiceItemUsageRecordInfo delegate) {
		super(record);
		this.delegate = delegate;
	}

	@Override
	public String getLocalizedUnitType() {
		return delegate.getLocalizedUnitType();
	}

	@Override
	public String getLocalizedAmount() {
		return delegate.getLocalizedAmount();
	}

}
