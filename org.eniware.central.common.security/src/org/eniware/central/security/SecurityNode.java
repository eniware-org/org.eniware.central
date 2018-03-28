/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */
package org.eniware.central.security;

/**
 * API for an authenticated node security details.

 * @version 1.0
 */
public interface SecurityNode extends SecurityActor {

	/**
	 * Get the node's ID.
	 * 
	 * @return the ID
	 */
	Long getNodeId();

}
