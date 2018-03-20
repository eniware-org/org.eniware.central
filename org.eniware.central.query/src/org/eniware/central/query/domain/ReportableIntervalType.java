/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.domain;

import org.eniware.central.datum.domain.ConsumptionDatum;
import org.eniware.central.datum.domain.NodeDatum;
import org.eniware.central.datum.domain.PowerDatum;

/**
 * Enum type for use in reportable interval calculations.
 */
public enum ReportableIntervalType {

	/** ConsumptionDatum */
	Consumption,

	/** PowerDatum. */
	Power;

	/**
	 * Get a NodeDatum class type for this enum value.
	 * 
	 * @return the class type
	 */
	public Class<? extends NodeDatum> getDatumTypeClass() {
		switch (this) {
			case Consumption:
				return ConsumptionDatum.class;

			case Power:
				return PowerDatum.class;

			default:
				return null;
		}
	}
}
