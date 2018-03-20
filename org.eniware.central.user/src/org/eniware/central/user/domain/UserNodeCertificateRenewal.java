/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.domain.NetworkCertificate;

/**
 * Extension of {@link NetworkCertificate} with support for renewal requests.
 * 
 * @author matt
 * @version 1.0
 */
public interface UserNodeCertificateRenewal extends NetworkCertificate {

	/**
	 * Get the status of the renewed certificate installation process.
	 * 
	 * @return The certificate installation status.
	 */
	UserNodeCertificateInstallationStatus getInstallationStatus();

}
