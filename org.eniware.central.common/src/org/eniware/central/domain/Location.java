/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.math.BigDecimal;

/**
 * Information describing the physical location of an object.
 * 
 * @author matt
 * @version 1.2
 */
public interface Location extends Filter {

	/**
	 * A generalized name, can be used for "virtual" locations.
	 * 
	 * @return the location name
	 */
	String getName();

	/**
	 * Get the ISO 3166-1 alpha-2 character country code.
	 * 
	 * @return 2-character country code
	 */
	String getCountry();

	/**
	 * A country-specific regional identifier.
	 * 
	 * @return region identifier
	 */
	String getRegion();

	/**
	 * Get a country-specific state or province identifier.
	 * 
	 * @return state or province identifier
	 */
	String getStateOrProvince();

	/**
	 * Get a country-specific postal code.
	 * 
	 * @return postal code
	 */
	String getPostalCode();

	/**
	 * Get the locality (city, town, etc.).
	 * 
	 * @return locality
	 */
	String getLocality();

	/**
	 * Get the street address.
	 * 
	 * @return street
	 */
	String getStreet();

	/**
	 * Get the decimal latitude.
	 * 
	 * @return latitude
	 */
	BigDecimal getLatitude();

	/**
	 * Get the decimal longitude.
	 * 
	 * @return longitude
	 */
	BigDecimal getLongitude();

	/**
	 * Get the elevation, in meters.
	 * 
	 * @return the elevation
	 * @since 1.2
	 */
	BigDecimal getElevation();

	/**
	 * Get a time zone ID, e.g. {@code Pacific/Auckland}.
	 * 
	 * @return a time zone ID
	 */
	String getTimeZoneId();

}
