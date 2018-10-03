/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

/**
 * A "match" to a {@link GeneralEdgeDatumMetadata}.
 * 
 * <p>
 * Although this class extends {@link GeneralEdgeDatumMetadata} that is merely
 * an implementation detail. Often instances of this class represent aggregated
 * data values and not actual datum entities.
 * </p>
 *
 * @version 1.0
 */
public class GeneralEdgeDatumMetadataMatch extends GeneralEdgeDatumMetadata implements
		GeneralEdgeDatumMetadataFilterMatch {

	private static final long serialVersionUID = 8655943853504680748L;

}
