/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import java.util.List;

import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserNode;
import org.eniware.central.user.domain.UserNodeCertificate;
import org.eniware.central.user.domain.UserNodePK;
import org.eniware.central.user.domain.UserNodeTransfer;

/**
 * DAO API for UserNode objects.
 * 
 * @author matt
 * @version 1.3
 */
public interface UserNodeDao extends GenericDao<UserNode, Long> {

	/**
	 * Find a list of all UserNode objects for a particular user.
	 * 
	 * This will not return archived nodes (see
	 * {@link #findArchivedUserNodesForUser(Long)}).
	 * 
	 * @param user
	 *        the user to get all nodes for
	 * @return list of {@link UserNode} objects, or an empty list if none found
	 */
	List<UserNode> findUserNodesForUser(User user);

	/**
	 * Find all UserNodes for a given user.
	 * 
	 * <p>
	 * The returned nodes will have {@link UserNodeCertificate} values populated
	 * in {@link UserNode#getCertificate()}, with the priority being requested,
	 * active, disabled. The {@link UserNodeTransfer} values will be populated
	 * in {@link UserNode#getTransfer()} as well.
	 * </p>
	 * 
	 * @param userId
	 *        the user ID
	 * @return the nodes
	 */
	List<UserNode> findUserNodesAndCertificatesForUser(Long userId);

	/**
	 * Find a list of all archived UserNode objects for a particular user.
	 * 
	 * @param userId
	 *        the user ID to get all archived nodes for
	 * @return list of {@link UserNode} objects, or an empty list if none found
	 * @since 1.3
	 */
	List<UserNode> findArchivedUserNodesForUser(Long userId);

	/**
	 * Update the archived status of a set of node IDs.
	 * 
	 * @param userId
	 *        The user ID of the nodes to update the status for.
	 * @param nodeIds
	 *        The IDs of the nodes to update the archived status for.
	 * @param archived
	 *        {@code true} to archive the nodes, {@code false} to un-archive
	 *        them.
	 * @since 1.3
	 */
	void updateUserNodeArchivedStatus(Long userId, Long[] nodeIds, boolean archived);

	/**
	 * Store a {@link UserNodeTransfer}.
	 * 
	 * @param transfer
	 *        The transfer to store.
	 * @since 1.2
	 */
	void storeUserNodeTransfer(UserNodeTransfer transfer);

	/**
	 * Get a {@link UserNodeTransfer} by primary key.
	 * 
	 * @param pk
	 *        The ID of the transfer to get.
	 * @return The matching UserNodeTransfer, or <em>null</em> if not available.
	 * @since 1.2
	 */
	UserNodeTransfer getUserNodeTransfer(UserNodePK pk);

	/**
	 * Delete a {@link UserNodeTransfer}.
	 * 
	 * @param transfer
	 *        The transfer to delete.
	 * @since 1.2
	 */
	void deleteUserNodeTrasnfer(UserNodeTransfer transfer);

	/**
	 * Get all {@link UserNodeTransfer} instances for a given email address.
	 * 
	 * @param email
	 *        The email of the requested recipient of the ownership trasnfer.
	 * @return The available node transfers, never <em>null</em>.
	 * @since 1.2
	 */
	List<UserNodeTransfer> findUserNodeTransferRequestsForEmail(String email);

}
