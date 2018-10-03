/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.util.Map;

import org.eniware.central.biz.AppConfigurationBiz;
import org.eniware.central.domain.AppConfiguration;
import org.eniware.util.StringUtils;

/**
 * Basic implementation of {@link AppConfigurationBiz}.
 * @version 1.0
 * @since 1.35
 */
public class BasicAppConfigurationBiz implements AppConfigurationBiz {

	private AppConfiguration appConfiguration = new SimpleAppConfiguration();

	@Override
	public AppConfiguration getAppConfiguration() {
		return appConfiguration;
	}

	/**
	 * Set the service URLs to use.
	 * 
	 * @param serviceUrls
	 *        the URLs to use
	 */
	public void setServiceUrls(Map<String, String> serviceUrls) {
		appConfiguration = new SimpleAppConfiguration(serviceUrls);
	}

	/**
	 * Set the serviice URLs to use via a string map.
	 * 
	 * @param mapping
	 *        the comma-delimited string mapping of service URLs
	 */
	public void setServiceUrlMapping(String mapping) {
		setServiceUrls(StringUtils.commaDelimitedStringToMap(mapping));
	}

}
