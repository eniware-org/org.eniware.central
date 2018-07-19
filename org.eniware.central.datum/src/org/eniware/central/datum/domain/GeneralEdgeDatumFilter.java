/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.central.domain.Filter;
import org.joda.time.DateTime;

/**
 * Filter API for {@link GeneralEdgeDatum}.
 *
 * @version 1.2
 */
public interface GeneralEdgeDatumFilter extends Filter {

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
	 * Get the first Edge ID. This returns the first available Edge ID from the
	 * {@link #getEdgeIds()} array, or <em>null</em> if not available.
	 * 
	 * @return the Edge ID, or <em>null</em> if not available
	 */
	public Long getEdgeId();

	/**
	 * Get an array of Edge IDs.
	 * 
	 * @return array of Edge IDs (may be <em>null</em>)
	 */
	public Long[] getEdgeIds();

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
	 * {@code i.watts} might return a watt value.
	 * 
	 * @return bean object path to extract
	 * @since 1.1
	 */
	public String getDataPath();

	/**
	 * Get the {@link #getDataPath()} value split into bean path elements. For
	 * example a path like {@code i.watts} would return an array like
	 * {@code ["i", "watts"]}.
	 * 
	 * @return the data path elements, or <em>null</em>
	 * @since 1.1
	 */
	public String[] getDataPathElements();

	/**
	 * Get the first user ID. This returns the first available user ID from the
	 * {@link #getUserIds()} array, or <em>null</em> if not available.
	 * 
	 * @return the first user ID, or <em>null</em> if not available
	 * @since 1.2
	 */
	public Long getUserId();

	/**
	 * Get an array of user IDs.
	 * 
	 * @return array of user IDs (may be <em>null</em>)
	 * @since 1.2
	 */
	public Long[] getUserIds();

}
