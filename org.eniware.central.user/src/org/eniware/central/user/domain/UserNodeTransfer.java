/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import java.io.Serializable;

import org.eniware.central.domain.Entity;
import org.eniware.central.domain.SolarNode;
import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A node ownership transfer request. This entity is associated with the node
 * requesting the transfer. The request is sent to the email address provided on
 * this entity.
 * 
 * @version 1.0
 */
public class UserNodeTransfer implements Entity<UserNodePK>, Cloneable, Serializable {

	private static final long serialVersionUID = -1316805739552206861L;

	private UserNodePK id = new UserNodePK();
	private DateTime created;
	private String email;

	private User user;
	private SolarNode node;

	/**
	 * Default constructor.
	 */
	public UserNodeTransfer() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param userId
	 *        The user ID.
	 * @param nodeId
	 *        The node ID.
	 * @param email
	 *        The email.
	 */
	public UserNodeTransfer(Long userId, Long nodeId, String email) {
		super();
		setUserId(userId);
		setNodeId(nodeId);
		setEmail(email);
	}

	/**
	 * Get the email of the requested new owner of the node.
	 * 
	 * @return The email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Set the email of the requested new owner of the node.
	 * 
	 * @param email
	 *        The email address to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	/**
	 * Convenience getter for {@link UserNodePK#getNodeId()}.
	 * 
	 * @return the nodeId
	 */
	public Long getNodeId() {
		return (id == null ? null : id.getNodeId());
	}

	/**
	 * Convenience setter for {@link UserNodePK#setNodeId(Long)}.
	 * 
	 * @param nodeId
	 *        the nodeId to set
	 */
	public void setNodeId(Long nodeId) {
		if ( id == null ) {
			id = new UserNodePK();
		}
		id.setNodeId(nodeId);
	}

	/**
	 * Convenience getter for {@link UserNodePK#getUserId()}.
	 * 
	 * @return the userId
	 */
	public Long getUserId() {
		return (id == null ? null : id.getUserId());
	}

	/**
	 * Convenience setter for {@link UserNodePK#setUserId(String)}.
	 * 
	 * @param userId
	 *        the userId to set
	 */
	public void setUserId(Long userId) {
		if ( id == null ) {
			id = new UserNodePK();
		}
		id.setUserId(userId);
	}

	@JsonIgnore
	@SerializeIgnore
	@Override
	public UserNodePK getId() {
		return id;
	}

	public void setId(UserNodePK id) {
		this.id = id;
	}

	@Override
	public int compareTo(UserNodePK o) {
		return id.compareTo(o);
	}

	@Override
	protected Object clone() {
		try {
			return super.clone();
		} catch ( CloneNotSupportedException e ) {
			// should not get here
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		UserNodeTransfer other = (UserNodeTransfer) obj;
		if ( id == null ) {
			if ( other.id != null ) {
				return false;
			}
		} else if ( !id.equals(other.id) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UserNodeTransfer{" + id + "}";
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

}
