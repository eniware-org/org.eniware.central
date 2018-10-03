/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.central.domain.BaseEntity;
import org.joda.time.DateTime;

/**
 * The "pending confirmation" entity for after a user generates a Edge
 * "invitation" to join EniwareNet. The user must confirm the invitation before a
 * UserEdge entity is created.
 * 
 * @version 1.2
 */
public class UserEdgeConfirmation extends BaseEntity {

	private static final long serialVersionUID = -598611218946751443L;

	private User user;
	private Long EdgeId;
	private String confirmationKey;
	private DateTime confirmationDate;
	private String securityPhrase;
	private String country;
	private String timeZoneId;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getEdgeId() {
		return EdgeId;
	}

	public void setEdgeId(Long EdgeId) {
		this.EdgeId = EdgeId;
	}

	public String getConfirmationKey() {
		return confirmationKey;
	}

	public void setConfirmationKey(String confirmationKey) {
		this.confirmationKey = confirmationKey;
	}

	public DateTime getConfirmationDate() {
		return confirmationDate;
	}

	public void setConfirmationDate(DateTime confirmationDate) {
		this.confirmationDate = confirmationDate;
	}

	public String getSecurityPhrase() {
		return securityPhrase;
	}

	public void setSecurityPhrase(String securityPhrase) {
		this.securityPhrase = securityPhrase;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String countryCode) {
		this.country = countryCode;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneName) {
		this.timeZoneId = timeZoneName;
	}

}
