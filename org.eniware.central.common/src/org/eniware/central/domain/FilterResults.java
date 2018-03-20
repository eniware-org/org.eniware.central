/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

/**
 * A filtered query results object.
 * 
 * <p>
 * This object extends {@link Iterable} but also exposes a JavaBean getter
 * property {@link #getResults()} to easy the marshaling of the results into
 * other forms.
 * </p>
 * 
 * @author matt
 * @version 1.1
 */
public interface FilterResults<T> extends Iterable<T> {

	/**
	 * Get the actual results.
	 * 
	 * <p>
	 * These are the same results returned by {@link Iterable#iterator()}.
	 * </p>
	 * 
	 * @return the results, never <em>null</em>
	 */
	Iterable<T> getResults();

	/**
	 * If available, a total number of results.
	 * 
	 * @return total results
	 */
	Long getTotalResults();

	/**
	 * Get the starting offset of the returned results.
	 * 
	 * @return the starting offset, never <em>null</em>
	 */
	Integer getStartingOffset();

	/**
	 * Get the number of results that matched the query.
	 * 
	 * @return the number of returned results, never <em>null</em>
	 */
	Integer getReturnedResultCount();

}
