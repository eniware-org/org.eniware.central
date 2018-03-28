/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package net.solarnetwork.central.reg.web.api.v1;

import static net.solarnetwork.web.domain.Response.response;

import org.eniware.central.biz.AppConfigurationBiz;
import org.eniware.central.domain.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.solarnetwork.central.web.support.WebServiceControllerSupport;
import net.solarnetwork.web.domain.Response;

/**
 * REST controller for configuration API.
 * 
 * @version 1.0
 */
@RestController("v1ConfigurationController")
@RequestMapping(value = { "/config", "/v1/pub/config", "/v1/sec/config" })
public class ConfigurationController extends WebServiceControllerSupport {

	private final AppConfigurationBiz appConfigurationBiz;

	/**
	 * Constructor.
	 * 
	 * @param appConfigurationBiz
	 *        the service to use
	 */
	@Autowired
	public ConfigurationController(AppConfigurationBiz appConfigurationBiz) {
		super();
		this.appConfigurationBiz = appConfigurationBiz;
	}

	/**
	 * Get the application configuration.
	 * 
	 * @return the app configuration response
	 */
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Response<AppConfiguration> getAppConfiguration() {
		return response(appConfigurationBiz.getAppConfiguration());
	}

}
