/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0 
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * An API for specifying a collection ordering.
 * @version 1.1
 */
public interface SortDescriptor {

	/**
	 * Get the name of the value to sort by.
	 * 
	 * <p>
	 * How this value is interpreted is implementation dependent.
	 * </p>
	 * 
	 * @return the sort key
	 */
	String getSortKey();

	/**
	 * Return <em>true</em> if the sort should be in descending order, otherwise
	 * the short should be in ascending order.
	 * 
	 * @return <em>true</em> if the sort should be in descending order
	 */
	boolean isDescending();

}
