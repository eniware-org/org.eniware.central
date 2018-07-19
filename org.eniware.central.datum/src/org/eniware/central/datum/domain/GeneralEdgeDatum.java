/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.eniware.central.datum.support.DatumUtils;
import org.eniware.central.domain.Entity;
import org.eniware.domain.GeneralEdgeDatumSamples;
import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Generalized Edge-based datum.
 * 
 * <p>
 * <b>Note</b> that {@link DatumUtils#getObjectFromJSON(String, Class)} is used
 * to manage the JSON value passed to {@link #setSampleJson(String)}.
 * </p>
 *
 * @version 1.2
 */
@JsonPropertyOrder({ "created", "EdgeId", "sourceId" })
public class GeneralEdgeDatum implements Entity<GeneralEdgeDatumPK>, Cloneable, Serializable {

	private static final long serialVersionUID = 1099409416648794740L;

	private GeneralEdgeDatumPK id = new GeneralEdgeDatumPK();
	private GeneralEdgeDatumSamples samples;
	private DateTime posted;
	private String sampleJson;

	/**
	 * Convenience getter for {@link GeneralEdgeDatumPK#getEdgeId()}.
	 * 
	 * @return the EdgeId
	 */
	public Long getEdgeId() {
		return (id == null ? null : id.getEdgeId());
	}

	/**
	 * Convenience setter for {@link GeneralEdgeDatumPK#setEdgeId(Long)}.
	 * 
	 * @param EdgeId
	 *        the EdgeId to set
	 */
	public void setEdgeId(Long EdgeId) {
		if ( id == null ) {
			id = new GeneralEdgeDatumPK();
		}
		id.setEdgeId(EdgeId);
	}

	/**
	 * Convenience getter for {@link GeneralEdgeDatumPK#getSourceId()}.
	 * 
	 * @return the sourceId
	 */
	public String getSourceId() {
		return (id == null ? null : id.getSourceId());
	}

	/**
	 * Convenience setter for {@link GeneralEdgeDatumPK#setSourceId(String)}.
	 * 
	 * @param sourceId
	 *        the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		if ( id == null ) {
			id = new GeneralEdgeDatumPK();
		}
		id.setSourceId(sourceId);
	}

	/**
	 * Convenience setter for {@link GeneralEdgeDatumPK#setCreated(DateTime)}.
	 * 
	 * @param created
	 *        the created to set
	 */
	public void setCreated(DateTime created) {
		if ( id == null ) {
			id = new GeneralEdgeDatumPK();
		}
		id.setCreated(created);
	}

	@Override
	public DateTime getCreated() {
		return (id == null ? null : id.getCreated());
	}

	@Override
	@JsonIgnore
	@SerializeIgnore
	public GeneralEdgeDatumPK getId() {
		return id;
	}

	/**
	 * Convenience method for {@link GeneralEdgeDatumSamples#getSampleData()}.
	 * 
	 * @return the sample data, or <em>null</em> if none available
	 */
	@JsonUnwrapped
	@JsonAnyGetter
	public Map<String, ?> getSampleData() {
		GeneralEdgeDatumSamples s = getSamples();
		return (s == null ? null : s.getSampleData());
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch ( CloneNotSupportedException e ) {
			// should never get here
			throw new RuntimeException(e);
		}
	}

	@Override
	public int compareTo(GeneralEdgeDatumPK o) {
		if ( id == null && o == null ) {
			return 0;
		}
		if ( id == null ) {
			return -1;
		}
		if ( o == null ) {
			return 1;
		}
		return id.compareTo(o);
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
		GeneralEdgeDatum other = (GeneralEdgeDatum) obj;
		if ( id == null ) {
			if ( other.id != null ) {
				return false;
			}
		} else if ( !id.equals(other.id) ) {
			return false;
		}
		return true;
	}

	/**
	 * Get the {@link GeneralEdgeDatumSamples} object as a JSON string.
	 * 
	 * <p>
	 * This method will ignore <em>null</em> values.
	 * </p>
	 * 
	 * @return a JSON encoded string, never <em>null</em>
	 */
	@SerializeIgnore
	@JsonIgnore
	public String getSampleJson() {
		if ( sampleJson == null ) {
			sampleJson = DatumUtils.getJSONString(samples, "{}");
		}
		return sampleJson;
	}

	/**
	 * Set the {@link GeneralEdgeDatumSamples} object via a JSON string.
	 * 
	 * <p>
	 * This method will remove any previously created GeneralEdgeDatumSamples
	 * and replace it with the values parsed from the JSON. All floating point
	 * values will be converted to {@link BigDecimal} instances.
	 * </p>
	 * 
	 * @param json
	 */
	@JsonProperty
	// @JsonProperty needed because of @JsonIgnore on getter
	public void setSampleJson(String json) {
		sampleJson = json;
		samples = null;
	}

	@SerializeIgnore
	@JsonIgnore
	public DateTime getPosted() {
		return posted;
	}

	public void setPosted(DateTime posted) {
		this.posted = posted;
	}

	@SerializeIgnore
	@JsonIgnore
	public GeneralEdgeDatumSamples getSamples() {
		if ( samples == null && sampleJson != null ) {
			samples = DatumUtils.getObjectFromJSON(sampleJson, GeneralEdgeDatumSamples.class);
		}
		return samples;
	}

	/**
	 * Set the {@link GeneralEdgeDatumSamples} instance to use.
	 * 
	 * <p>
	 * This will replace any value set previously via
	 * {@link #setSampleJson(String)} as well.
	 * </p>
	 * 
	 * @param samples
	 *        the samples instance to set
	 */
	@JsonProperty
	// @JsonProperty needed because of @JsonIgnore on getter
	public void setSamples(GeneralEdgeDatumSamples samples) {
		this.samples = samples;
		sampleJson = null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeneralEdgeDatum{id=");
		builder.append(id);
		builder.append(", samples=");
		builder.append(samples == null ? "null" : samples.getSampleData());
		builder.append("}");
		return builder.toString();
	}

}
