/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import org.eniware.domain.NetworkAssociation;
import org.eniware.domain.NetworkAssociationDetails;

/**
 * Basic implementation of {@link UserEdgeCertificateRenewal}.
 *
 * @version 1.0
 */
public class BasicUserEdgeCertificateRenewal extends NetworkAssociationDetails
		implements UserEdgeCertificateRenewal {

	private static final long serialVersionUID = 3537089462856128834L;

	private UserEdgeCertificateInstallationStatus installationStatus;

	/**
	 * Default constructor.
	 */
	public BasicUserEdgeCertificateRenewal() {
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 *        the NetworkAssociation to copy
	 */
	public BasicUserEdgeCertificateRenewal(NetworkAssociation other) {
		super(other);
		if ( other instanceof BasicUserEdgeCertificateRenewal ) {
			BasicUserEdgeCertificateRenewal otherRenewal = (BasicUserEdgeCertificateRenewal) other;
			setInstallationStatus(otherRenewal.getInstallationStatus());
		}
	}

	@Override
	public UserEdgeCertificateInstallationStatus getInstallationStatus() {
		return installationStatus;
	}

	public void setInstallationStatus(UserEdgeCertificateInstallationStatus installationStatus) {
		this.installationStatus = installationStatus;
	}

}
