/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import org.eniware.central.domain.AggregationFilter;

/**
 * Extension of {@link NodeDatumFilter} with support for aggregated values.
 * 
 * @author matt
 * @version 1.0
 */
public interface AggregateGeneralNodeDatumFilter extends GeneralNodeDatumFilter, AggregationFilter {

}
