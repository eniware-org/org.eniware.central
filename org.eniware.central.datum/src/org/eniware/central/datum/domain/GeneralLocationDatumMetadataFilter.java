/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.central.domain.Filter;
import org.eniware.central.domain.Location;

/**
 * Filter API for {@link GeneralLocationDatumMetadata}.
 *
 * @version 1.0
 */
public interface GeneralLocationDatumMetadataFilter extends Filter {

	/**
	 * Get a location filter to restrict the results to. This provides a way to
	 * query for {@link GeneralLocationDatumMetadata} indirectly that match a
	 * {@link Location} criteria, for example all metadata that match the region
	 * <em>Wellington</em>.
	 * 
	 * @return the location filter
	 */
	public Location getLocation();

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
	 * Get the first tag. This returns the first available tag from the
	 * {@link #getTags()} array, or <em>null</em> if not available.
	 * 
	 * @return the first tag, or <em>null</em> if not available
	 */
	public String getTag();

	/**
	 * Get an array of tags.
	 * 
	 * @return array of tags (may be <em>null</em>)
	 */
	public String[] getTags();

}
