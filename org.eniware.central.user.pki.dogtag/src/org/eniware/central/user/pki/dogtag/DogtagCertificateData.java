/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.pki.dogtag;

import java.math.BigInteger;

/**
 * Details about a certificate in Dogtag.
 * 
 * @version 1.0
 */
public class DogtagCertificateData {

	private BigInteger id;
	private String pkcs7Chain;

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getPkcs7Chain() {
		return pkcs7Chain;
	}

	public void setPkcs7Chain(String pkcs7Chain) {
		this.pkcs7Chain = pkcs7Chain;
	}

}
