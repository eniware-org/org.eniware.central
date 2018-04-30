/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * Common API for identity information in EniwareNetwork participating services.
 * @version $Revision$
 * @param <PK> the primary data type that uniquely identifies the object
 */
public interface Identity<PK> extends Comparable<PK> {

	/**
	 * Get the primary identifier of the object
	 * 
	 * @return the primary identifier
	 */
	PK getId();
	
}
