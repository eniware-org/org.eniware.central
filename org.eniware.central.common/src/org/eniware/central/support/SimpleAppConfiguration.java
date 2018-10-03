/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eniware.central.domain.AppConfiguration;

/**
 * Basic immutable implementation of {@link AppConfiguration}.
 * @version 1.0
 */
public class SimpleAppConfiguration implements AppConfiguration {

	private final Map<String, String> serviceUrls;

	/**
	 * Default constructor.
	 */
	public SimpleAppConfiguration() {
		this(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param serviceUrls
	 *        the service URLs to expose
	 */
	public SimpleAppConfiguration(Map<String, String> serviceUrls) {
		super();
		if ( serviceUrls == null || serviceUrls.isEmpty() ) {
			this.serviceUrls = Collections.emptyMap();
		} else {
			this.serviceUrls = Collections
					.unmodifiableMap(new LinkedHashMap<String, String>(serviceUrls));
		}
	}

	@Override
	public Map<String, String> getServiceUrls() {
		return serviceUrls;
	}

}
