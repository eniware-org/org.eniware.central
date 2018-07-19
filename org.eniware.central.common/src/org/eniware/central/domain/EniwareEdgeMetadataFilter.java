/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * Filter API for {@link EniwareEdgeMetadata}
 * @version 1.0
 * @since 1.32
 */
public interface EniwareEdgeMetadataFilter extends Filter {

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
