/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import java.util.Map;
import java.util.Set;

import org.eniware.central.dao.FilterableDao;
import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserFilter;
import org.eniware.central.user.domain.UserFilterMatch;

/**
 * DAO API for User objects.
 * 
 * @version 1.1
 */
public interface UserDao
		extends GenericDao<User, Long>, FilterableDao<UserFilterMatch, Long, UserFilter> {

	/**
	 * Get a user by their email.
	 * 
	 * @param email
	 *        the email address to lookup
	 * @return the found User, or {@literal null} if not found
	 */
	User getUserByEmail(String email);

	/**
	 * Get the set of roles a user belongs to.
	 * 
	 * <p>
	 * Roles represent granted user authorizations.
	 * </p>
	 * 
	 * @return the user roles
	 */
	Set<String> getUserRoles(User user);

	/**
	 * Store the set of roles a user belongs to.
	 * 
	 * <p>
	 * This will completely replace any existing roles the user may already
	 * belong to.
	 * </p>
	 * 
	 * @param user
	 *        the user to store the roles for
	 * @param roles
	 *        the set of roles
	 */
	void storeUserRoles(User user, Set<String> roles);

	/**
	 * Get internal data for a user.
	 * 
	 * @param userId
	 *        the ID of the user to get
	 * @return the internal data, or {@literal null} if none available
	 */
	Map<String, Object> getInternalData(Long userId);

	/**
	 * Add, update, or remove properties from the internal data of a user.
	 * 
	 * <p>
	 * To remove properties, pass in {@literal null} values.
	 * </p>
	 * 
	 * @param userId
	 *        the ID if the user to update
	 * @param data
	 *        the properties to add, update, or remove
	 * @since 1.1
	 */
	void storeInternalData(Long userId, Map<String, Object> data);

}
