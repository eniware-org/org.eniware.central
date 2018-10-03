/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.util.Map;

/**
 * API for common application configuration elements.
 * @version 1.0
 * @since 1.35
 */
public interface AppConfiguration {

	/** Service URL name for the EniwareUser app. */
	String ENIWAREUSER_SERVICE_NAME = "eniwareuser";

	/** Service URL name for the EniwareQuery app. */
	String ENIWAREQUERY_SERVICE_NAME = "eniwarequery";

	/**
	 * Service URL name for a user-facing "dashboard" specific to a single
	 * EniwareEdge.
	 * 
	 * <p>
	 * This URL can reasonably be expected to support a <code>EdgeId</code>
	 * variable.
	 * </p>
	 */
	String SOALREdge_DASHBAORD_SERVICE_NAME = "Edge-dashboard";

	/**
	 * Service URL name for a user-facing "dashboard" specific to a single
	 * EniwareEdge.
	 * 
	 * <p>
	 * This URL can reasonably be expected to support a <code>EdgeId</code>
	 * variable.
	 * </p>
	 */
	String ENIWARENDOE_DATAVIEW_SERVICE_NAME = "Edge-dataview";

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
	 * use is obvious, for example <code>EdgeId</code> for a EniwareEdge ID.
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
