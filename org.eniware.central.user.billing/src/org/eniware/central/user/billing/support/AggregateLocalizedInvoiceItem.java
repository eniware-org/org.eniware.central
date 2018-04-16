/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.support;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import org.eniware.central.user.billing.domain.InvoiceItem;
import org.eniware.central.user.billing.domain.LocalizedInvoiceItemInfo;
import org.eniware.central.user.billing.domain.LocalizedInvoiceItemUsageRecordInfo;

import org.eniware.javax.money.MoneyUtils;

/**
 * An aggregate invoice item that sums the item amount for a group of items.
 * 
 * <p>
 * This class is designed to support combining similar invoice items into a
 * single logical value, for example to combine all tax items into a single
 * value.
 * </p>
 * 
 * <p>
 * Unless otherwise documented, the methods of this class delegate to the
 * <b>first</b> invoice item added via
 * {@link #addItem(LocalizedInvoiceItemInfo)}. Documented methods like
 * {@link #getAmount()} return an aggregate value derived from all invoice items
 * added.
 * </p>
 * 
 * @version 1.0
 * @since 0.2
 */
public class AggregateLocalizedInvoiceItem implements LocalizedInvoiceItemInfo {

	private final Locale locale;
	private final List<InvoiceItem> items = new ArrayList<>(4);
	private LocalizedInvoiceItem delegate;

	/**
	 * Constructor.
	 * 
	 * @param locale
	 *        the desired locale
	 */
	public AggregateLocalizedInvoiceItem(Locale locale) {
		super();
		this.locale = locale;
	}

	/**
	 * Add an invoice item.
	 * 
	 * @param item
	 *        the item to add
	 * @return this object
	 */
	public AggregateLocalizedInvoiceItem addItem(InvoiceItem item) {
		items.add(item);
		if ( delegate == null ) {
			delegate = new LocalizedInvoiceItem(item, locale);
		}
		return this;
	}

	/**
	 * Get the ID of the first item added.
	 * 
	 * @return the id
	 */
	public String getId() {
		return delegate.getId();
	}

	/**
	 * Get a supplier of aggregate items for a specific locale.
	 * 
	 * @param locale
	 *        the locale to use for all supplied items
	 * @param delegate
	 *        the delegate to handle localized messages
	 * @return the supplier
	 */
	public static Supplier<AggregateLocalizedInvoiceItem> itemOfLocale(Locale locale) {
		return new Supplier<AggregateLocalizedInvoiceItem>() {

			@Override
			public AggregateLocalizedInvoiceItem get() {
				return new AggregateLocalizedInvoiceItem(locale);
			}

		};
	}

	/**
	 * Add items from another aggregate.
	 * 
	 * @param agg
	 *        the aggregate
	 * @return this object
	 */
	public AggregateLocalizedInvoiceItem addItems(AggregateLocalizedInvoiceItem agg) {
		items.addAll(agg.items);
		return this;
	}

	/**
	 * Get an aggregate amount of all configured invoice items.
	 * 
	 * <p>
	 * This will return the sum of the amount of all invoice items configured
	 * via {@link #addItem(ExtendedInvoiceItemFormatter)}.
	 * </p>
	 * 
	 * @return the aggregate amount
	 */
	public BigDecimal getAmount() {
		return items.stream().map(item -> item.getAmount()).reduce(BigDecimal.ZERO, (l, r) -> l.add(r));
	}

	/**
	 * Get a formatted and localized aggregate amount of all configured invoice
	 * items.
	 * 
	 * <p>
	 * This will return a formatted value of {@link #getAmount()}.
	 * </p>
	 */
	@Override
	public String getLocalizedAmount() {
		return MoneyUtils.formattedMoneyAmountFormatWithSymbolCurrencyStyle(locale,
				delegate.getCurrencyCode(), getAmount());
	}

	@Override
	public String getLocalizedDescription() {
		String desc = delegate.getLocalizedDescription();
		if ( desc == null ) {
			desc = delegate.getDescription();
		}
		return desc;
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
	public List<LocalizedInvoiceItemUsageRecordInfo> getLocalizedInvoiceItemUsageRecords() {
		return Collections.emptyList(); // maybe join lists into single list
	}

}
