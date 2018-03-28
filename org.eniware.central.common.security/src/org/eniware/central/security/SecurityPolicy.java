/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import java.util.Set;

import org.eniware.central.domain.Aggregation;
import org.eniware.central.domain.LocationPrecision;

/**
 * API for a security policy, that is rules defining access permissions.
 
 * @version 1.1
 */
public interface SecurityPolicy {

	/**
	 * Get a set of node IDs this policy applies to.
	 * 
	 * @return set of node IDs, or {@code null}
	 */
	Set<Long> getNodeIds();

	/**
	 * Get a set of source IDs this policy applies to.
	 * 
	 * @return set of source IDs
	 */
	Set<String> getSourceIds();

	/**
	 * Get a set of aggregations this policy applies to.
	 * 
	 * @return set of aggregations
	 */
	Set<Aggregation> getAggregations();

	/**
	 * Get a minimum aggregation level this policy applies to.
	 * 
	 * @return The minimum aggregation level.
	 */
	Aggregation getMinAggregation();

	/**
	 * Get a location precision this policy applies to.
	 * 
	 * @return set of precisions
	 */
	Set<LocationPrecision> getLocationPrecisions();

	/**
	 * Get a minimum location precision this policy applies to.
	 * 
	 * @return The minimum location precision.
	 */
	LocationPrecision getMinLocationPrecision();

	/**
	 * Get a set of node metadata paths this policy applies to.
	 * 
	 * @return set of node metadata paths
	 * @since 1.1
	 */
	Set<String> getNodeMetadataPaths();

	/**
	 * Get a set of user metadata paths this policy applies to.
	 * 
	 * @return set of user metadata paths
	 * @since 1.1
	 */
	Set<String> getUserMetadataPaths();

}
