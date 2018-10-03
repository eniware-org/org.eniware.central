/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */
package org.eniware.central.security;

/**
 * API for an authenticated Edge security details.

 * @version 1.0
 */
public interface SecurityEdge extends SecurityActor {

	/**
	 * Get the Edge's ID.
	 * 
	 * @return the ID
	 */
	Long getEdgeId();

}
