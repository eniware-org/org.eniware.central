/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;


/**
 * A "match" to a {@link GeneralEdgeDatum}.
 * 
 * <p>
 * Although this class extends {@link GeneralEdgeDatum} that is merely an
 * implementation detail. Often instances of this class represent aggregated
 * data values and not actual datum entities.
 * </p>
 *
 * @version 1.0
 */
public class GeneralEdgeDatumMatch extends GeneralEdgeDatum implements GeneralEdgeDatumFilterMatch {

	private static final long serialVersionUID = 6894637498151159318L;

}
