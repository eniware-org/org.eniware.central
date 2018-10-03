/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.biz;

import java.util.List;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.user.domain.UserMetadataFilter;
import org.eniware.central.user.domain.UserMetadataFilterMatch;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * API for manipulating user metadata.
 * 
 * @version 1.0
 * @since 1.23
 */
public interface UserMetadataBiz {

	/**
	 * Add metadata to a specific user. If metadata already exists for the given
	 * user, the values will be merged such that tags are added and info values
	 * are added or updated.
	 * 
	 * @param userId
	 *        the user ID to add to
	 * @param meta
	 *        the metadata to add
	 */
	void addUserMetadata(Long userId, GeneralDatumMetadata meta);

	/**
	 * Store metadata to a specific user, replacing any existing metadata with
	 * the provided metadata.
	 * 
	 * @param userId
	 *        the user ID to add to
	 * @param meta
	 *        the metadata to store
	 */
	void storeUserMetadata(Long userId, GeneralDatumMetadata meta);

	/**
	 * Remove all metadata to a specific user.
	 * 
	 * @param userId
	 *        the user ID to remove from
	 */
	void removeUserMetadata(Long userId);

	/**
	 * Search for user metadata.
	 * 
	 * @param criteria
	 *        the search criteria
	 * @param sortDescriptors
	 *        the optional sort descriptors
	 * @param offset
	 *        an optional result offset
	 * @param max
	 *        an optional maximum number of returned results
	 * @return the results, never <em>null</em>
	 */
	FilterResults<UserMetadataFilterMatch> findUserMetadata(UserMetadataFilter criteria,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max);

}
