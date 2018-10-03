/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.biz;

import java.util.List;
import java.util.Set;

import org.eniware.central.domain.EniwareEdge;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityPolicy;
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
 * API for registered user tasks.
 * 
 * @version 1.5
 */
public interface UserBiz {

	/**
	 * Get a User object by its ID.
	 * 
	 * @param id
	 *        the ID of the User to get
	 * @return the User, or <em>null</em> if not found
	 */
	User getUser(Long id) throws AuthorizationException;

	/**
	 * Get a list of Edges belonging to a specific user.
	 * 
	 * Archived Edges will not be returned (see
	 * {@link #getArchivedUserEdges(Long)} for that).
	 * 
	 * @param userId
	 *        the ID of the user to get the Edges for
	 * @return list of UserEdge objects, or an empty list if none found
	 */
	List<UserEdge> getUserEdges(Long userId) throws AuthorizationException;

	/**
	 * Get a specific Edge belonging to a specific user.
	 * 
	 * @param userId
	 *        the ID of the user to get the Edge for
	 * @param EdgeId
	 *        the ID of the Edge to get
	 * @return the matching UserEdge object
	 * @throws AuthorizationException
	 *         if the user is not authorized to access the given Edge
	 */
	UserEdge getUserEdge(Long userId, Long EdgeId) throws AuthorizationException;

	/**
	 * Update a specific Edge belonging to a specific user.
	 * 
	 * <p>
	 * The {@link EniwareEdge#getId()} and {@link User#getId()} values are
	 * expected to be set on the entry object.
	 * </p>
	 * 
	 * @param userEdgeEntry
	 *        the UserEdge data to save
	 * @return the updated UserEdge object
	 * @throws AuthorizationException
	 *         if the user is not authorized to access the given Edge
	 */
	UserEdge saveUserEdge(UserEdge userEdgeEntry) throws AuthorizationException;

	/**
	 * Archive, or un-archive a user Edge.
	 * 
	 * An archived Edge will not be returned from {@link #getUserEdges(Long)}.
	 * Its data will remain intact and the Edge can be un-archived at a future
	 * date.
	 * 
	 * @param userId
	 *        the ID of the user to update the Edge for
	 * @param EdgeIds
	 *        the IDs of the Edges to update
	 * @param boolean
	 *        {@code true} to archive the Edges, {@code false} to un-archive
	 * @throws AuthorizationException
	 *         if the user is not authorized to access a given Edge
	 * @since 1.4
	 */
	void updateUserEdgeArchivedStatus(Long userId, Long[] EdgeIds, boolean archived)
			throws AuthorizationException;

	/**
	 * Get a list of archived Edges belonging to a specific user.
	 * 
	 * @param userId
	 *        the ID of the user to get the Edges for
	 * @return list of UserEdge objects, or an empty list if none found
	 * @since 1.4
	 */
	List<UserEdge> getArchivedUserEdges(Long userId) throws AuthorizationException;

	/**
	 * Get a list of pending Edge confirmations belonging to a specific user.
	 * 
	 * @param user
	 *        the user to get the Edges for
	 * @return list of UserEdgeConfirmation objects, or an empty list if none
	 *         found
	 */
	List<UserEdgeConfirmation> getPendingUserEdgeConfirmations(Long userId);

	/**
	 * Get a specific pending confirmation.
	 * 
	 * @param userEdgeConfirmationId
	 *        the ID of the pending confirmation
	 * @return the pending confirmation, or <em>null</em> if not found
	 */
	UserEdgeConfirmation getPendingUserEdgeConfirmation(Long userEdgeConfirmationId);

	/**
	 * Get a specific UserEdgeCertificate object.
	 * 
	 * @param userId
	 *        the user ID
	 * @param EdgeId
	 *        the Edge ID
	 * @return the certificate, or <em>null</em> if not available
	 */
	UserEdgeCertificate getUserEdgeCertificate(Long userId, Long EdgeId);

	/**
	 * Generate a new, unique {@link UserAuthToken} entity and return it.
	 * 
	 * @param userId
	 *        the user ID to generate the token for
	 * @param type
	 *        the type of token to create
	 * @param EdgeIds
	 *        an optional set of Edge IDs to include with the token
	 * @return the generated token
	 * @deprecated use
	 *             {@link #generateUserAuthToken(Long, UserAuthTokenType, SecurityPolicy)}
	 *             with Edge IDs applied
	 */
	@Deprecated
	UserAuthToken generateUserAuthToken(Long userId, UserAuthTokenType type, Set<Long> EdgeIds);

	/**
	 * Generate a new, unique {@link UserAuthToken} entity and return it.
	 * 
	 * @param userId
	 *        the user ID to generate the token for
	 * @param type
	 *        the type of token to create
	 * @param policy
	 *        an optional policy to attach, or {@code null} for no restrictions
	 * @return the generated token
	 * @since 1.3
	 */
	UserAuthToken generateUserAuthToken(Long userId, UserAuthTokenType type, SecurityPolicy policy);

	/**
	 * Get all {@link UserAuthToken} entities for a given user.
	 * 
	 * @param userId
	 *        the ID to get the tokens for
	 * @return the tokens, or an empty list if none available
	 */
	List<UserAuthToken> getAllUserAuthTokens(Long userId);

	/**
	 * Delete a user auth token.
	 * 
	 * @param userId
	 *        the user ID
	 * @param tokenId
	 *        the UserAuthToken ID to delete
	 */
	void deleteUserAuthToken(Long userId, String tokenId);

	/**
	 * Update the status of a UserAuthToken.
	 * 
	 * @param userId
	 *        the user ID
	 * @param tokenId
	 *        the UserAuthToken ID to delete
	 * @param newStatus
	 *        the desired status
	 * @return the updated token
	 */
	UserAuthToken updateUserAuthTokenStatus(Long userId, String tokenId, UserAuthTokenStatus newStatus);

	/**
	 * Update the policy of a UserAuthToken.
	 * 
	 * @param userId
	 *        the user ID
	 * @param tokenId
	 *        the UserAuthToken ID to delete
	 * @param newPolicy
	 *        the new policy to apply
	 * @param replace
	 *        {@code true} to replace the token's policy with the provided one,
	 *        or {@code false} to merge the provided policy properties into the
	 *        existing policy
	 * @return the updated token
	 */
	UserAuthToken updateUserAuthTokenPolicy(Long userId, String tokenId, SecurityPolicy newPolicy,
			boolean replace);

	/**
	 * Create an authorization builder object with a populated signing key for a
	 * specific token.
	 * 
	 * <p>
	 * Use this method to create a new builder with a signing key populated for
	 * generating signed SNWS2 {@code Authorization} HTTP header values.
	 * </p>
	 * 
	 * @param userId
	 *        the user ID
	 * @param tokenId
	 *        the UserAuthToken ID to use
	 * @param signingDate
	 *        the date to generate the signing key with
	 * @return the builder
	 * @since 1.5
	 */
	AuthorizationV2Builder createAuthorizationV2Builder(Long userId, String tokenId,
			DateTime signingDate);
}
