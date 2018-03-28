/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.central.domain.BaseEntity;
import org.eniware.central.domain.SolarLocation;
import org.eniware.central.domain.SolarNode;

/**
 * A solar node with user details.
 * 
 * <p>
 * This object augments a {@link SolarNode} with additional information that
 * nodes themselves are not concerned with, but users are. This allows the
 * {@link SolarNode} object to remain lightweight.
 * </p>
 * 
 * @version 1.3
 */
public class UserNode extends BaseEntity {

	private static final long serialVersionUID = -3247965742224565205L;

	private String description;
	private String name;
	private boolean requiresAuthorization = false;

	private User user;
	private SolarNode node;

	// transient
	private UserNodeCertificate certificate;
	private UserNodeTransfer transfer;

	/**
	 * Default constructor.
	 */
	public UserNode() {
		super();
	}

	/**
	 * Construct for a user and node.
	 * 
	 * @param user
	 *        the user
	 * @param node
	 *        the node
	 */
	public UserNode(User user, SolarNode node) {
		super();
		setUser(user);
		setNode(node);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the node ID and associated name, if available, as a string. If no
	 * name is available, this method returns just the node ID.
	 * 
	 * @return The node ID and name as a string.
	 * @since 1.3
	 */
	public String getIdAndName() {
		StringBuilder buf = new StringBuilder();
		if ( node != null ) {
			buf.append(node.getId());
		}
		if ( name != null && name.length() > 0 ) {
			buf.append(" - ").append(name);
		}
		return buf.toString();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SolarNode getNode() {
		return node;
	}

	public void setNode(SolarNode node) {
		this.node = node;
	}

	public UserNodeCertificate getCertificate() {
		return certificate;
	}

	public void setCertificate(UserNodeCertificate certificate) {
		this.certificate = certificate;
	}

	public boolean isRequiresAuthorization() {
		return requiresAuthorization;
	}

	public void setRequiresAuthorization(boolean requiresAuthorization) {
		this.requiresAuthorization = requiresAuthorization;
	}

	/**
	 * Exposed as a top-level property so that it can be marshalled to clients.
	 * 
	 * @return
	 */
	public SolarLocation getNodeLocation() {
		return (node != null ? node.getLocation() : null);
	}

	public UserNodeTransfer getTransfer() {
		return transfer;
	}

	public void setTransfer(UserNodeTransfer transfer) {
		this.transfer = transfer;
	}

}
