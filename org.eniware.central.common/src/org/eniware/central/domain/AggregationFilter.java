/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import org.joda.time.DateTime;

/**
 * Extension of {@link Filter} specific to aggregated results. This API designed
 * to support DAOs that want to implement both "raw" filter results and
 * "aggregate" filter results.
 * @version 1.0
 */
public interface AggregationFilter extends Filter {

	/**
	 * Get an aggregation to apply to the query.
	 * 
	 * @return the aggregation, or <em>null</em> for default
	 */
	Aggregation getAggregation();

	/**
	 * Get a start date.
	 * 
	 * @return the start date
	 */
	public DateTime getStartDate();

	/**
	 * Get an end date.
	 * 
	 * @return the end date
	 */
	public DateTime getEndDate();

}
