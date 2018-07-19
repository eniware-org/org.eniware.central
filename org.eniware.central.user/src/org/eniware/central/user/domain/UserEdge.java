/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.central.domain.BaseEntity;
import org.eniware.central.domain.EniwareLocation;
import org.eniware.central.domain.EniwareEdge;

/**
 * A eniware Edge with user details.
 * 
 * <p>
 * This object augments a {@link EniwareEdge} with additional information that
 * Edges themselves are not concerned with, but users are. This allows the
 * {@link EniwareEdge} object to remain lightweight.
 * </p>
 * 
 * @version 1.3
 */
public class UserEdge extends BaseEntity {

	private static final long serialVersionUID = -3247965742224565205L;

	private String description;
	private String name;
	private boolean requiresAuthorization = false;

	private User user;
	private EniwareEdge Edge;

	// transient
	private UserEdgeCertificate certificate;
	private UserEdgeTransfer transfer;

	/**
	 * Default constructor.
	 */
	public UserEdge() {
		super();
	}

	/**
	 * Construct for a user and Edge.
	 * 
	 * @param user
	 *        the user
	 * @param Edge
	 *        the Edge
	 */
	public UserEdge(User user, EniwareEdge Edge) {
		super();
		setUser(user);
		setEdge(Edge);
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
	 * Get the Edge ID and associated name, if available, as a string. If no
	 * name is available, this method returns just the Edge ID.
	 * 
	 * @return The Edge ID and name as a string.
	 * @since 1.3
	 */
	public String getIdAndName() {
		StringBuilder buf = new StringBuilder();
		if ( Edge != null ) {
			buf.append(Edge.getId());
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

	public EniwareEdge getEdge() {
		return Edge;
	}

	public void setEdge(EniwareEdge Edge) {
		this.Edge = Edge;
	}

	public UserEdgeCertificate getCertificate() {
		return certificate;
	}

	public void setCertificate(UserEdgeCertificate certificate) {
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
	public EniwareLocation getEdgeLocation() {
		return (Edge != null ? Edge.getLocation() : null);
	}

	public UserEdgeTransfer getTransfer() {
		return transfer;
	}

	public void setTransfer(UserEdgeTransfer transfer) {
		this.transfer = transfer;
	}

}
