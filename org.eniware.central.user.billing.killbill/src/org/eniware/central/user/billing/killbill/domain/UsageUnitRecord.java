/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill.domain;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Usage records of a specific type.
 * 
 * @version 1.0
 */
@JsonPropertyOrder({ "unitType", "usageRecords" })
public class UsageUnitRecord {

	private final String unitType;
	private final List<UsageRecord> usageRecords;

	/**
	 * Constructor.
	 */
	public UsageUnitRecord(String unitType, List<UsageRecord> usageRecords) {
		super();
		this.unitType = unitType;
		this.usageRecords = usageRecords;
	}

	/**
	 * Get the unit type.
	 * 
	 * @return the unitType
	 */
	public String getUnitType() {
		return unitType;
	}

	/**
	 * Get the usage records.
	 * 
	 * @return the usageRecords
	 */
	public List<UsageRecord> getUsageRecords() {
		return usageRecords;
	}

}
