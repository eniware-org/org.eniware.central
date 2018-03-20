/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Bean for a new node request details.
 * 
 * @author matt
 * @version 1.0
 */
public class NewNodeRequest {

	private final Long userId;
	private final String securityPhrase;
	private final TimeZone timeZone;
	private final Locale locale;

	/**
	 * Construct with values.
	 * 
	 * @param userId
	 *        the user to associate the node with
	 * @param securityPhrase
	 *        the security phrase to use during the association process
	 * @param timeZone
	 *        a time zone for the node
	 * @param locale
	 *        a locale for the node; at least the country should be specified
	 */
	public NewNodeRequest(Long userId, String securityPhrase, TimeZone timeZone, Locale locale) {
		super();
		this.userId = userId;
		this.securityPhrase = securityPhrase;
		this.timeZone = timeZone;
		this.locale = locale;
	}

	public Long getUserId() {
		return userId;
	}

	public String getSecurityPhrase() {
		return securityPhrase;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public Locale getLocale() {
		return locale;
	}

}
