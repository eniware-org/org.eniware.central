/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.central.domain.FilterMatch;

/**
 * API for a {@link UserMetadataEntity} search or filter match result.
 * 
 * @author matt
 * @version 1.0
 * @since 1.23
 */
public interface UserMetadataFilterMatch extends UserMetadata, FilterMatch<Long> {

}
