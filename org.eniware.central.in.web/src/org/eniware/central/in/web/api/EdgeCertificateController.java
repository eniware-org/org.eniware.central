/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web.api;

import static org.eniware.web.domain.Response.response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.eniware.central.user.biz.RegistrationBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;

/**
 * Controller for Edge certificate API actions.
 *
 * @version 1.0
 */
@Controller("v1EdgeCertificateController")
@RequestMapping(value = "/api/v1/sec/cert")
public class EdgeCertificateController extends WebServiceControllerSupport {

	private final RegistrationBiz registrationBiz;

	/**
	 * Constructor.
	 * 
	 * @param regBiz
	 *        the RegistrationBiz to use
	 */
	@Autowired
	public EdgeCertificateController(RegistrationBiz regBiz) {
		super();
		this.registrationBiz = regBiz;
	}

	/**
	 * Renew a Edge's certificate, saving the entire keystore on the server. The
	 * renewal will be processed asynchronously, and Edges can pick up the
	 * renewed certificate via the same process as implemented by
	 * {@link RegistrationBiz#renewEdgeCertificate(org.eniware.central.user.domain.UserEdge, String)}.
	 * 
	 * @param keystorePassword
	 *        The password for the keystore.
	 * @param keystore
	 *        The PKCS12 keystore data with the existing Edge private/public
	 *        keys and signed certificate.
	 * @return the result
	 */
	@RequestMapping(value = "/renew", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public Response<Object> renewActiveCert(@RequestParam("password") String keystorePassword,
			@RequestPart("keystore") MultipartFile keystore) {
		try {
			registrationBiz.renewEdgeCertificate(keystore.getInputStream(), keystorePassword);
			return response(null);
		} catch ( IOException e ) {
			log.debug("IOException renewing certificate", e);
			return new Response<Object>(Boolean.FALSE, null, e.getMessage(), null);
		}
	}

	/**
	 * Renew a Edge's certificate, saving the entire keystore on the server. The
	 * renewal will be processed asynchronously, and Edges can pick up the
	 * renewed certificate via the same process as implemented by
	 * {@link RegistrationBiz#renewEdgeCertificate(org.eniware.central.user.domain.UserEdge, String)}.
	 * 
	 * 
	 * @param keystorePassword
	 *        The password for the keystore.
	 * @param base64Keystore
	 *        The PKCS12 keystore data, as a Base64-encoded string, with the
	 *        existing Edge private/public keys and signed certificate.
	 * @return the result
	 */
	@RequestMapping(value = "/renew", method = RequestMethod.POST, params = "keystore")
	@ResponseBody
	public Response<Object> renewActiveCert(@RequestParam("password") String keystorePassword,
			@RequestParam("keystore") String base64Keystore) {
		byte[] data = Base64.decodeBase64(base64Keystore);
		try {
			registrationBiz.renewEdgeCertificate(new ByteArrayInputStream(data), keystorePassword);
			return response(null);
		} catch ( IOException e ) {
			log.debug("IOException renewing certificate", e);
			return new Response<Object>(Boolean.FALSE, null, e.getMessage(), null);
		}
	}

}
