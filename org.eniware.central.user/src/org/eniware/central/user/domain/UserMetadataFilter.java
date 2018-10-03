/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.central.domain.Filter;

/**
 * Filter API for {@link UserMetadataEntity}.
 * 
 * @version 1.0
 * @since 1.23
 */
public interface UserMetadataFilter extends Filter {

	/**
	 * Get the first user ID. This returns the first available user ID from the
	 * {@link #getUserIds()} array, or <em>null</em> if not available.
	 * 
	 * @return the user ID, or <em>null</em> if not available
	 */
	public Long getUserId();

	/**
	 * Get an array of user IDs.
	 * 
	 * @return array of user IDs (may be <em>null</em>)
	 */
	public Long[] getUserIds();

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
