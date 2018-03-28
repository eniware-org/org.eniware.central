/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import java.util.List;

import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserNodeConfirmation;

/**
 * DAO API for UserNodeConfirmation entities.
 * 
 * @version $Revision$
 */
public interface UserNodeConfirmationDao extends GenericDao<UserNodeConfirmation, Long> {

	/**
	 * Find a list of all pending UserNodeConfirmation objects for a particular
	 * user.
	 * 
	 * @param user
	 *        the user to get all pending confirmations for
	 * @return list of {@link UserNodeConfirmation} objects, or an empty list if
	 *         none found
	 */
	List<UserNodeConfirmation> findPendingConfirmationsForUser(User user);

	/**
	 * Get a confirmation object for a given user ID and key.
	 * 
	 * @param userId
	 *        the user ID
	 * @param key
	 *        the confirmation key
	 * @return the found UserNodeConfirmation, or <em>null</em> if not found
	 */
	UserNodeConfirmation getConfirmationForKey(Long userId, String key);

}
