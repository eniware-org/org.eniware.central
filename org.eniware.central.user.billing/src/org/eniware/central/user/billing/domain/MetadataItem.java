/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.domain;

/**
 * A key/value style metadata object.
 * 
 * @author matt
 * @version 1.0
 */
public interface MetadataItem {

	/**
	 * Get the metadata name.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Get the metadata value.
	 * 
	 * @return the value
	 */
	String getValue();
}
