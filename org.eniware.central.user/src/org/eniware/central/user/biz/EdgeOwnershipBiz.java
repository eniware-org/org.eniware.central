/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.biz;

import java.util.List;

import org.eniware.central.security.AuthorizationException;
import org.eniware.central.user.domain.UserEdgeTransfer;

/**
 * API for Edge owner tasks.
 * 
 * @version 1.0
 */
public interface EdgeOwnershipBiz {

	/**
	 * Get a specific transfer for a given Edge.
	 * 
	 * @param userId
	 *        The ID of the user making the request.
	 * @param EdgeId
	 *        The ID of the Edge to transfer.
	 * @return The transfer, or <em>null</em> if none exists.
	 */
	UserEdgeTransfer getEdgeOwnershipTransfer(Long userId, Long EdgeId);

	/**
	 * Get a complete list of transfers for a given recipient.
	 * 
	 * <p>
	 * This provides a view into all transfer requests awaiting confirmation by
	 * a single user.
	 * </p>
	 * 
	 * @param email
	 *        The email address of the requested ownership recipient.
	 * @return A list of all ownership requests associated with the given
	 *         recipient (never <em>null</em>).
	 */
	List<UserEdgeTransfer> pendingEdgeOwnershipTransfersForEmail(String email);

	/**
	 * Request a transfer of ownership of a Edge.
	 * 
	 * When requesting a new owner for a Edge, the email address of the new
	 * owner is provided and an email will be sent a message with a special link
	 * for confirming or rejecting the transfer request. The Edge will not be
	 * transferred until the request is confirmed, via the
	 * {@link #confirmEdgeOwnershipTransfer()}.
	 * 
	 * @param userId
	 *        The ID of the user making the request.
	 * @param EdgeId
	 *        The ID of the Edge to transfer.
	 * @param newOwnerEmail
	 *        The email address of the user requested to take ownership of the
	 *        Edge.
	 * @throws AuthorizationException
	 *         If the active user is not authorized to transfer ownership of the
	 *         given Edge.
	 */
	void requestEdgeOwnershipTransfer(Long userId, Long EdgeId, String newOwnerEmail)
			throws AuthorizationException;

	/**
	 * Cancel a Edge ownership transfer request.
	 * 
	 * After a Edge transfer request has been made via
	 * {@link #requestEdgeOwnershipTransfer(Long, Long, String)} but before the
	 * new owner has accepted the request, the original owner can cancel the
	 * request by calling this method.
	 * 
	 * @param userId
	 *        The ID of the user making the request.
	 * @param EdgeId
	 *        The ID of the Edge to transfer.
	 * @throws AuthorizationException
	 *         If the active user is not authorized to transfer ownership of the
	 *         given Edge.
	 */
	void cancelEdgeOwnershipTransfer(Long userId, Long EdgeId) throws AuthorizationException;

	/**
	 * Confirm or reject a Edge transfer request.
	 * 
	 * After a Edge transfer request has been made via
	 * {@link #requestEdgeOwnershipTransfer(Long, Long, String)} the recipient
	 * of the transfer request can confirm or reject the request by calling this
	 * method.
	 * 
	 * <b>Note:</b> the active user's email address must match the one used in
	 * the original transfer request.
	 * 
	 * @param userId
	 *        The ID of the user making the request.
	 * @param EdgeId
	 *        The Edge ID if the Edge to accept or reject ownership of.
	 * @param accept
	 *        If <em>true</em> then accept the transfer request, otherwise
	 *        reject (canceling the request).
	 * @returns The original transfer entity.
	 * @throws AuthorizationException
	 *         If the active user is not authorized to confirm (or reject) the
	 *         ownership transfer.
	 */
	UserEdgeTransfer confirmEdgeOwnershipTransfer(Long userId, Long EdgeId, boolean accept)
			throws AuthorizationException;

}
