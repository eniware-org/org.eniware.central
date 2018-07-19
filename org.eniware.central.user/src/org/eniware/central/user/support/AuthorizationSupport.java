/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityActor;
import org.eniware.central.security.SecurityException;
import org.eniware.central.security.SecurityNode;
import org.eniware.central.security.SecurityPolicy;
import org.eniware.central.security.SecurityPolicyEnforcer;
import org.eniware.central.security.SecurityPolicyMetadataType;
import org.eniware.central.security.SecurityToken;
import org.eniware.central.security.SecurityUser;
import org.eniware.central.security.SecurityUtils;
import org.eniware.central.support.BasicFilterResults;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.domain.UserAuthTokenType;
import org.eniware.central.user.domain.UserEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.PathMatcher;

/**
 * Helper class for authorization needs, e.g. aspect implementations.
 * 
 * @version 1.4
 */
public abstract class AuthorizationSupport {

	private final UserEdgeDao userNodeDao;
	private PathMatcher pathMatcher;

	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Constructor.
	 * 
	 * @param userNodeDao
	 *        the UserNodeDao to use
	 */
	public AuthorizationSupport(UserEdgeDao userNodeDao) {
		super();
		this.userNodeDao = userNodeDao;
	}

	/**
	 * Get the {@link UserEdgeDao}.
	 * 
	 * @return The {@link UserEdgeDao}.
	 * @since 1.1
	 */
	protected UserEdgeDao getUserNodeDao() {
		return userNodeDao;
	}

	/**
	 * Require the active user have "write" access to a given node ID. If the
	 * active user is not authorized, a {@link AuthorizationException} will be
	 * thrown.
	 * 
	 * @param nodeId
	 *        the node ID to check
	 * @throws AuthorizationException
	 *         if the authorization check fails
	 */
	protected void requireNodeWriteAccess(Long nodeId) {
		UserEdge userNode = (nodeId == null ? null : userNodeDao.get(nodeId));
		if ( userNode == null ) {
			log.warn("Access DENIED to node {}; not found", nodeId);
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, nodeId);
		}

