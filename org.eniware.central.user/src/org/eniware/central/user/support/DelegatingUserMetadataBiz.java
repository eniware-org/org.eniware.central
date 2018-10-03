/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.support;

import java.util.List;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SortDescriptor;
import org.eniware.central.user.biz.UserMetadataBiz;
import org.eniware.central.user.domain.UserMetadataFilter;
import org.eniware.central.user.domain.UserMetadataFilterMatch;
import org.eniware.domain.GeneralDatumMetadata;

/**
 * Delegating implementation of {@link UserMetadataBiz}, mostly to help with
 * AOP.
 * 
 * @version 1.0
 * @since 1.23
 */
public class DelegatingUserMetadataBiz implements UserMetadataBiz {

	private final UserMetadataBiz delegate;

	/**
	 * Constructor.
	 * 
	 * @param delegate
	 *        The delegate to use.
	 */
	public DelegatingUserMetadataBiz(UserMetadataBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void addUserMetadata(Long userId, GeneralDatumMetadata meta) {
		delegate.addUserMetadata(userId, meta);
	}

	@Override
	public void storeUserMetadata(Long userId, GeneralDatumMetadata meta) {
		delegate.storeUserMetadata(userId, meta);
	}

	@Override
	public void removeUserMetadata(Long userId) {
		delegate.removeUserMetadata(userId);
	}

	@Override
	public FilterResults<UserMetadataFilterMatch> findUserMetadata(UserMetadataFilter criteria,
			List<SortDescriptor> sortDescriptors, Integer offset, Integer max) {
		return delegate.findUserMetadata(criteria, sortDescriptors, offset, max);
	}

}
