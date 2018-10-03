/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.central.domain.Filter;
import org.joda.time.DateTime;

/**
 * Filter API for {@link GeneralLocationDatum}.
 *
 * @version 1.0
 */
public interface GeneralLocationDatumFilter extends Filter {

	/**
	 * Flag to indicate that only the most recently available data should be
	 * returned.
	 * 
	 * @return the most recent only
	 */
	public boolean isMostRecent();

	/**
	 * Get a start date.
	 * 
	 * @return the start date
	 */
	public DateTime getStartDate();

	/**
	 * Get an end date.
	 * 
	 * @return the end date
	 */
	public DateTime getEndDate();

	/**
	 * Get the first location ID. This returns the first available location ID
	 * from the {@link #getLocationIds()} array, or <em>null</em> if not
	 * available.
	 * 
	 * @return the location ID, or <em>null</em> if not available
	 */
	public Long getLocationId();

	/**
	 * Get an array of location IDs.
	 * 
	 * @return array of location IDs (may be <em>null</em>)
	 */
	public Long[] getLocationIds();

	/**
	 * Get the first source ID. This returns the first available source ID from
	 * the {@link #getSourceIds()} array, or <em>null</em> if not available.
	 * 
	 * @return the first source ID, or <em>null</em> if not available
	 */
	public String getSourceId();

	/**
	 * Get an array of source IDs.
	 * 
	 * @return array of source IDs (may be <em>null</em>)
	 */
	public String[] getSourceIds();

	/**
	 * Get a bean object path to a specific data value key to extract and return
	 * from the results, instead of all data. For example a path like
	 * {@code i.temp} might return a temperature value.
	 * 
	 * @return bean object path to extract
	 * @since 1.1
	 */
	public String getDataPath();

	/**
	 * Get the {@link #getDataPath()} value split into bean path elements. For
	 * example a path like {@code i.temp} would return an array like
	 * {@code ["i", "temp"]}.
	 * 
	 * @return the data path elements, or <em>null</em>
	 * @since 1.1
	 */
	public String[] getDataPathElements();

}
