/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.math.BigDecimal;

import org.eniware.central.user.billing.domain.InvoiceItemUsageRecord;

/**
 * A unit usage record.
 * 
 * @author matt
 * @version 1.0
 */
public class UnitRecord implements InvoiceItemUsageRecord {

	private String unitType;
	private BigDecimal amount;

	/**
	 * Default constructor.
	 */
	public UnitRecord() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param unitType
	 *        the unit type
	 * @param amount
	 *        the amount
	 */
	public UnitRecord(String unitType, BigDecimal amount) {
		super();
		setUnitType(unitType);
		setAmount(amount);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param record
	 *        the record to copy
	 */
	public UnitRecord(UnitRecord record) {
		setAmount(record.getAmount());
		setUnitType(record.getUnitType());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((unitType == null) ? 0 : unitType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof UnitRecord) ) {
			return false;
		}
		UnitRecord other = (UnitRecord) obj;
		if ( amount == null ) {
			if ( other.amount != null ) {
				return false;
			}
		} else if ( !amount.equals(other.amount) ) {
			return false;
		}
		if ( unitType == null ) {
			if ( other.unitType != null ) {
				return false;
			}
		} else if ( !unitType.equals(other.unitType) ) {
			return false;
		}
		return true;
	}

	@Override
	public String getUnitType() {
		return unitType;
	}

	/**
	 * Set the usage unit type.
	 * 
	 * @param unitType
	 *        the unitType to set
	 */
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Set the amount.
	 * 
	 * @param amount
	 *        the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
