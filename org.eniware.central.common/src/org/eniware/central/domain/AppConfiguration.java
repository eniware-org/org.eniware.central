/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.util.Map;

/**
 * API for common application configuration elements.
 * 
 * @author matt
 * @version 1.0
 * @since 1.35
 */
public interface AppConfiguration {

	/** Service URL name for the SolarUser app. */
	String SOLARUSER_SERVICE_NAME = "solaruser";

	/** Service URL name for the SolarQuery app. */
	String SOLARQUERY_SERVICE_NAME = "solarquery";

	/**
	 * Service URL name for a user-facing "dashboard" specific to a single
	 * SolarNode.
	 * 
	 * <p>
	 * This URL can reasonably be expected to support a <code>nodeId</code>
	 * variable.
	 * </p>
	 */
	String SOALRNODE_DASHBAORD_SERVICE_NAME = "node-dashboard";

	/**
	 * Service URL name for a user-facing "dashboard" specific to a single
	 * SolarNode.
	 * 
	 * <p>
	 * This URL can reasonably be expected to support a <code>nodeId</code>
	 * variable.
	 * </p>
	 */
	String SOLARNDOE_DATAVIEW_SERVICE_NAME = "node-dataview";

	/**
	 * Get a mapping of named service URLs.
	 * 
	 * <p>
	 * The keys of the returned maps represent logical names for the associated
	 * URL values. The keys will be application-dependent, and should include
	 * values for well-defined application services. For example a URL to the
	 * application terms of service might be included under a key
	 * {@literal tos}.
	 * </p>
	 * 
	 * <p>
	 * URL values are permitted to contain <em>variables</em> in the form
	 * <code>{var}</code> that consumers of the URLs can replace with
	 * appropriate values. The variable names must be named so their intended
	 * use is obvious, for example <code>nodeId</code> for a SolarNode ID.
	 * </p>
	 * 
	 * <p>
	 * Some common service URL names are defined as constants on this interface.
	 * Implementations are recommended to use these keys when it makes sense,
	 * and to add any other values needed by the application.
	 * </p>
	 * 
	 * @return named service URLs, never {@literal null}
	 */
	Map<String, String> getServiceUrls();

}
