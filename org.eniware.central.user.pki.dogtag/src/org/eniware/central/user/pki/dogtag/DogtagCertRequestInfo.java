/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.pki.dogtag;

import java.net.URL;

/**
 * Details on a Dogtag CSR.
 * 
 * @version 1.0
 */
public class DogtagCertRequestInfo {

	private String requestStatus;
	private URL requestURL;
	private URL certURL;

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public URL getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(URL requestURL) {
		this.requestURL = requestURL;
	}

	public URL getCertURL() {
		return certURL;
	}

	public void setCertURL(URL certURL) {
		this.certURL = certURL;
	}

}
