/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package net.solarnetwork.central.reg.web.api.v1;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import net.solarnetwork.central.security.SecurityActor;
import net.solarnetwork.central.security.SecurityToken;
import net.solarnetwork.central.security.SecurityUser;
import net.solarnetwork.central.security.SecurityUtils;
import net.solarnetwork.central.web.support.WebServiceControllerSupport;
import net.solarnetwork.web.domain.Response;

/**
 * Remote authentication for nodes.
 * 
 * @author matt
 * @version 1.2
 */
@Controller("v1authenticationController")
public class AuthenticationController extends WebServiceControllerSupport {

	@ExceptionHandler(AuthenticationException.class)
	@ResponseBody
	public Response<?> handleException(AuthenticationException e, HttpServletResponse response) {
		log.debug("AuthenticationException in {} controller: {}", getClass().getSimpleName(),
				e.getMessage());
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		return new Response<Object>(Boolean.FALSE, null, e.getMessage(), null);
	}

	/**
	 * Check who the caller is.
	 * 
	 * <p>
	 * This is a convenient way to verify the credentials of a user.
	 * </p>
	 * 
	 * @return a response that details who the authenticated caller is
	 */
	@ResponseBody
	@RequestMapping(value = "/v1/sec/whoami", method = RequestMethod.GET)
	public Response<Map<String, ?>> validate() {
		SecurityActor actor = SecurityUtils.getCurrentActor();
		Map<String, Object> data = new LinkedHashMap<String, Object>(3);
		if ( actor instanceof SecurityUser ) {
			SecurityUser user = (SecurityUser) actor;
			data.put("userId", user.getUserId());
			data.put("username", user.getEmail());
			data.put("name", user.getDisplayName());
		} else if ( actor instanceof SecurityToken ) {
			SecurityToken token = (SecurityToken) actor;
			data.put("token", token.getToken());
			data.put("tokenType", token.getTokenType());
		}
		return Response.response(data);
	}

}
