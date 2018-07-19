/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

import org.eniware.central.support.JsonUtils;
import org.eniware.domain.GeneralDatumMetadata;
import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Metadata about a {@link EniwareEdge}.
 * 
 * <b>Note</b> that {@link JsonUtils#getObjectFromJSON(String, Class)} is used
 * to manage the JSON value passed to {@link #setMetaJson(String)}.
 * @version 1.0
 * @since 1.32
 */
@JsonIgnoreProperties("id")
@JsonPropertyOrder({ "EdgeId", "created", "updated" })
public class EniwareEdgeMetadata extends BaseEntity implements EdgeMetadata, Cloneable, Serializable {

	private static final long serialVersionUID = 7366747359583724835L;

	private DateTime updated;
	private GeneralDatumMetadata meta;
	private String metaJson;

	/**
	 * Convenience getter for {@link #getId()}.
	 * 
	 * @return the EdgeId
	 */
	@Override
	public Long getEdgeId() {
		return getId();
	}

	/**
	 * Convenience setter for {@link #setId(Long)}.
	 * 
	 * @param EdgeId
	 *        the EdgeId to set
	 */
	public void setEdgeId(Long EdgeId) {
		setId(EdgeId);
	}

	/**
	 * Alternative for {@link #getMeta()}. This method exists so that we can
	 * configure {@code @JsonUnwrapped} on our {@link GeneralDatumMetadata} but
	 * still support setting it in a normal, wrapped fashion via
	 * {@link #setMeta(GeneralDatumMetadata)}.
	 * 
	 * @return GeneralDatumMetadata
	 */
	@Override
	@JsonUnwrapped
	public GeneralDatumMetadata getMetadata() {
		return getMeta();
	}

	@JsonIgnore
	@SerializeIgnore
	public GeneralDatumMetadata getMeta() {
		if ( meta == null && metaJson != null ) {
			meta = JsonUtils.getObjectFromJSON(metaJson, GeneralDatumMetadata.class);
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
			metaJson = JsonUtils.getJSONString(meta, "{}");
			meta = null; // clear this out, because we might otherwise mutate it and invalidate our cached JSON value
		}
		return metaJson;
	}

	public void setMetaJson(String infoJson) {
		this.metaJson = infoJson;
		this.meta = null;
	}

	@Override
	public DateTime getUpdated() {
		return updated;
	}

	public void setUpdated(DateTime updated) {
		this.updated = updated;
	}

}
