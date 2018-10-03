/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web.api.v1;

import static org.eniware.web.domain.Response.response;

import org.eniware.central.biz.AppConfigurationBiz;
import org.eniware.central.domain.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;

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
