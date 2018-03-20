/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.io.Serializable;

import org.eniware.central.datum.support.DatumUtils;
import org.eniware.central.domain.Entity;
import org.eniware.central.domain.SolarLocation;
import org.eniware.domain.GeneralDatumMetadata;
import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Metadata about the {@link GeneralLocationDatum} associated with a specific
 * location and source.
 * 
 * <p>
 * <b>Note</b> that {@link DatumUtils#getObjectFromJSON(String, Class)} is used
 * to manage the JSON value passed to {@link #setMetaJson(String)}.
 * </p>
 * 
 * @author matt
 * @version 1.0
 */
@JsonPropertyOrder({ "created", "updated", "locationId", "sourceId", "m", "t" })
public class GeneralLocationDatumMetadata implements Entity<LocationSourcePK>, Cloneable, Serializable {

	private static final long serialVersionUID = -1918479844558497435L;

	private LocationSourcePK id = new LocationSourcePK();
	private DateTime created;
	private DateTime updated;
	private GeneralDatumMetadata meta;
	private String metaJson;

	private SolarLocation location;

	@Override
	public int compareTo(LocationSourcePK o) {
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
		GeneralLocationDatumMetadata other = (GeneralLocationDatumMetadata) obj;
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
	 * Convenience getter for {@link LocationSourcePK#getLocationId()}.
	 * 
	 * @return the locationId
	 */
	public Long getLocationId() {
		return (id == null ? null : id.getLocationId());
	}

	/**
	 * Convenience setter for {@link LocationSourcePK#setLocationId(Long)}.
	 * 
	 * @param locationId
	 *        the locationId to set
	 */
	public void setLocationId(Long locationId) {
		if ( id == null ) {
			id = new LocationSourcePK();
		}
		id.setLocationId(locationId);
	}

	/**
	 * Convenience getter for {@link LocationSourcePK#getSourceId()}.
	 * 
	 * @return the sourceId
	 */
	public String getSourceId() {
		return (id == null ? null : id.getSourceId());
	}

	/**
	 * Convenience setter for {@link LocationSourcePK#setSourceId(String)}.
	 * 
	 * @param sourceId
	 *        the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		if ( id == null ) {
			id = new LocationSourcePK();
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
	public LocationSourcePK getId() {
		return id;
	}

	public void setId(LocationSourcePK id) {
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

	public SolarLocation getLocation() {
		return location;
	}

	public void setLocation(SolarLocation location) {
		this.location = location;
	}

}
