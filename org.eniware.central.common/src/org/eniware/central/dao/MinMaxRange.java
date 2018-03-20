/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

/**
 * A range specification.
 * 
 * @author matt
 * @version $Revision$
 * @param <T> the range type
 */
public interface MinMaxRange<T extends Number> {

	/**
	 * The minimum value.
	 * 
	 * @return minimum
	 */
	T getMinimum();
	
	/**
	 * The maximum value.
	 * 
	 * @return maximum
	 */
	T getMaximum();
	
}
