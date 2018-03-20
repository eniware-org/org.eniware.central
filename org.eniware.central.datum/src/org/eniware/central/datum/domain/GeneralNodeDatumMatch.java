/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;


/**
 * A "match" to a {@link GeneralNodeDatum}.
 * 
 * <p>
 * Although this class extends {@link GeneralNodeDatum} that is merely an
 * implementation detail. Often instances of this class represent aggregated
 * data values and not actual datum entities.
 * </p>
 * 
 * @author matt
 * @version 1.0
 */
public class GeneralNodeDatumMatch extends GeneralNodeDatum implements GeneralNodeDatumFilterMatch {

	private static final long serialVersionUID = 6894637498151159318L;

}
