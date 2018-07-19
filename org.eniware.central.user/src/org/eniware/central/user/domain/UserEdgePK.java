/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import java.io.Serializable;

/**
 * Primary key based on a user ID and node ID.
 * 
 * @version 1.0
 */
public class UserEdgePK implements Serializable, Cloneable, Comparable<UserEdgePK> {

	private static final long serialVersionUID = -2661140310545544324L;

	private Long nodeId;
	private Long userId;

	/**
	 * Default constructor.
	 */
	public UserEdgePK() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param userId
	 *        the user ID
	 * @param nodeId
	 *        the node ID
	 */
	public UserEdgePK(Long userId, Long nodeId) {
		super();
		this.nodeId = nodeId;
		this.userId = userId;
	}

	/**
	 * Compare two {@code UserNodePK} objects. Keys are ordered based on:
	 * 
	 * <ol>
	 * <li>userId</li>
	 * <li>nodeId</li>
	 * </ol>
	 * 
	 * <em>Null</em> values will be sorted before non-<em>null</em> values.
	 */
	@Override
	public int compareTo(UserEdgePK o) {
		if ( o == null ) {
			return 1;
		}
		if ( o.userId == null ) {
			return 1;
		} else if ( userId == null ) {
			return -1;
		}
		int comparison = userId.compareTo(o.userId);
		if ( comparison != 0 ) {
			return comparison;
		}
		if ( o.nodeId == null ) {
			return 1;
		} else if ( nodeId == null ) {
			return -1;
		}
		return nodeId.compareTo(o.nodeId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserNodePK{");
		if ( userId != null ) {
			builder.append("userId=");
			builder.append(userId);
			builder.append(", ");
		}
		if ( nodeId != null ) {
			builder.append("nodeId=");
			builder.append(nodeId);
		}
		builder.append("}");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
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
		UserEdgePK other = (UserEdgePK) obj;
		if ( nodeId == null ) {
			if ( other.nodeId != null ) {
				return false;
			}
		} else if ( !nodeId.equals(other.nodeId) ) {
			return false;
		}
		if ( userId == null ) {
			if ( other.userId != null ) {
				return false;
			}
		} else if ( !userId.equals(other.userId) ) {
			return false;
		}
		return true;
	}

	@Override
	protected Object clone() {
		try {
			return super.clone();
		} catch ( CloneNotSupportedException e ) {
			// shouldn't get here
			throw new RuntimeException(e);
		}
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
