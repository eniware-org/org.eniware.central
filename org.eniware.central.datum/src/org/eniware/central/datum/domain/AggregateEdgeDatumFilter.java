/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.central.domain.AggregationFilter;

/**
 * Extension of {@link EdgeDatumFilter} with support for aggregated values.
 
 * @version 1.0
 */
public interface AggregateEdgeDatumFilter extends EdgeDatumFilter, AggregationFilter {

}
