/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * A filter for locations based on a specific source.
 * 
 * @author matt
 * @version 1.1
 */
public interface SourceLocation extends Filter {

	/**
	 * Get a specific ID to find.
	 * 
	 * @return the ID
	 */
	Long getId();

	/**
	 * Get the source name.
	 * 
	 * @return the source name
	 */
	String getSource();

	/**
	 * A location filter.
	 * 
	 * @return the location filter
	 */
	Location getLocation();

}
