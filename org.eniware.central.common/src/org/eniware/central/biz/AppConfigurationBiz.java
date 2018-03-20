/* ==================================================================
 * Eniware Open sorce:Nikolai Manchev
 * Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.biz;

import org.eniware.central.domain.AppConfiguration;

/**
 * Common API for exposing configuration data within applications.
 * 
 * @author matt
 * @version 1.0
 * @since 1.35
 */
public interface AppConfigurationBiz {

	/**
	 * Get the application configuration.
	 * 
	 * @return the configuration, never {@literal null}
	 */
	AppConfiguration getAppConfiguration();

}