		final SecurityActor actor;
		try {
			actor = SecurityUtils.getCurrentActor();
		} catch ( SecurityException e ) {
			log.warn("Access DENIED to node {} for non-authenticated user", nodeId);
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, nodeId);
		}

		// node requires authentication
		if ( actor instanceof SecurityNode ) {
			SecurityNode node = (SecurityNode) actor;
			if ( !nodeId.equals(node.getNodeId()) ) {
				log.warn("Access DENIED to node {} for node {}; wrong node", nodeId, node.getNodeId());
				throw new AuthorizationException(node.getNodeId().toString(),
						AuthorizationException.Reason.ACCESS_DENIED);
			}
			return;
		}

		if ( actor instanceof SecurityUser ) {
			SecurityUser user = (SecurityUser) actor;
			if ( !user.getUserId().equals(userNode.getUser().getId()) ) {
				log.warn("Access DENIED to node {} for user {}; wrong user", nodeId, user.getEmail());
				throw new AuthorizationException(user.getEmail(),
						AuthorizationException.Reason.ACCESS_DENIED);
			}
			return;
		}

		if ( actor instanceof SecurityToken ) {
			SecurityToken token = (SecurityToken) actor;
			if ( UserAuthTokenType.User.toString().equals(token.getTokenType()) ) {
				// user token, so user ID must match node user's ID
				if ( !token.getUserId().equals(userNode.getUser().getId()) ) {
					log.warn("Access DENIED to node {} for token {}; wrong user", nodeId,
							token.getToken());
					throw new AuthorizationException(token.getToken(),
							AuthorizationException.Reason.ACCESS_DENIED);
				}
				return;
			}
		}

		log.warn("Access DENIED to node {} for actor {}", nodeId, actor);
		throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, nodeId);
	}

	/**
	 * Require the active user have "read" access to a given node ID. If the
	 * active user is not authorized, a {@link AuthorizationException} will be
	 * thrown.
	 * 
	 * @param nodeId
	 *        the node ID to check
	 * @throws AuthorizationException
	 *         if the authorization check fails
	 */
	protected void requireNodeReadAccess(Long nodeId) {
		UserEdge userNode = (nodeId == null ? null : userNodeDao.get(nodeId));
		if ( userNode == null ) {
			log.warn("Access DENIED to node {}; not found", nodeId);
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, nodeId);
		}
		if ( !userNode.isRequiresAuthorization() ) {
			return;
		}

		final SecurityActor actor;
		try {
			actor = SecurityUtils.getCurrentActor();
		} catch ( SecurityException e ) {
			log.warn("Access DENIED to node {} for non-authenticated user", nodeId);
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, nodeId);
		}

		// node requires authentication
		if ( actor instanceof SecurityNode ) {
			SecurityNode node = (SecurityNode) actor;
			if ( !nodeId.equals(node.getNodeId()) ) {
				log.warn("Access DENIED to node {} for node {}; wrong node", nodeId, node.getNodeId());
				throw new AuthorizationException(node.getNodeId().toString(),
						AuthorizationException.Reason.ACCESS_DENIED);
			}
			return;
		}

		if ( actor instanceof SecurityUser ) {
			SecurityUser user = (SecurityUser) actor;
			if ( !user.getUserId().equals(userNode.getUser().getId()) ) {
				log.warn("Access DENIED to node {} for user {}; wrong user", nodeId, user.getEmail());
				throw new AuthorizationException(user.getEmail(),
						AuthorizationException.Reason.ACCESS_DENIED);
			}
			return;
		}

		if ( actor instanceof SecurityToken ) {
			SecurityToken token = (SecurityToken) actor;
			if ( UserAuthTokenType.User.toString().equals(token.getTokenType()) ) {
				// user token, so user ID must match node user's ID
				if ( !token.getUserId().equals(userNode.getUser().getId()) ) {
					log.warn("Access DENIED to node {} for token {}; wrong user", nodeId,
							token.getToken());
					throw new AuthorizationException(token.getToken(),
							AuthorizationException.Reason.ACCESS_DENIED);
				}
				return;
			}
			if ( UserAuthTokenType.ReadNodeData.toString().equals(token.getTokenType()) ) {
				// data token, so token must include the requested node ID
				if ( token.getPolicy() == null || token.getPolicy().getNodeIds() == null
						|| !token.getPolicy().getNodeIds().contains(nodeId) ) {
					log.warn("Access DENIED to node {} for token {}; node not included", nodeId,
							token.getToken());
					throw new AuthorizationException(token.getToken(),
							AuthorizationException.Reason.ACCESS_DENIED);
				}
				return;
			}
		}

		log.warn("Access DENIED to node {} for actor {}", nodeId, actor);
		throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, nodeId);
	}

	/**
	 * Require the active user have "write" access to a given user ID. If the
	 * active user is not authorized, a {@link AuthorizationException} will be
	 * thrown.
	 * 
	 * @param userId
	 *        the user ID to check
	 * @throws AuthorizationException
	 *         if the authorization check fails
	 * @since 1.1
	 */
	protected void requireUserWriteAccess(Long userId) {
		final SecurityActor actor;
		try {
			actor = SecurityUtils.getCurrentActor();
		} catch ( SecurityException e ) {
			log.warn("Access DENIED to user {} for non-authenticated user", userId);
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, userId);
		}

		if ( actor instanceof SecurityUser ) {
			SecurityUser user = (SecurityUser) actor;
			if ( !user.getUserId().equals(userId) ) {
				log.warn("Access DENIED to user {} for user {}; wrong user", userId, user.getEmail());
				throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, userId);
			}
			return;
		}

		if ( actor instanceof SecurityToken ) {
			SecurityToken token = (SecurityToken) actor;
			if ( UserAuthTokenType.User.toString().equals(token.getTokenType()) ) {
				// user token, so user ID must match node user's ID
				if ( !token.getUserId().equals(userId) ) {
					log.warn("Access DENIED to user {} for token {}; wrong user", userId,
							token.getToken());
					throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED,
							userId);
				}
				return;
			}
		}

		log.warn("Access DENIED to user {} for actor {}", userId, actor);
		throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, userId);
	}

	/**
	 * Get a {@link SecurityPolicy} for the active user, if avaiable.
	 * 
	 * @return The active user's policy, or {@code null}.
	 * @since 1.3
	 */
	protected SecurityPolicy getActiveSecurityPolicy() {
		final SecurityActor actor;
		try {
			actor = SecurityUtils.getCurrentActor();
		} catch ( SecurityException e ) {
			return null;
		}

		if ( actor instanceof SecurityToken ) {
			SecurityToken token = (SecurityToken) actor;
			return token.getPolicy();
		}

		return null;
	}

	/**
	 * Require the active user have "read" access to a given user ID. If the
	 * active user is not authorized, a {@link AuthorizationException} will be
	 * thrown.
	 * 
	 * @param userId
	 *        the user ID to check
	 * @throws AuthorizationException
	 *         if the authorization check fails
	 * @since 1.1
	 */
	protected void requireUserReadAccess(Long userId) {
		final SecurityActor actor;
		try {
			actor = SecurityUtils.getCurrentActor();
		} catch ( SecurityException e ) {
			log.warn("Access DENIED to user {} for non-authenticated user", userId);
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, userId);
		}

		// node requires authentication
		if ( actor instanceof SecurityNode ) {
			SecurityNode node = (SecurityNode) actor;
			UserEdge userNode = (node.getNodeId() == null ? null : userNodeDao.get(node.getNodeId()));
			if ( userNode == null ) {
				log.warn("Access DENIED to user {} for node {}; not found", userId, node.getNodeId());
				throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, userId);
			}
			if ( !userId.equals(userNode.getUser().getId()) ) {
				log.warn("Access DENIED to user {} for node {}; wrong node", userId, node.getNodeId());
				throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, userId);
			}
			return;
		}

		if ( actor instanceof SecurityUser ) {
			SecurityUser user = (SecurityUser) actor;
			if ( !user.getUserId().equals(userId) ) {
				log.warn("Access DENIED to user {} for user {}; wrong user", userId, user.getEmail());
				throw new AuthorizationException(user.getEmail(),
						AuthorizationException.Reason.ACCESS_DENIED);
			}
			return;
		}

		if ( actor instanceof SecurityToken ) {
			SecurityToken token = (SecurityToken) actor;
			// user token, so user ID must match token owner's ID
			if ( !token.getUserId().equals(userId) ) {
				log.warn("Access DENIED to user {} for token {}; wrong user", userId, token.getToken());
				throw new AuthorizationException(token.getToken(),
						AuthorizationException.Reason.ACCESS_DENIED);
			}
			if ( UserAuthTokenType.ReadNodeData.toString().equals(token.getTokenType()) ) {
				// data token, the token must include a user metadata policy that can be enforced
				if ( token.getPolicy() == null || token.getPolicy().getUserMetadataPaths() == null
						|| token.getPolicy().getUserMetadataPaths().isEmpty() ) {
					log.warn(
							"Access DENIED to user {} for token {}; user metadata not included in policy",
							userId, token.getToken());
					throw new AuthorizationException(token.getToken(),
							AuthorizationException.Reason.ACCESS_DENIED);
				}
			}
			return;
		}

		log.warn("Access DENIED to user {} for actor {}", userId, actor);
		throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, userId);
	}

	/**
	 * Enforce a security policy on a domain object and
	 * {@code SecurityPolicyMetadataType#Node} metadata type.
	 * 
	 * @param domainObject
	 *        The domain object to enforce the active policy on.
	 * @return The domain object to use.
	 * @throws AuthorizationException
	 *         If the policy check fails.
	 * @since 1.4
	 */
	protected <T> T policyEnforcerCheck(T domainObject) {
		return policyEnforcerCheck(domainObject, SecurityPolicyMetadataType.Node);
	}

	/**
	 * Enforce a security policy on a domain object or collection of domain
	 * objects.
	 * 
	 * The {@link FilterResults} API is supported, as is {@link List}.
	 * 
	 * @param domainObject
	 *        The domain object to enforce the active policy on.
	 * @param metadataType
	 *        The metadata type to enforce the active policy on.
	 * @return The domain object to use.
	 * @throws AuthorizationException
	 *         If the policy check fails.
	 * @since 1.4
	 */
	protected <T> T policyEnforcerCheck(T domainObject, SecurityPolicyMetadataType metadataType) {
		Authentication authentication = SecurityUtils.getCurrentAuthentication();
		SecurityPolicy policy = getActiveSecurityPolicy();
		if ( policy == null || domainObject == null ) {
			return domainObject;
		}

		final Object principal = (authentication != null ? authentication.getPrincipal() : null);

		if ( domainObject instanceof FilterResults ) {
			FilterResults<?> filterResults = (FilterResults<?>) domainObject;
			Collection<Object> filteredObjects = policyEnforcedCollection(filterResults, policy,
					principal, metadataType);
			@SuppressWarnings("unchecked")
			T result = (T) new BasicFilterResults<Object>(filteredObjects,
					filterResults.getTotalResults(), filterResults.getStartingOffset(),
					filterResults.getReturnedResultCount());
			return result;
		} else if ( domainObject instanceof List ) {
			List<?> collectionResults = (List<?>) domainObject;
			@SuppressWarnings("unchecked")
			T filteredObjects = (T) policyEnforcedCollection(collectionResults, policy, principal,
					metadataType);
			return filteredObjects;
		}

		SecurityPolicyEnforcer enforcer = new SecurityPolicyEnforcer(policy,
				(authentication != null ? authentication.getPrincipal() : null), domainObject,
				pathMatcher, metadataType);
		enforcer.verify();
		return SecurityPolicyEnforcer.createSecurityPolicyProxy(enforcer);
	}

	private Collection<Object> policyEnforcedCollection(Iterable<?> input, SecurityPolicy policy,
			Object principal, SecurityPolicyMetadataType metadataType) {
		if ( input == null ) {
			return null;
		}
		List<Object> enforced = new ArrayList<Object>();
		for ( Object obj : input ) {
			SecurityPolicyEnforcer enforcer = new SecurityPolicyEnforcer(policy, principal, obj,
					pathMatcher, metadataType);
			enforcer.verify();
			enforced.add(SecurityPolicyEnforcer.createSecurityPolicyProxy(enforcer));
		}
		return enforced;
	}

	/**
	 * Get the path matcher to use.
	 * 
	 * @return the path matcher
	 * @since 1.4
	 */
	public PathMatcher getPathMatcher() {
		return pathMatcher;
	}

	/**
	 * Set the path matcher to use.
	 * 
	 * @param pathMatcher
	 *        the matcher to use
	 * @since 1.4
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

}
