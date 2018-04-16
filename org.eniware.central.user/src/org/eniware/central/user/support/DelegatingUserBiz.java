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
import org.eniware.central.user.domain.UserNode;
import org.eniware.central.user.domain.UserNodeCertificate;
import org.eniware.central.user.domain.UserNodeConfirmation;
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
	public List<UserNode> getUserNodes(Long userId) {
		return delegate.getUserNodes(userId);
	}

	@Override
	public UserNode getUserNode(Long userId, Long nodeId) throws AuthorizationException {
		return delegate.getUserNode(userId, nodeId);
	}

	@Override
	public UserNode saveUserNode(UserNode userNodeEntry) throws AuthorizationException {
		return delegate.saveUserNode(userNodeEntry);
	}

	@Override
	public List<UserNodeConfirmation> getPendingUserNodeConfirmations(Long userId) {
		return delegate.getPendingUserNodeConfirmations(userId);
	}

	@Override
	public UserNodeConfirmation getPendingUserNodeConfirmation(Long userNodeConfirmationId) {
		return delegate.getPendingUserNodeConfirmation(userNodeConfirmationId);
	}

	@Override
	public UserNodeCertificate getUserNodeCertificate(Long userId, Long nodeId) {
		return delegate.getUserNodeCertificate(userId, nodeId);
	}

	@Override
	public UserAuthToken generateUserAuthToken(Long userId, UserAuthTokenType type, Set<Long> nodeIds) {
		return delegate.generateUserAuthToken(userId, type, nodeIds);
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
	public void updateUserNodeArchivedStatus(Long userId, Long[] nodeIds, boolean archived)
			throws AuthorizationException {
		delegate.updateUserNodeArchivedStatus(userId, nodeIds, archived);
	}

	@Override
	public List<UserNode> getArchivedUserNodes(Long userId) throws AuthorizationException {
		return delegate.getArchivedUserNodes(userId);
	}

	@Override
	public AuthorizationV2Builder createAuthorizationV2Builder(Long userId, String tokenId,
			DateTime signingDate) {
		return delegate.createAuthorizationV2Builder(userId, tokenId, signingDate);
	}

}
