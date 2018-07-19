/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.support;

import java.util.List;
import java.util.Set;

import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityPolicy;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserAuthToken;
import org.eniware.central.user.domain.UserAuthTokenStatus;
import org.eniware.central.user.domain.UserAuthTokenType;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.domain.UserEdgeCertificate;
import org.eniware.central.user.domain.UserEdgeConfirmation;
import org.joda.time.DateTime;

import org.eniware.web.security.AuthorizationV2Builder;

/**
 * Delegating implementation of {@link UserBiz}, mostly to help with AOP.
 * 
 * @version 1.5
 */
@SuppressWarnings("deprecation")
public class DelegatingUserBiz implements UserBiz {

	private final UserBiz delegate;

	/**
	 * Construct with a delegate;
	 * 
	 * @param delegate
	 *        the delegate
	 */
	public DelegatingUserBiz(UserBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public User getUser(Long id) {
		return delegate.getUser(id);
	}

	@Override
	public List<UserEdge> getUserEdges(Long userId) {
		return delegate.getUserEdges(userId);
	}

	@Override
	public UserEdge getUserEdge(Long userId, Long EdgeId) throws AuthorizationException {
		return delegate.getUserEdge(userId, EdgeId);
	}

	@Override
	public UserEdge saveUserEdge(UserEdge userEdgeEntry) throws AuthorizationException {
		return delegate.saveUserEdge(userEdgeEntry);
	}

	@Override
	public List<UserEdgeConfirmation> getPendingUserEdgeConfirmations(Long userId) {
		return delegate.getPendingUserEdgeConfirmations(userId);
	}

	@Override
	public UserEdgeConfirmation getPendingUserEdgeConfirmation(Long userEdgeConfirmationId) {
		return delegate.getPendingUserEdgeConfirmation(userEdgeConfirmationId);
	}

	@Override
	public UserEdgeCertificate getUserEdgeCertificate(Long userId, Long EdgeId) {
		return delegate.getUserEdgeCertificate(userId, EdgeId);
	}

	@Override
	public UserAuthToken generateUserAuthToken(Long userId, UserAuthTokenType type, Set<Long> EdgeIds) {
		return delegate.generateUserAuthToken(userId, type, EdgeIds);
	}

	@Override
	public UserAuthToken generateUserAuthToken(Long userId, UserAuthTokenType type,
			SecurityPolicy policy) {
		return delegate.generateUserAuthToken(userId, type, policy);
	}

	@Override
	public List<UserAuthToken> getAllUserAuthTokens(Long userId) {
		return delegate.getAllUserAuthTokens(userId);
	}

	@Override
	public void deleteUserAuthToken(Long userId, String tokenId) {
		delegate.deleteUserAuthToken(userId, tokenId);
	}

	@Override
	public UserAuthToken updateUserAuthTokenStatus(Long userId, String tokenId,
			UserAuthTokenStatus newStatus) {
		return delegate.updateUserAuthTokenStatus(userId, tokenId, newStatus);
	}

	@Override
	public UserAuthToken updateUserAuthTokenPolicy(Long userId, String tokenId, SecurityPolicy newPolicy,
			boolean replace) {
		return delegate.updateUserAuthTokenPolicy(userId, tokenId, newPolicy, replace);
	}

	@Override
	public void updateUserEdgeArchivedStatus(Long userId, Long[] EdgeIds, boolean archived)
			throws AuthorizationException {
		delegate.updateUserEdgeArchivedStatus(userId, EdgeIds, archived);
	}

	@Override
	public List<UserEdge> getArchivedUserEdges(Long userId) throws AuthorizationException {
		return delegate.getArchivedUserEdges(userId);
	}

	@Override
	public AuthorizationV2Builder createAuthorizationV2Builder(Long userId, String tokenId,
			DateTime signingDate) {
		return delegate.createAuthorizationV2Builder(userId, tokenId, signingDate);
	}

}
