/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.util.Map;

/**
 * API for an aggregate (reporting) {@link GeneralEdgeDatumFilterMatch}.
 *
 * @version 1.0
 */
public interface ReportingGeneralEdgeDatumMatch extends ReportingDatum, GeneralEdgeDatumFilterMatch {

	/**
	 * Get all available sample data as a Map.
	 * 
	 * @return the sample data as a Map, or <em>null</em> if no sample data
	 *         available
	 */
	public Map<String, ?> getSampleData();

}
