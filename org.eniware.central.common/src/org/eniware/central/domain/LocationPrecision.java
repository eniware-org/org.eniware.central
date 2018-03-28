/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * Enumeration of locatioin precision levels.
 * @version 1.0
 */
public enum LocationPrecision {

	LatLong(1),

	Block(5),

	Street(10),

	PostalCode(20),

	Locality(30),

	StateOrProvince(40),

	Region(50),

	TimeZone(60),

	Country(70);

	private final Integer precision;

	private LocationPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * Compare the precision of this to another.
	 * 
	 * @param other
	 *        the other
	 * @return -1 if this precision less than other precision, 0 if precisions
	 *         are equal, or 1 if this precision is greater than other precision
	 */
	public int comparePrecision(LocationPrecision other) {
		return this.precision.compareTo(other.precision);
	}

	/**
	 * Get a relative precision value for this enum. The smaller the value, the
	 * more precise a location of this level represents.
	 * 
	 * @return The precision.
	 */
	public Integer getPrecision() {
		return precision;
	}

}
