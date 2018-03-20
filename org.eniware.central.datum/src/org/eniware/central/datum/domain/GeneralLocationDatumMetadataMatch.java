/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * A "match" to a {@link GeneralLocationDatumMetadata}.
 * 
 * <p>
 * Although this class extends {@link GeneralLocationDatumMetadata} that is
 * merely an implementation detail. Often instances of this class represent
 * aggregated data values and not actual datum entities.
 * </p>
 * 
 * @author matt
 * @version 1.0
 */
public class GeneralLocationDatumMetadataMatch extends GeneralLocationDatumMetadata implements
		GeneralLocationDatumMetadataFilterMatch {

	private static final long serialVersionUID = -7617092801981088291L;

}
