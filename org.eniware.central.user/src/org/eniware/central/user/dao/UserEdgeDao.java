/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao;

import java.util.List;

import org.eniware.central.dao.GenericDao;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.domain.UserEdgeCertificate;
import org.eniware.central.user.domain.UserEdgePK;
import org.eniware.central.user.domain.UserEdgeTransfer;

/**
 * DAO API for UserNode objects.
 * 
 * @version 1.3
 */
public interface UserEdgeDao extends GenericDao<UserEdge, Long> {

	/**
	 * Find a list of all UserNode objects for a particular user.
	 * 
	 * This will not return archived nodes (see
	 * {@link #findArchivedUserNodesForUser(Long)}).
	 * 
	 * @param user
	 *        the user to get all nodes for
	 * @return list of {@link UserEdge} objects, or an empty list if none found
	 */
	List<UserEdge> findUserNodesForUser(User user);

	/**
	 * Find all UserNodes for a given user.
	 * 
	 * <p>
	 * The returned nodes will have {@link UserEdgeCertificate} values populated
	 * in {@link UserEdge#getCertificate()}, with the priority being requested,
	 * active, disabled. The {@link UserEdgeTransfer} values will be populated
	 * in {@link UserEdge#getTransfer()} as well.
	 * </p>
	 * 
	 * @param userId
	 *        the user ID
	 * @return the nodes
	 */
	List<UserEdge> findUserNodesAndCertificatesForUser(Long userId);

	/**
	 * Find a list of all archived UserNode objects for a particular user.
	 * 
	 * @param userId
	 *        the user ID to get all archived nodes for
	 * @return list of {@link UserEdge} objects, or an empty list if none found
	 * @since 1.3
	 */
	List<UserEdge> findArchivedUserNodesForUser(Long userId);

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
	 * Store a {@link UserEdgeTransfer}.
	 * 
	 * @param transfer
	 *        The transfer to store.
	 * @since 1.2
	 */
	void storeUserNodeTransfer(UserEdgeTransfer transfer);

	/**
	 * Get a {@link UserEdgeTransfer} by primary key.
	 * 
	 * @param pk
	 *        The ID of the transfer to get.
	 * @return The matching UserNodeTransfer, or <em>null</em> if not available.
	 * @since 1.2
	 */
	UserEdgeTransfer getUserNodeTransfer(UserEdgePK pk);

	/**
	 * Delete a {@link UserEdgeTransfer}.
	 * 
	 * @param transfer
	 *        The transfer to delete.
	 * @since 1.2
	 */
	void deleteUserNodeTrasnfer(UserEdgeTransfer transfer);

	/**
	 * Get all {@link UserEdgeTransfer} instances for a given email address.
	 * 
	 * @param email
	 *        The email of the requested recipient of the ownership trasnfer.
	 * @return The available node transfers, never <em>null</em>.
	 * @since 1.2
	 */
	List<UserEdgeTransfer> findUserNodeTransferRequestsForEmail(String email);

}
