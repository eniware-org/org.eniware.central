/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

/**
 * A status enum for {@link UserNodeCertificate}.
 * 
 * @author matt
 * @version 1.0
 */
public enum UserNodeCertificateStatus {

	/** The certificate has been requested, but has not been generated yet. */
	a("Requested"),

	/** The certificate is active. */
	v("Active"),

	/** The certificate is disabled and should not be used. */
	z("Disabled");

	private final String value;

	private UserNodeCertificateStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
