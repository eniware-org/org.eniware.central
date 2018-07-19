/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import java.math.BigDecimal;
import java.util.Map;

import org.eniware.central.domain.BaseEntity;
import org.eniware.central.support.JsonUtils;
import org.eniware.domain.GeneralEdgeDatumSamples;
import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * An alert condition definition. User alerts are designed to cover conditions
 * such as
 * 
 * <ul>
 * <li>Edge <em>X</em> has not posted data in <em>Y</em> hours</li>
 * <li>Edge <em>X</em> posted value <em>V</em> for property <em>A</em> greater
 * than <em>T</em></li>
 * <li>location <em>L</em> posted value <em>V</em> for property <em>A</em> less
 * than <em>T</em></li>
 * </ul>
 *
 * @version 1.0
 */
@JsonPropertyOrder({ "id", "created", "userId", "EdgeId", "type", "status", "validTo", "options" })
public class UserAlert extends BaseEntity {

	private static final long serialVersionUID = 1374111067444093568L;

	private Long userId;
	private UserAlertType type;
	private UserAlertStatus status;
	private Long EdgeId;
	private DateTime validTo;
	private Map<String, Object> options;
	private String optionsJson;

	// transient
	private UserAlertSituation situation;

	/**
	 * Get the options object as a JSON string.
	 * 
	 * <p>
	 * This method will ignore <em>null</em> values.
	 * </p>
	 * 
	 * @return a JSON encoded string, never <em>null</em>
	 */
	@SerializeIgnore
	@JsonIgnore
	public String getOptionsJson() {
		if ( optionsJson == null ) {
			optionsJson = JsonUtils.getJSONString(options, "{}");
		}
		return optionsJson;
	}

	/**
	 * Set the options object via a JSON string.
	 * 
	 * <p>
	 * This method will remove any previously created options and replace it
	 * with the values parsed from the JSON. All floating point values will be
	 * converted to {@link BigDecimal} instances.
	 * </p>
	 * 
	 * @param json
	 */
	@JsonProperty
	// @JsonProperty needed because of @JsonIgnore on getter
	public void setOptionsJson(String json) {
		optionsJson = json;
		options = null;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public UserAlertType getType() {
		return type;
	}

	public void setType(UserAlertType type) {
		this.type = type;
	}

	public UserAlertStatus getStatus() {
		return status;
	}

	public void setStatus(UserAlertStatus status) {
		this.status = status;
	}

	public Long getEdgeId() {
		return EdgeId;
	}

	public void setEdgeId(Long EdgeId) {
		this.EdgeId = EdgeId;
	}

	public DateTime getValidTo() {
		return validTo;
	}

	public void setValidTo(DateTime validTo) {
		this.validTo = validTo;
	}

	@SuppressWarnings("unchecked")
	@JsonProperty
	public Map<String, Object> getOptions() {
		if ( options == null && optionsJson != null ) {
			options = JsonUtils.getObjectFromJSON(optionsJson, Map.class);
		}
		return options;
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
	public void setOptions(Map<String, Object> options) {
		this.options = options;
		optionsJson = null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserAlert{id=");
		builder.append(getId());
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", type=");
		builder.append(type);
		builder.append(", status=");
		builder.append(status);
		if ( EdgeId != null ) {
			builder.append(", ");
			builder.append("EdgeId=");
			builder.append(EdgeId);
		}
		builder.append("}");
		return builder.toString();
	}

	public UserAlertSituation getSituation() {
		return situation;
	}

	public void setSituation(UserAlertSituation situation) {
		this.situation = situation;
	}

}
