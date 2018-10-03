/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

/**
 * The status of a network certificate install process.
 * 
 * @version 1.0
 */
public enum UserEdgeCertificateInstallationStatus {

	/** A request to install the certificate on a Edge has been queued. */
	RequestQueued,

	/**
	 * The request to install the certificate has been received by the Edge.
	 */
	RequestReceived,

	/**
	 * The certificate has been installed successfully on the Edge.
	 */
	Installed,

	/** The Edge declined the certificate installation request. */
	Declined,

}
