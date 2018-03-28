/* ==================================================================
 * Eniware Open sorce:Nikolai Manchev
 * Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.biz;

import java.util.Map;

import org.eniware.domain.Identifiable;

/**
 * API for a service that needs periodic maintenance performed.
 * 
 * <p>
 * This API is meant to allow a service to subscribe to a periodic task or job
 * in order to maintain implementation specific resources over time. For example
 * a service may wish to purge expired items from a cache periodically.
 * </p>
 * @version 1.0
 * @since 1.36
 */
public interface MaintenanceSubscriber extends Identifiable {

	/**
	 * Perform any required periodic maintenance.
	 * 
	 * @param parameters
	 *        job parameters, never {@literal null}
	 */
	void performServiceMaintenance(Map<String, ?> parameters);

}
