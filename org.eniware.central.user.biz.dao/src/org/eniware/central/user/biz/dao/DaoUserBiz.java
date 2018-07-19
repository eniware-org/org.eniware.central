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
	private UserEdgeDao userEdgeDao;
	private UserEdgeConfirmationDao userEdgeConfirmationDao;
	private UserEdgeCertificateDao userEdgeCertificateDao;
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
	public List<UserEdge> getUserEdges(Long userId) {
		return userEdgeDao.findUserEdgesAndCertificatesForUser(userId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdge getUserEdge(Long userId, Long EdgeId) throws AuthorizationException {
		assert userId != null;
		assert EdgeId != null;
		UserEdge result = userEdgeDao.get(EdgeId);
		if ( result == null ) {
			throw new AuthorizationException(EdgeId.toString(), Reason.UNKNOWN_OBJECT);
		}
		if ( result.getUser().getId().equals(userId) == false ) {
			throw new AuthorizationException(Reason.ACCESS_DENIED, EdgeId);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserEdge saveUserEdge(UserEdge entry) throws AuthorizationException {
		assert entry != null;
		assert entry.getEdge() != null;
		assert entry.getUser() != null;
		if ( entry.getEdge().getId() == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_OBJECT, null);
		}
		if ( entry.getUser().getId() == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_OBJECT, null);
		}
		UserEdge entity = userEdgeDao.get(entry.getEdge().getId());
		if ( entity == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_OBJECT, entry.getEdge().getId());
		}
		if ( entry.getName() != null ) {
			entity.setName(entry.getName());
		}
		if ( entry.getDescription() != null ) {
			entity.setDescription(entry.getDescription());
		}
		entity.setRequiresAuthorization(entry.isRequiresAuthorization());

		// Maintain the Edge's location as well; see if the location matches exactly one in the DB,
		// and if so assign that location (if not already assigned). If no location matches, create
		// a new location and assign that.
		if ( entry.getEdgeLocation() != null ) {
			EniwareEdge Edge = entity.getEdge();
			EniwareLocation norm = EniwareLocation.normalizedLocation(entry.getEdgeLocation());
			EniwareLocation locEntity = eniwareLocationDao.getEniwareLocationForLocation(norm);
			if ( locEntity == null ) {
				log.debug("Saving new EniwareLocation {}", locEntity);
				locEntity = eniwareLocationDao.get(eniwareLocationDao.store(norm));
			}
			if ( locEntity.getId().equals(Edge.getLocationId()) == false ) {
				log.debug("Updating Edge {} location from {} to {}", Edge.getId(), Edge.getLocationId(),
						locEntity.getId());
				Edge.setLocationId(locEntity.getId());
				eniwareEdgeDao.store(Edge);
			}
		}

		userEdgeDao.store(entity);

		return entity;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateUserEdgeArchivedStatus(Long userId, Long[] EdgeIds, boolean archived)
			throws AuthorizationException {
		userEdgeDao.updateUserEdgeArchivedStatus(userId, EdgeIds, archived);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdge> getArchivedUserEdges(Long userId) throws AuthorizationException {
		return userEdgeDao.findArchivedUserEdgesForUser(userId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserEdgeConfirmation> getPendingUserEdgeConfirmations(Long userId) {
		User user = userDao.get(userId);
		return userEdgeConfirmationDao.findPendingConfirmationsForUser(user);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeConfirmation getPendingUserEdgeConfirmation(final Long userEdgeConfirmationId) {
		return userEdgeConfirmationDao.get(userEdgeConfirmationId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeCertificate getUserEdgeCertificate(Long userId, Long EdgeId) {
		assert userId != null;
		assert EdgeId != null;
		return userEdgeCertificateDao.get(new UserEdgePK(userId, EdgeId));
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserAuthToken generateUserAuthToken(final Long userId, final UserAuthTokenType type,
			final Set<Long> EdgeIds) {
		BasicSecurityPolicy policy = new BasicSecurityPolicy.Builder().withEdgeIds(EdgeIds).build();
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

				// verify user account has access to requested Edge IDs
				Set<Long> EdgeIds = (policy == null ? null : policy.getEdgeIds());
				if ( EdgeIds != null ) {
					for ( Long EdgeId : EdgeIds ) {
						UserEdge userEdge = userEdgeDao.get(EdgeId);
						if ( userEdge == null ) {
							throw new AuthorizationException(Reason.UNKNOWN_OBJECT, EdgeId);
						}
						if ( userEdge.getUser().getId().equals(userId) == false ) {
							throw new AuthorizationException(Reason.ACCESS_DENIED, EdgeId);
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
	public List<UserEdgeTransfer> pendingEdgeOwnershipTransfersForEmail(String email) {
		return userEdgeDao.findUserEdgeTransferRequestsForEmail(email);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeTransfer getEdgeOwnershipTransfer(Long userId, Long EdgeId) {
		return userEdgeDao.getUserEdgeTransfer(new UserEdgePK(userId, EdgeId));
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void requestEdgeOwnershipTransfer(Long userId, Long EdgeId, String newOwnerEmail)
			throws AuthorizationException {
		UserEdgeTransfer xfer = new UserEdgeTransfer();
		xfer.setUserId(userId);
		xfer.setEdgeId(EdgeId);
		xfer.setEmail(newOwnerEmail);
		userEdgeDao.storeUserEdgeTransfer(xfer);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void cancelEdgeOwnershipTransfer(Long userId, Long EdgeId) throws AuthorizationException {
		UserEdgeTransfer xfer = userEdgeDao.getUserEdgeTransfer(new UserEdgePK(userId, EdgeId));
		if ( xfer != null ) {
			userEdgeDao.deleteUserEdgeTrasnfer(xfer);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserEdgeTransfer confirmEdgeOwnershipTransfer(Long userId, Long EdgeId, boolean accept)
			throws AuthorizationException {
		UserEdgePK pk = new UserEdgePK(userId, EdgeId);
		UserEdgeTransfer xfer = userEdgeDao.getUserEdgeTransfer(pk);
		if ( accept ) {
			if ( xfer == null ) {
				throw new AuthorizationException(Reason.UNKNOWN_OBJECT, pk);
			}
			UserEdge userEdge = userEdgeDao.get(EdgeId);
			if ( userEdge == null ) {
				throw new AuthorizationException(Reason.UNKNOWN_OBJECT, EdgeId);
			}
			User recipient = userDao.getUserByEmail(xfer.getEmail());
			if ( recipient == null ) {
				throw new AuthorizationException(Reason.UNKNOWN_OBJECT, xfer.getEmail());
			}

			// at this point, we can delete the transfer request
			userEdgeDao.deleteUserEdgeTrasnfer(xfer);

			// remove any Edge alerts associated with this Edge
			int deletedAlertCount = userAlertDao.deleteAllAlertsForEdge(userId, EdgeId);
			log.debug("Deleted {} alerts associated with Edge {} for ownership transfer",
					deletedAlertCount, EdgeId);

			// clean up auth tokens associated with Edge: if token contains just this Edge id, delete it
			// but if it contains other Edge IDs, just remove this Edge ID from it
			for ( UserAuthToken token : userAuthTokenDao.findUserAuthTokensForUser(userId) ) {
				if ( token.getEdgeIds() != null && token.getEdgeIds().contains(EdgeId) ) {
					if ( token.getEdgeIds().size() == 1 ) {
						// only this Edge ID associated, so delete token
						log.debug("Deleting UserAuthToken {} for Edge ownership transfer",
								token.getId());
						userAuthTokenDao.delete(token);
					} else {
						// other Edge IDs associated, so remove the Edge ID from this token
						log.debug(
								"Removing Edge ID {} from UserAuthToken {} for Edge ownership transfer",
								EdgeId, token.getId());
						Set<Long> EdgeIds = new LinkedHashSet<Long>(token.getEdgeIds()); // get mutable set
						EdgeIds.remove(EdgeId);
						BasicSecurityPolicy.Builder secPolicyBuilder = new BasicSecurityPolicy.Builder()
								.withPolicy(token.getPolicy()).withEdgeIds(EdgeIds);
						token.setPolicy(secPolicyBuilder.build());

						userAuthTokenDao.store(token);
					}
				}
			}

			// and now, transfer ownership
			if ( recipient.getId().equals(userEdge.getUser().getId()) == false ) {
				userEdge.setUser(recipient);
				userEdgeDao.store(userEdge);
			}
		} else {
			// rejecting
			cancelEdgeOwnershipTransfer(userId, EdgeId);
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

	public void setUserEdgeDao(UserEdgeDao userEdgeDao) {
		this.userEdgeDao = userEdgeDao;
	}

	public void setUserEdgeConfirmationDao(UserEdgeConfirmationDao userEdgeConfirmationDao) {
		this.userEdgeConfirmationDao = userEdgeConfirmationDao;
	}

	public void setUserEdgeCertificateDao(UserEdgeCertificateDao userEdgeCertificateDao) {
		this.userEdgeCertificateDao = userEdgeCertificateDao;
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
