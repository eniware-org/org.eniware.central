/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.io.Serializable;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;

/**
 * Primary key for a general location datum.
 *
 * @version 1.1
 */
public class GeneralLocationDatumPK
		implements Serializable, Cloneable, Comparable<GeneralLocationDatumPK> {

	private static final long serialVersionUID = 8888712105726539385L;

	private Long locationId;
	private DateTime created;
	private String sourceId;

	/**
	 * Get a computed string ID value for this primary key. Note this value is
	 * derived from the properties of this class, and not assigned by the
	 * system.
	 * 
	 * @return computed ID string
	 */
	public String getId() {
		StringBuilder builder = new StringBuilder();
		builder.append("n=");
		if ( locationId != null ) {
			builder.append(locationId);
		}
		builder.append(";c=");
		if ( created != null ) {
			builder.append(created);
		}
		builder.append(";s=");
		if ( sourceId != null ) {
			builder.append(sourceId);
		}
		return DigestUtils.sha1Hex(builder.toString());
	}

	/**
	 * Compare two {@code GeneralLocationDautumPK} objects. Keys are ordered
	 * based on:
	 * 
	 * <ol>
	 * <li>locationId</li>
	 * <li>sourceId</li>
	 * <li>created</li>
	 * </ol>
	 * 
	 * <em>Null</em> values will be sorted before non-<em>null</em> values.
	 */
	@Override
	public int compareTo(GeneralLocationDatumPK o) {
		if ( o == null ) {
			return 1;
		}
		if ( o.locationId == null ) {
			return 1;
		} else if ( locationId == null ) {
			return -1;
		}
		int comparison = locationId.compareTo(o.locationId);
		if ( comparison != 0 ) {
			return comparison;
		}
		if ( o.sourceId == null ) {
			return 1;
		} else if ( sourceId == null ) {
			return -1;
		}
		comparison = sourceId.compareToIgnoreCase(o.sourceId);
		if ( comparison != 0 ) {
			return comparison;
		}
		if ( o.created == null ) {
			return 1;
		} else if ( created == null ) {
			return -1;
		}
		return created.compareTo(o.created);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeneralLocationDatumPK{");
		if ( locationId != null ) {
			builder.append("locationId=");
			builder.append(locationId);
			builder.append(", ");
		}
		if ( created != null ) {
			builder.append("created=");
			builder.append(created);
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
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
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
		GeneralLocationDatumPK other = (GeneralLocationDatumPK) obj;
		if ( locationId == null ) {
			if ( other.locationId != null ) {
				return false;
			}
		} else if ( !locationId.equals(other.locationId) ) {
			return false;
		}
		if ( created == null ) {
			if ( other.created != null ) {
				return false;
			}
		} else if ( !created.equals(other.created) ) {
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

	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

}
