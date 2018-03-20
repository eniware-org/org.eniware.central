/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill;

import java.io.IOException;
import java.nio.charset.Charset;

import org.eniware.central.security.SecurityException;
import org.eniware.central.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.util.Base64Utils;

/**
 * Authorization interceptor for Killbill.
 * 
 * @author matt
 * @version 1.0
 */
public class KillbillAuthorizationInterceptor implements ClientHttpRequestInterceptor {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	/** The Killbill API key HTTP header name. */
	public static final String API_KEY_HEADER_NAME = "X-Killbill-ApiKey";

	/** The Killbill API secret HTTP header name. */
	public static final String API_SECRET_HEADER_NAME = "X-Killbill-ApiSecret";

	/** The Killbill "created by" HTTP header name. */
	public static final String CREATED_BY_HEADER_NAME = "X-Killbill-CreatedBy";

	/** The default "created by" HTTP header value. */
	public static final String DEFAULT_CREATED_BY = "SolarUser";

	private final String auth;
	private final String apiKey;
	private final String apiSecret;
	private String defaultCreatedBy = DEFAULT_CREATED_BY;

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Create a new interceptor which adds a BASIC authorization header for the
	 * given username and password.
	 * 
	 * @param username
	 *        the username to use
	 * @param password
	 *        the password to use
	 * @param apiKey
	 *        the API key to use
	 * @param apiSecret
	 *        the API secret to use
	 */
	public KillbillAuthorizationInterceptor(String username, String password, String apiKey,
			String apiSecret) {
		assert username != null && username.length() > 0;
		this.auth = Base64Utils
				.encodeToString((username + ":" + (password != null ? password : "")).getBytes(UTF_8));
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution) throws IOException {
		request.getHeaders().add("Authorization", "Basic " + this.auth);
		request.getHeaders().set(API_KEY_HEADER_NAME, this.apiKey);
		request.getHeaders().set(API_SECRET_HEADER_NAME, this.apiSecret);

		// for POST/PUT methods include the CreatedBy header
		if ( !HttpMethod.GET.equals(request.getMethod()) ) {
			String createdBy = this.defaultCreatedBy;
			try {
				Authentication auth = SecurityUtils.getCurrentAuthentication();
				if ( auth != null ) {
					createdBy = auth.getName();
				}
			} catch ( SecurityException e ) {
				log.debug("User not available for CreatedBy header");
			}
			request.getHeaders().set(CREATED_BY_HEADER_NAME, createdBy);
		}

		return execution.execute(request, body);
	}

	/**
	 * The default value to use for the {@literal X-Killbill-CreatedBy} HTTP
	 * header.
	 * 
	 * @param defaultCreatedBy
	 *        the header value to set
	 */
	public void setDefaultCreatedBy(String defaultCreatedBy) {
		this.defaultCreatedBy = defaultCreatedBy;
	}

}
