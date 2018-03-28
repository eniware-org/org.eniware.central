/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 * $Id$
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * A match for a SourceLocation filter search.
 * @version $Revision$
 */
public interface SourceLocationMatch extends EntityMatch {

	/**
	 * Get the source name.
	 * 
	 * @return the source name
	 */
	String getSourceName();
	
	/**
	 * Get the location ID.
	 * 
	 * @return the location ID
	 */
	Long getLocationId();
	
	/**
	 * Get the location name.
	 * @return the location name
	 */
	String getLocationName();
	
}
