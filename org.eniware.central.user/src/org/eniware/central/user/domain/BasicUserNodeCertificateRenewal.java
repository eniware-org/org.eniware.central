/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.domain.NetworkAssociation;
import org.eniware.domain.NetworkAssociationDetails;

/**
 * Basic implementation of {@link UserNodeCertificateRenewal}.
 *
 * @version 1.0
 */
public class BasicUserNodeCertificateRenewal extends NetworkAssociationDetails
		implements UserNodeCertificateRenewal {

	private static final long serialVersionUID = 3537089462856128834L;

	private UserNodeCertificateInstallationStatus installationStatus;

	/**
	 * Default constructor.
	 */
	public BasicUserNodeCertificateRenewal() {
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 *        the NetworkAssociation to copy
	 */
	public BasicUserNodeCertificateRenewal(NetworkAssociation other) {
		super(other);
		if ( other instanceof BasicUserNodeCertificateRenewal ) {
			BasicUserNodeCertificateRenewal otherRenewal = (BasicUserNodeCertificateRenewal) other;
			setInstallationStatus(otherRenewal.getInstallationStatus());
		}
	}

	@Override
	public UserNodeCertificateInstallationStatus getInstallationStatus() {
		return installationStatus;
	}

	public void setInstallationStatus(UserNodeCertificateInstallationStatus installationStatus) {
		this.installationStatus = installationStatus;
	}

}
