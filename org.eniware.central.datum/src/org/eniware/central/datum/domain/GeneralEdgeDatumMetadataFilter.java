/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.central.domain.Filter;

/**
 * Filter API for {@link GeneralEdgeDatumMetadata}.
 *
 * @version 1.0
 */
public interface GeneralEdgeDatumMetadataFilter extends Filter {

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
