/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */
package org.eniware.central.user.biz.dao;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eniware.central.dao.EniwareLocationDao;
import org.eniware.central.dao.EniwareEdgeDao;
import org.eniware.central.domain.EniwareLocation;
import org.eniware.central.domain.EniwareEdge;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.BasicSecurityPolicy;
import org.eniware.central.security.SecurityPolicy;
import org.eniware.central.security.AuthorizationException.Reason;
import org.eniware.central.user.biz.EdgeOwnershipBiz;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.dao.UserAlertDao;
import org.eniware.central.user.dao.UserAuthTokenDao;
import org.eniware.central.user.dao.UserDao;
import org.eniware.central.user.dao.UserEdgeCertificateDao;
import org.eniware.central.user.dao.UserEdgeConfirmationDao;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserAuthToken;
import org.eniware.central.user.domain.UserAuthTokenStatus;
import org.eniware.central.user.domain.UserAuthTokenType;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.domain.UserEdgeCertificate;
import org.eniware.central.user.domain.UserEdgeConfirmation;
import org.eniware.central.user.domain.UserEdgePK;
import org.eniware.central.user.domain.UserEdgeTransfer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.eniware.web.security.AuthorizationV2Builder;

/**
 * DAO-based implementation of {@link UserBiz}.
 * 
 * @version 1.3
 */
