/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import java.math.BigDecimal;
import java.util.Map;

import org.eniware.central.domain.BaseEntity;
import org.eniware.central.support.JsonUtils;
import org.eniware.util.SerializeIgnore;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A triggered alert condition.
 * 
 * @version 1.1
 */
public class UserAlertSituation extends BaseEntity {

	private static final long serialVersionUID = -8362863080058243096L;

	private UserAlert alert;
	private UserAlertSituationStatus status;
	private DateTime notified;
	private Map<String, Object> info;
	private String infoJson;

	public UserAlert getAlert() {
		return alert;
	}

	public void setAlert(UserAlert alert) {
		this.alert = alert;
	}

	public UserAlertSituationStatus getStatus() {
		return status;
	}

	public void setStatus(UserAlertSituationStatus status) {
		this.status = status;
	}

	public DateTime getNotified() {
		return notified;
	}

	public void setNotified(DateTime notified) {
		this.notified = notified;
	}

	/**
	 * Get the info object as a JSON string.
	 * 
	 * <p>
	 * This method will ignore <em>null</em> values.
	 * </p>
	 * 
	 * @return a JSON encoded string, never <em>null</em>
	 * @since 1.1
	 */
	@SerializeIgnore
	@JsonIgnore
	public String getInfoJson() {
		if ( infoJson == null ) {
			infoJson = JsonUtils.getJSONString(info, "{}");
		}
		return infoJson;
	}

	/**
	 * Set the info object via a JSON string.
	 * 
	 * <p>
	 * This method will remove any previously created info and replace it with
	 * the values parsed from the JSON. All floating point values will be
	 * converted to {@link BigDecimal} instances.
	 * </p>
	 * 
	 * @param json
	 * @since 1.1
	 */
	@JsonProperty
	// @JsonProperty needed because of @JsonIgnore on getter
	public void setInfoJson(String json) {
		infoJson = json;
		info = null;
	}

	/**
	 * Get the info object.
	 * 
	 * @return the info object
	 * @since 1.1
	 */
	@SuppressWarnings("unchecked")
	@JsonProperty
	public Map<String, Object> getInfo() {
		if ( info == null && infoJson != null ) {
			info = JsonUtils.getObjectFromJSON(infoJson, Map.class);
		}
		return info;
	}

	/**
	 * Set the info instance to use.
	 * 
	 * <p>
	 * This will replace any value set previously via
	 * {@link #setInfoJson(String)} as well.
	 * </p>
	 * 
	 * @param samples
	 *        the info to set
	 * @since 1.1
	 */
	@JsonProperty
	public void setInfo(Map<String, Object> info) {
		this.info = info;
		infoJson = null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserAlertSituation{id=");
		builder.append(getId());
		builder.append(", created=");
		builder.append(getCreated());
		builder.append(", alert=");
		builder.append(alert);
		builder.append(", status=");
		builder.append(status);
		builder.append(", notified=");
		builder.append(notified);
		builder.append("}");
		return builder.toString();
	}

}
