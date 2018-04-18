/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web.api.v1;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import org.eniware.central.user.nim.biz.SolarNodeImageMakerBiz;
import org.eniware.util.OptionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.eniware.central.security.SecurityToken;
import org.eniware.central.security.SecurityUtils;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;

/**
 * REST API for the SolarNode Image Maker app.
 *
 * @version 1.0
 */
@RestController
@RequestMapping(value = { "/sec/nim", "/v1/sec/user/nim" })
public class SolarNodeImageMakerController extends WebServiceControllerSupport {

	private final OptionalService<SolarNodeImageMakerBiz> nimBiz;
	private int timeoutSeconds = (int) TimeUnit.MINUTES.toSeconds(5L);

	@Autowired
	public SolarNodeImageMakerController(OptionalService<SolarNodeImageMakerBiz> nimBiz) {
		super();
		this.nimBiz = nimBiz;
	}

	/**
	 * Asynchronously handle getting an authentication key for the SolarNode
	 * Image Maker.
	 * 
	 * <p>
	 * This operation may need to start up the virtual machine hosting the NIM
	 * app, and so handles the request asynchronously.
	 * </p>
	 * 
	 * @return a {@code Callable} for the authorization key to use with NIM
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/authorize")
	@ResponseBody
	public WebAsyncTask<Response<String>> getAuthorizationKey(HttpServletRequest req) {
		String reqUrl = req.getRequestURL().toString();
		SecurityToken token = SecurityUtils.getCurrentToken();

		Callable<Response<String>> task = new Callable<Response<String>>() {

			@Override
			public Response<String> call() throws Exception {
				SolarNodeImageMakerBiz biz = nimBiz.service();
				if ( biz == null ) {
					return new Response<String>(false, null, "NIM service not available", null);
				}
				String key = biz.authorizeToken(token, reqUrl);
				return Response.response(key);
			}
		};

		return new WebAsyncTask<>(TimeUnit.SECONDS.toMillis(timeoutSeconds), task);
	}

	/**
	 * Set the number of seconds to allow asynchronous handler to take.
	 * 
	 * @param timeoutSeconds
	 *        the timeout, in seconds; defaults to 5 minutes
	 */
	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

}
