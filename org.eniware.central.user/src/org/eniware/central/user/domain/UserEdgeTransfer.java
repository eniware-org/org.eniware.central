/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import java.io.Serializable;

import org.eniware.central.domain.Entity;
import org.eniware.central.domain.EniwareEdge;
import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Edge ownership transfer request. This entity is associated with the Edge
 * requesting the transfer. The request is sent to the email address provided on
 * this entity.
 * 
 * @version 1.0
 */
public class UserEdgeTransfer implements Entity<UserEdgePK>, Cloneable, Serializable {

	private static final long serialVersionUID = -1316805739552206861L;

	private UserEdgePK id = new UserEdgePK();
	private DateTime created;
	private String email;

	private User user;
	private EniwareEdge Edge;

	/**
	 * Default constructor.
	 */
	public UserEdgeTransfer() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param userId
	 *        The user ID.
	 * @param EdgeId
	 *        The Edge ID.
	 * @param email
	 *        The email.
	 */
	public UserEdgeTransfer(Long userId, Long EdgeId, String email) {
		super();
		setUserId(userId);
		setEdgeId(EdgeId);
		setEmail(email);
	}

	/**
	 * Get the email of the requested new owner of the Edge.
	 * 
	 * @return The email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Set the email of the requested new owner of the Edge.
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
	 * Convenience getter for {@link UserEdgePK#getEdgeId()}.
	 * 
	 * @return the EdgeId
	 */
	public Long getEdgeId() {
		return (id == null ? null : id.getEdgeId());
	}

	/**
	 * Convenience setter for {@link UserEdgePK#setEdgeId(Long)}.
	 * 
	 * @param EdgeId
	 *        the EdgeId to set
	 */
	public void setEdgeId(Long EdgeId) {
		if ( id == null ) {
			id = new UserEdgePK();
		}
		id.setEdgeId(EdgeId);
	}

	/**
	 * Convenience getter for {@link UserEdgePK#getUserId()}.
	 * 
	 * @return the userId
	 */
	public Long getUserId() {
		return (id == null ? null : id.getUserId());
	}

	/**
	 * Convenience setter for {@link UserEdgePK#setUserId(String)}.
	 * 
	 * @param userId
	 *        the userId to set
	 */
	public void setUserId(Long userId) {
		if ( id == null ) {
			id = new UserEdgePK();
		}
		id.setUserId(userId);
	}

	@JsonIgnore
	@SerializeIgnore
	@Override
	public UserEdgePK getId() {
		return id;
	}

	public void setId(UserEdgePK id) {
		this.id = id;
	}

	@Override
	public int compareTo(UserEdgePK o) {
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
		UserEdgeTransfer other = (UserEdgeTransfer) obj;
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
		return "UserEdgeTransfer{" + id + "}";
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

}
