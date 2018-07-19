/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.io.Serializable;

/**
 * Primary key based on a Edge ID and source ID.
 *
 * @version 1.0
 */
public class EdgeSourcePK implements Serializable, Cloneable, Comparable<EdgeSourcePK> {

	private static final long serialVersionUID = -4263480807507680532L;

	private Long EdgeId;
	private String sourceId;

	/**
	 * Default constructor.
	 */
	public EdgeSourcePK() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param sourceId
	 *        the source ID
	 */
	public EdgeSourcePK(Long EdgeId, String sourceId) {
		super();
		this.EdgeId = EdgeId;
		this.sourceId = sourceId;
	}

	/**
	 * Compare two {@code EdgeSourcePK} objects. Keys are ordered based on:
	 * 
	 * <ol>
	 * <li>EdgeId</li>
	 * <li>sourceId</li>
	 * </ol>
	 * 
	 * <em>Null</em> values will be sorted before non-<em>null</em> values.
	 */
	@Override
	public int compareTo(EdgeSourcePK o) {
		if ( o == null ) {
			return 1;
		}
		if ( o.EdgeId == null ) {
			return 1;
		} else if ( EdgeId == null ) {
			return -1;
		}
		int comparison = EdgeId.compareTo(o.EdgeId);
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
		builder.append("EdgeSourcePK{");
		if ( EdgeId != null ) {
			builder.append("EdgeId=");
			builder.append(EdgeId);
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
		result = prime * result + ((EdgeId == null) ? 0 : EdgeId.hashCode());
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
		EdgeSourcePK other = (EdgeSourcePK) obj;
		if ( EdgeId == null ) {
			if ( other.EdgeId != null ) {
				return false;
			}
		} else if ( !EdgeId.equals(other.EdgeId) ) {
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

	public Long getEdgeId() {
		return EdgeId;
	}

	public void setEdgeId(Long EdgeId) {
		this.EdgeId = EdgeId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

}
