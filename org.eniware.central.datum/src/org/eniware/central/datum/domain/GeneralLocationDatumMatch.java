/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * A "match" to a {@link GeneralLocationDatum}.
 * 
 * <p>
 * Although this class extends {@link GeneralLocationDatum} that is merely an
 * implementation detail. Often instances of this class represent aggregated
 * data values and not actual datum entities.
 * </p>
 * 
 * @author matt
 * @version 1.0
 */
public class GeneralLocationDatumMatch extends GeneralLocationDatum implements
		GeneralLocationDatumFilterMatch {

	private static final long serialVersionUID = 6050154075058540361L;

}
