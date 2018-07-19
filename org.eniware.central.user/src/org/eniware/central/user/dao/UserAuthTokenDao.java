/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import java.util.List;

import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.UserAuthToken;
import org.joda.time.DateTime;

import org.eniware.web.security.AuthorizationV2Builder;

/**
 * DAO API for {@link UserAuthToken} entities.
 * 
 * @version 1.1
 */
public interface UserAuthTokenDao extends GenericDao<UserAuthToken, String> {

	/**
	 * Find a list of all UserEdgeAuthToken objects for a particular user.
	 * 
	 * @param user
	 *        the user ID to get all tokens for
	 * @return list of {@link UserAuthToken} objects, or an empty list if none
	 *         found
	 */
	List<UserAuthToken> findUserAuthTokensForUser(Long userId);

	/**
	 * Create a new {@link AuthorizationV2Builder} for a given token.
	 * 
	 * <p>
	 * The returned builder will have a signing key populated.
	 * </p>
	 * 
	 * @param tokenId
	 *        the token ID to get a builder for
	 * @param signingDate
	 *        the date to use in the signing key
	 * @return the builder, or {@literal null} if the given {@code tokenId} is
	 *         not found
	 */
	public AuthorizationV2Builder createAuthorizationV2Builder(String tokenId, DateTime signingDate);

}
