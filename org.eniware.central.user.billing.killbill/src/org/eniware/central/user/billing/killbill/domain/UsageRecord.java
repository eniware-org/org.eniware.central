/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.math.BigDecimal;
import org.joda.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A record of usage for a day.
 * 
 * @version 1.0
 */
@JsonPropertyOrder({ "recordDate", "amount" })
public class UsageRecord {

	private LocalDate recordDate;
	private BigDecimal amount;

	/**
	 * Default constructor.
	 */
	public UsageRecord() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param recordDate
	 *        the record date
	 * @param amount
	 *        the amount
	 */
	public UsageRecord(LocalDate recordDate, BigDecimal amount) {
		super();
		this.recordDate = recordDate;
		this.amount = amount;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public LocalDate getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(LocalDate recordDate) {
		this.recordDate = recordDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
