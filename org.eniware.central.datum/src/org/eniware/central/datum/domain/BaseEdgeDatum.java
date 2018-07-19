/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.joda.time.DateTime;

/**
 * Abstract base class for {@link EdgeDatum} implementations.
 
 * @version $Revision$ $Date$
 */
public abstract class BaseEdgeDatum extends BaseDatum implements EdgeDatum, Cloneable {

	private static final long serialVersionUID = -3071239480960854869L;

	private Long EdgeId = null;
	private String sourceId = null;
	private DateTime posted = null;
	
	/**
	 * Default constructor.
	 */
	public BaseEdgeDatum() {
		super();
	}
	
	/**
	 * Construct with an ID value.
	 * 
	 * @param id the ID value
	 */
	public BaseEdgeDatum(Long id) {
		super();
		setId(id);
	}

	/**
	 * Construct with ID and Edge ID values.
	 * 
	 * @param id the ID value
	 * @param EdgeId the Edge ID value
	 */
	public BaseEdgeDatum(Long id, Long EdgeId) {
		this(id);
		setEdgeId(EdgeId);
	}

	/**
	 * Construct with ID and Edge ID and source ID values.
	 * 
	 * @param id the ID value
	 * @param EdgeId the Edge ID value
	 * @param sourceId the source ID value
	 */
	public BaseEdgeDatum(Long id, Long EdgeId, String sourceId) {
		this(id, EdgeId);
		setSourceId(sourceId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((EdgeId == null) ? 0 : EdgeId.hashCode());
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result + ((getCreated() == null) ? 0 : getCreated().hashCode());
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
		BaseEdgeDatum other = (BaseEdgeDatum) obj;
		if ( super.equals(obj) ) {
			// we can stop here because IDs are equal
			return true;
		}
		if ( EdgeId != null && EdgeId.equals(other.EdgeId)
				&& sourceId != null && sourceId.equals(other.sourceId)
				&& getCreated() != null && getCreated().equals(other.getCreated()) ) {
			return true;
		}
		return false;
	}

	public String getErrorMessage() {
		// intentionally return null, for backwards compatibility
		return null;
	}
	public void setErrorMessage(String errorMessage) {
		// intentionally left blank, for backwards compatibility
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
	public DateTime getPosted() {
		return posted;
	}
	public void setPosted(DateTime posted) {
		this.posted = posted;
	}

}
