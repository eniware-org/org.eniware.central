/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.io.Serializable;

/**
 * Primary key based on a node ID and source ID.
 *
 * @version 1.0
 */
public class NodeSourcePK implements Serializable, Cloneable, Comparable<NodeSourcePK> {

	private static final long serialVersionUID = -4263480807507680532L;

	private Long nodeId;
	private String sourceId;

	/**
	 * Default constructor.
	 */
	public NodeSourcePK() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param sourceId
	 *        the source ID
	 */
	public NodeSourcePK(Long nodeId, String sourceId) {
		super();
		this.nodeId = nodeId;
		this.sourceId = sourceId;
	}

	/**
	 * Compare two {@code NodeSourcePK} objects. Keys are ordered based on:
	 * 
	 * <ol>
	 * <li>nodeId</li>
	 * <li>sourceId</li>
	 * </ol>
	 * 
	 * <em>Null</em> values will be sorted before non-<em>null</em> values.
	 */
	@Override
	public int compareTo(NodeSourcePK o) {
		if ( o == null ) {
			return 1;
		}
		if ( o.nodeId == null ) {
			return 1;
		} else if ( nodeId == null ) {
			return -1;
		}
		int comparison = nodeId.compareTo(o.nodeId);
		if ( comparison != 0 ) {
			return comparison;
		}
		if ( o.sourceId == null ) {
			return 1;
		} else if ( sourceId == null ) {
			return -1;
		}
		return sourceId.compareToIgnoreCase(o.sourceId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NodeSourcePK{");
		if ( nodeId != null ) {
			builder.append("nodeId=");
			builder.append(nodeId);
			builder.append(", ");
		}
		if ( sourceId != null ) {
			builder.append("sourceId=");
			builder.append(sourceId);
		}
		builder.append("}");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
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
		NodeSourcePK other = (NodeSourcePK) obj;
		if ( nodeId == null ) {
			if ( other.nodeId != null ) {
				return false;
			}
		} else if ( !nodeId.equals(other.nodeId) ) {
			return false;
		}
		if ( sourceId == null ) {
			if ( other.sourceId != null ) {
				return false;
			}
		} else if ( !sourceId.equals(other.sourceId) ) {
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

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

}
