/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.io.Serializable;

import org.eniware.central.datum.support.DatumUtils;
import org.eniware.central.domain.Entity;
import org.eniware.domain.GeneralDatumMetadata;
import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Metadata about the {@link GeneralEdgeDatum} associated with a specific Edge
 * and source.
 * 
 * <p>
 * <b>Note</b> that {@link DatumUtils#getObjectFromJSON(String, Class)} is used
 * to manage the JSON value passed to {@link #setMetaJson(String)}.
 * </p>
 *
 * @version 1.1
 */
@JsonPropertyOrder({ "created", "updated", "EdgeId", "sourceId", "m", "t" })
public class GeneralEdgeDatumMetadata implements Entity<EdgeSourcePK>, Cloneable, Serializable {

	private static final long serialVersionUID = -1918479844558497435L;

	private EdgeSourcePK id = new EdgeSourcePK();
	private DateTime created;
	private DateTime updated;
	private GeneralDatumMetadata meta;
	private String metaJson;

	@Override
	public int compareTo(EdgeSourcePK o) {
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
		GeneralEdgeDatumMetadata other = (GeneralEdgeDatumMetadata) obj;
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
	 * Convenience getter for {@link EdgeSourcePK#getEdgeId()}.
	 * 
	 * @return the EdgeId
	 */
	public Long getEdgeId() {
		return (id == null ? null : id.getEdgeId());
	}

	/**
	 * Convenience setter for {@link EdgeSourcePK#setEdgeId(Long)}.
	 * 
	 * @param EdgeId
	 *        the EdgeId to set
	 */
	public void setEdgeId(Long EdgeId) {
		if ( id == null ) {
			id = new EdgeSourcePK();
		}
		id.setEdgeId(EdgeId);
	}

	/**
	 * Convenience getter for {@link EdgeSourcePK#getSourceId()}.
	 * 
	 * @return the sourceId
	 */
	public String getSourceId() {
		return (id == null ? null : id.getSourceId());
	}

	/**
	 * Convenience setter for {@link EdgeSourcePK#setSourceId(String)}.
	 * 
	 * @param sourceId
	 *        the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		if ( id == null ) {
			id = new EdgeSourcePK();
		}
		id.setSourceId(sourceId);
	}

	/**
	 * Alternative for {@link #getMeta()}. This method exists so that we can
	 * configure {@code @JsonUnwrapped} on our {@link GeneralDatumMetadata} but
	 * still support setting it in a normal, wrapped fashion via
	 * {@link #setMeta(GeneralDatumMetadata)}.
	 * 
	 * @return GeneralDatumMetadata
	 */
	@JsonUnwrapped
	public GeneralDatumMetadata getMetadata() {
		return getMeta();
	}

	@JsonIgnore
	@SerializeIgnore
	public GeneralDatumMetadata getMeta() {
		if ( meta == null && metaJson != null ) {
			meta = DatumUtils.getObjectFromJSON(metaJson, GeneralDatumMetadata.class);
			metaJson = null; // clear this out, because we might mutate meta and invalidate our cached JSON value
		}
		return meta;
	}

	@JsonProperty
	public void setMeta(GeneralDatumMetadata meta) {
		this.meta = meta;
		this.metaJson = null;
	}

	@JsonIgnore
	@SerializeIgnore
	public String getMetaJson() {
		if ( metaJson == null ) {
			metaJson = DatumUtils.getJSONString(meta, "{}");
			meta = null; // clear this out, because we might otherwise mutate it and invalidate our cached JSON value
		}
		return metaJson;
	}

	public void setMetaJson(String infoJson) {
		this.metaJson = infoJson;
		this.meta = null;
	}

	@Override
	@JsonIgnore
	@SerializeIgnore
	public EdgeSourcePK getId() {
		return id;
	}

	public void setId(EdgeSourcePK id) {
		this.id = id;
	}

	@Override
	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public DateTime getUpdated() {
		return updated;
	}

	public void setUpdated(DateTime updated) {
		this.updated = updated;
	}

}
