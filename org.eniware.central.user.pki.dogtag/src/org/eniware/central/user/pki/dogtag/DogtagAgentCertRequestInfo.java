/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.pki.dogtag;

/**
 * Agent info on a certificate request in Dogtag.
 *
 * @version 1.0
 */
public class DogtagAgentCertRequestInfo {

	private String requestorName;
	private String requestorEmail;
	private String subjectDn;
	private String csr;

	public String getRequestorName() {
		return requestorName;
	}

	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
	}

	public String getRequestorEmail() {
		return requestorEmail;
	}

	public void setRequestorEmail(String requestorEmail) {
		this.requestorEmail = requestorEmail;
	}

	public String getSubjectDn() {
		return subjectDn;
	}

	public void setSubjectDn(String subjectDn) {
		this.subjectDn = subjectDn;
	}

	public String getCsr() {
		return csr;
	}

	public void setCsr(String csr) {
		this.csr = csr;
	}

}
