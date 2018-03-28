/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import org.eniware.central.biz.AppConfigurationBiz;
import org.eniware.central.domain.AppConfiguration;

/**
 * Implementation of {@link AppConfigurationBiz} that delegates to another
 * {@link AppConfigurationBiz}. Designed for use with AOP.
 * @version 1.0
 * @since 1.35
 */
public class DelegatingAppConfigurationBiz implements AppConfigurationBiz {

	private final AppConfigurationBiz delegate;

	/**
	 * Construct with a delegate.
	 * 
	 * @param delegate
	 *        the delegate
	 */
	public DelegatingAppConfigurationBiz(AppConfigurationBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public AppConfiguration getAppConfiguration() {
		return delegate.getAppConfiguration();
	}

}
