/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.biz;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.eniware.support.CertificateService;

/**
 * API for managing EniwareEdge PKI from within EniwareUser.
 * 
 * @version 1.1
 */
public interface EdgePKIBiz extends CertificateService {

	/**
	 * Submit a certificate signing request (CSR) and obtain a unique request
	 * ID. The active security user will be used for the CSR requester details.
	 * 
	 * @param certificate
	 *        the certificate to sign and submit to the certification authority
	 *        (CA)
	 * @param privateKey
	 *        the private key to sign the certificate with
	 * @return a unique ID from the CA
	 * @throws org.eniware.central.security.SecurityException
	 *         if the active user is not available
	 */
	String submitCSR(final X509Certificate certificate, final PrivateKey privateKey)
			throws org.eniware.central.security.SecurityException;

	/**
	 * Approve a certificate signing request (CSR) and obtain the certificate
	 * chain. The active security user details must match the requester details
	 * for the given {@code requestID}.
	 * 
	 * @param requestID
	 *        the request ID to approve
	 * @return the certificate, and the rest of the certificates in the chain
	 * @throws org.eniware.central.security.SecurityException
	 *         if the active user is not available
	 */
	X509Certificate[] approveCSR(String requestID)
			throws org.eniware.central.security.SecurityException;

	/**
	 * Submit a request to renew a certificate. The active security user details
	 * must match the certificate details.
	 * 
	 * @param certificate
	 *        The certificate to renew.
	 * @return a unique ID from the CA
	 * @throws org.eniware.central.security.SecurityException
	 *         if the active user is not available
	 */
	String submitRenewalRequest(final X509Certificate certificate)
			throws org.eniware.central.security.SecurityException;

}
