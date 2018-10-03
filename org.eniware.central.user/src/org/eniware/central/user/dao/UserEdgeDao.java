/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
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
 * DAO API for UserEdge objects.
 * 
 * @version 1.3
 */
public interface UserEdgeDao extends GenericDao<UserEdge, Long> {

	/**
	 * Find a list of all UserEdge objects for a particular user.
	 * 
	 * This will not return archived Edges (see
	 * {@link #findArchivedUserEdgesForUser(Long)}).
	 * 
	 * @param user
	 *        the user to get all Edges for
	 * @return list of {@link UserEdge} objects, or an empty list if none found
	 */
	List<UserEdge> findUserEdgesForUser(User user);

	/**
	 * Find all UserEdges for a given user.
	 * 
	 * <p>
	 * The returned Edges will have {@link UserEdgeCertificate} values populated
	 * in {@link UserEdge#getCertificate()}, with the priority being requested,
	 * active, disabled. The {@link UserEdgeTransfer} values will be populated
	 * in {@link UserEdge#getTransfer()} as well.
	 * </p>
	 * 
	 * @param userId
	 *        the user ID
	 * @return the Edges
	 */
	List<UserEdge> findUserEdgesAndCertificatesForUser(Long userId);

	/**
	 * Find a list of all archived UserEdge objects for a particular user.
	 * 
	 * @param userId
	 *        the user ID to get all archived Edges for
	 * @return list of {@link UserEdge} objects, or an empty list if none found
	 * @since 1.3
	 */
	List<UserEdge> findArchivedUserEdgesForUser(Long userId);

	/**
	 * Update the archived status of a set of Edge IDs.
	 * 
	 * @param userId
	 *        The user ID of the Edges to update the status for.
	 * @param EdgeIds
	 *        The IDs of the Edges to update the archived status for.
	 * @param archived
	 *        {@code true} to archive the Edges, {@code false} to un-archive
	 *        them.
	 * @since 1.3
	 */
	void updateUserEdgeArchivedStatus(Long userId, Long[] EdgeIds, boolean archived);

	/**
	 * Store a {@link UserEdgeTransfer}.
	 * 
	 * @param transfer
	 *        The transfer to store.
	 * @since 1.2
	 */
	void storeUserEdgeTransfer(UserEdgeTransfer transfer);

	/**
	 * Get a {@link UserEdgeTransfer} by primary key.
	 * 
	 * @param pk
	 *        The ID of the transfer to get.
	 * @return The matching UserEdgeTransfer, or <em>null</em> if not available.
	 * @since 1.2
	 */
	UserEdgeTransfer getUserEdgeTransfer(UserEdgePK pk);

	/**
	 * Delete a {@link UserEdgeTransfer}.
	 * 
	 * @param transfer
	 *        The transfer to delete.
	 * @since 1.2
	 */
	void deleteUserEdgeTrasnfer(UserEdgeTransfer transfer);

	/**
	 * Get all {@link UserEdgeTransfer} instances for a given email address.
	 * 
	 * @param email
	 *        The email of the requested recipient of the ownership trasnfer.
	 * @return The available Edge transfers, never <em>null</em>.
	 * @since 1.2
	 */
	List<UserEdgeTransfer> findUserEdgeTransferRequestsForEmail(String email);

}