public class DaoUserBiz implements UserBiz, EdgeOwnershipBiz {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private UserDao userDao;
	private UserAlertDao userAlertDao;
	private UserEdgeDao userNodeDao;
	private UserEdgeConfirmationDao userNodeConfirmationDao;
	private UserEdgeCertificateDao userNodeCertificateDao;
	private UserAuthTokenDao userAuthTokenDao;
	private EniwareLocationDao eniwareLocationDao;
	private EniwareEdgeDao eniwareEdgeDao;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public User getUser(Long id) {
		return userDao.get(id);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdge> getUserNodes(Long userId) {
		return userNodeDao.findUserNodesAndCertificatesForUser(userId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdge getUserNode(Long userId, Long nodeId) throws AuthorizationException {
		assert userId != null;
		assert nodeId != null;
		UserEdge result = userNodeDao.get(nodeId);
		if ( result == null ) {
			throw new AuthorizationException(nodeId.toString(), Reason.UNKNOWN_OBJECT);
		}
		if ( result.getUser().getId().equals(userId) == false ) {
			throw new AuthorizationException(Reason.ACCESS_DENIED, nodeId);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserEdge saveUserNode(UserEdge entry) throws AuthorizationException {
		assert entry != null;
		assert entry.getNode() != null;
		assert entry.getUser() != null;
		if ( entry.getNode().getId() == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_OBJECT, null);
		}
		if ( entry.getUser().getId() == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_OBJECT, null);
		}
		UserEdge entity = userNodeDao.get(entry.getNode().getId());
		if ( entity == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_OBJECT, entry.getNode().getId());
		}
		if ( entry.getName() != null ) {
			entity.setName(entry.getName());
		}
		if ( entry.getDescription() != null ) {
			entity.setDescription(entry.getDescription());
		}
		entity.setRequiresAuthorization(entry.isRequiresAuthorization());

		// Maintain the node's location as well; see if the location matches exactly one in the DB,
		// and if so assign that location (if not already assigned). If no location matches, create
		// a new location and assign that.
		if ( entry.getNodeLocation() != null ) {
			EniwareEdge node = entity.getNode();
			EniwareLocation norm = EniwareLocation.normalizedLocation(entry.getNodeLocation());
			EniwareLocation locEntity = eniwareLocationDao.getEniwareLocationForLocation(norm);
			if ( locEntity == null ) {
				log.debug("Saving new EniwareLocation {}", locEntity);
				locEntity = eniwareLocationDao.get(eniwareLocationDao.store(norm));
			}
			if ( locEntity.getId().equals(node.getLocationId()) == false ) {
				log.debug("Updating node {} location from {} to {}", node.getId(), node.getLocationId(),
						locEntity.getId());
				node.setLocationId(locEntity.getId());
				eniwareEdgeDao.store(node);
			}
		}

		userNodeDao.store(entity);

		return entity;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateUserNodeArchivedStatus(Long userId, Long[] nodeIds, boolean archived)
			throws AuthorizationException {
		userNodeDao.updateUserNodeArchivedStatus(userId, nodeIds, archived);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdge> getArchivedUserNodes(Long userId) throws AuthorizationException {
		return userNodeDao.findArchivedUserNodesForUser(userId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdgeConfirmation> getPendingUserNodeConfirmations(Long userId) {
		User user = userDao.get(userId);
		return userNodeConfirmationDao.findPendingConfirmationsForUser(user);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeConfirmation getPendingUserNodeConfirmation(final Long userNodeConfirmationId) {
		return userNodeConfirmationDao.get(userNodeConfirmationId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeCertificate getUserNodeCertificate(Long userId, Long nodeId) {
		assert userId != null;
		assert nodeId != null;
		return userNodeCertificateDao.get(new UserEdgePK(userId, nodeId));
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserAuthToken generateUserAuthToken(final Long userId, final UserAuthTokenType type,
			final Set<Long> nodeIds) {
		BasicSecurityPolicy policy = new BasicSecurityPolicy.Builder().withNodeIds(nodeIds).build();
		return generateUserAuthToken(userId, type, policy);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserAuthToken generateUserAuthToken(Long userId, UserAuthTokenType type,
			SecurityPolicy policy) {
		assert userId != null;
		assert type != null;
		SecureRandom rng;
		try {
			rng = SecureRandom.getInstance("SHA1PRNG");
		} catch ( NoSuchAlgorithmException e ) {
			throw new RuntimeException("Unable to generate auth token", e);
		}
		final int randomLength = 16 + rng.nextInt(8);
		final String secretString = UserBizConstants.generateRandomToken(rng, randomLength);
		final int maxAttempts = 50;
		for ( int i = maxAttempts; i > 0; i-- ) {
			String tok = UserBizConstants.generateRandomAuthToken(rng);
			// verify token doesn't already exist
			if ( userAuthTokenDao.get(tok) == null ) {
				UserAuthToken authToken = new UserAuthToken(tok, userId, secretString, type);

				// verify user account has access to requested node IDs
				Set<Long> nodeIds = (policy == null ? null : policy.getNodeIds());
				if ( nodeIds != null ) {
					for ( Long nodeId : nodeIds ) {
						UserEdge userNode = userNodeDao.get(nodeId);
						if ( userNode == null ) {
							throw new AuthorizationException(Reason.UNKNOWN_OBJECT, nodeId);
						}
						if ( userNode.getUser().getId().equals(userId) == false ) {
							throw new AuthorizationException(Reason.ACCESS_DENIED, nodeId);
						}
					}
				}

				if ( policy != null ) {
					BasicSecurityPolicy.Builder policyBuilder = new BasicSecurityPolicy.Builder()
							.withPolicy(policy);
					authToken.setPolicy(policyBuilder.build());
				}

				userAuthTokenDao.store(authToken);
				return authToken;
			}
		}
		log.error("Failed to generate unique token after {} attempts", maxAttempts);
		throw new RuntimeException("Failed to generate unique token");
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserAuthToken> getAllUserAuthTokens(Long userId) {
		assert userId != null;
		return userAuthTokenDao.findUserAuthTokensForUser(userId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteUserAuthToken(Long userId, String tokenId) {
		assert userId != null;
		UserAuthToken token = userAuthTokenDao.get(tokenId);
		if ( token == null ) {
			return;
		}
		if ( !userId.equals(token.getUserId()) ) {
			throw new AuthorizationException(Reason.ACCESS_DENIED, tokenId);
		}
		userAuthTokenDao.delete(token);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserAuthToken updateUserAuthTokenStatus(Long userId, String tokenId,
			UserAuthTokenStatus newStatus) {
		assert userId != null;
		UserAuthToken token = userAuthTokenDao.get(tokenId);
		if ( token == null ) {
			return null;
		}
		if ( !userId.equals(token.getUserId()) ) {
			throw new AuthorizationException(Reason.ACCESS_DENIED, tokenId);
		}
		if ( token.getStatus() != newStatus ) {
			token.setStatus(newStatus);
			userAuthTokenDao.store(token);
		}
		return token;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserAuthToken updateUserAuthTokenPolicy(Long userId, String tokenId, SecurityPolicy newPolicy,
			boolean replace) {
		assert userId != null;
		UserAuthToken token = userAuthTokenDao.get(tokenId);
		if ( token == null ) {
			return null;
		}
		if ( !userId.equals(token.getUserId()) ) {
			throw new AuthorizationException(Reason.ACCESS_DENIED, tokenId);
		}
		BasicSecurityPolicy.Builder policyBuilder = new BasicSecurityPolicy.Builder();
		if ( replace ) {
			policyBuilder = policyBuilder.withPolicy(newPolicy);
		} else {
			policyBuilder = policyBuilder.withPolicy(token.getPolicy()).withMergedPolicy(newPolicy);
		}
		BasicSecurityPolicy newBasicPolicy = policyBuilder.build();
		if ( !newBasicPolicy.equals(token.getPolicy()) ) {
			token.setPolicy(newBasicPolicy);
			userAuthTokenDao.store(token);
		}
		return token;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdgeTransfer> pendingNodeOwnershipTransfersForEmail(String email) {
		return userNodeDao.findUserNodeTransferRequestsForEmail(email);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeTransfer getNodeOwnershipTransfer(Long userId, Long nodeId) {
		return userNodeDao.getUserNodeTransfer(new UserEdgePK(userId, nodeId));
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void requestNodeOwnershipTransfer(Long userId, Long nodeId, String newOwnerEmail)
			throws AuthorizationException {
		UserEdgeTransfer xfer = new UserEdgeTransfer();
		xfer.setUserId(userId);
		xfer.setNodeId(nodeId);
		xfer.setEmail(newOwnerEmail);
		userNodeDao.storeUserNodeTransfer(xfer);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void cancelNodeOwnershipTransfer(Long userId, Long nodeId) throws AuthorizationException {
		UserEdgeTransfer xfer = userNodeDao.getUserNodeTransfer(new UserEdgePK(userId, nodeId));
		if ( xfer != null ) {
			userNodeDao.deleteUserNodeTrasnfer(xfer);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserEdgeTransfer confirmNodeOwnershipTransfer(Long userId, Long nodeId, boolean accept)
			throws AuthorizationException {
		UserEdgePK pk = new UserEdgePK(userId, nodeId);
		UserEdgeTransfer xfer = userNodeDao.getUserNodeTransfer(pk);
		if ( accept ) {
			if ( xfer == null ) {
				throw new AuthorizationException(Reason.UNKNOWN_OBJECT, pk);
			}
			UserEdge userNode = userNodeDao.get(nodeId);
			if ( userNode == null ) {
				throw new AuthorizationException(Reason.UNKNOWN_OBJECT, nodeId);
			}
			User recipient = userDao.getUserByEmail(xfer.getEmail());
			if ( recipient == null ) {
				throw new AuthorizationException(Reason.UNKNOWN_OBJECT, xfer.getEmail());
			}

			// at this point, we can delete the transfer request
			userNodeDao.deleteUserNodeTrasnfer(xfer);

			// remove any node alerts associated with this node
			int deletedAlertCount = userAlertDao.deleteAllAlertsForNode(userId, nodeId);
			log.debug("Deleted {} alerts associated with node {} for ownership transfer",
					deletedAlertCount, nodeId);

			// clean up auth tokens associated with node: if token contains just this node id, delete it
			// but if it contains other node IDs, just remove this node ID from it
			for ( UserAuthToken token : userAuthTokenDao.findUserAuthTokensForUser(userId) ) {
				if ( token.getNodeIds() != null && token.getNodeIds().contains(nodeId) ) {
					if ( token.getNodeIds().size() == 1 ) {
						// only this node ID associated, so delete token
						log.debug("Deleting UserAuthToken {} for node ownership transfer",
								token.getId());
						userAuthTokenDao.delete(token);
					} else {
						// other node IDs associated, so remove the node ID from this token
						log.debug(
								"Removing node ID {} from UserAuthToken {} for node ownership transfer",
								nodeId, token.getId());
						Set<Long> nodeIds = new LinkedHashSet<Long>(token.getNodeIds()); // get mutable set
						nodeIds.remove(nodeId);
						BasicSecurityPolicy.Builder secPolicyBuilder = new BasicSecurityPolicy.Builder()
								.withPolicy(token.getPolicy()).withNodeIds(nodeIds);
						token.setPolicy(secPolicyBuilder.build());

						userAuthTokenDao.store(token);
					}
				}
			}

			// and now, transfer ownership
			if ( recipient.getId().equals(userNode.getUser().getId()) == false ) {
				userNode.setUser(recipient);
				userNodeDao.store(userNode);
			}
		} else {
			// rejecting
			cancelNodeOwnershipTransfer(userId, nodeId);
		}
		return xfer;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public AuthorizationV2Builder createAuthorizationV2Builder(Long userId, String tokenId,
			DateTime signingDate) {
		assert userId != null;
		UserAuthToken token = userAuthTokenDao.get(tokenId);
		if ( token == null ) {
			return null;
		}
		if ( !userId.equals(token.getUserId()) ) {
			throw new AuthorizationException(Reason.ACCESS_DENIED, tokenId);
		}
		return userAuthTokenDao.createAuthorizationV2Builder(tokenId, signingDate);
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setUserNodeDao(UserEdgeDao userNodeDao) {
		this.userNodeDao = userNodeDao;
	}

	public void setUserNodeConfirmationDao(UserEdgeConfirmationDao userNodeConfirmationDao) {
		this.userNodeConfirmationDao = userNodeConfirmationDao;
	}

	public void setUserNodeCertificateDao(UserEdgeCertificateDao userNodeCertificateDao) {
		this.userNodeCertificateDao = userNodeCertificateDao;
	}

	public void setUserAuthTokenDao(UserAuthTokenDao userAuthTokenDao) {
		this.userAuthTokenDao = userAuthTokenDao;
	}

	public void setEniwareLocationDao(EniwareLocationDao eniwareLocationDao) {
		this.eniwareLocationDao = eniwareLocationDao;
	}

	public void setEniwareEdgeDao(EniwareEdgeDao eniwareEdgeDao) {
		this.eniwareEdgeDao = eniwareEdgeDao;
	}

	public void setUserAlertDao(UserAlertDao userAlertDao) {
		this.userAlertDao = userAlertDao;
	}

}
