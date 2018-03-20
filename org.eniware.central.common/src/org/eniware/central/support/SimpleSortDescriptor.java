/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import org.eniware.central.domain.SortDescriptor;

/**
 * Implementation of {@link SortDescriptor}.
 * 
 * @author matt
 * @version 1.2
 */
public class SimpleSortDescriptor implements SortDescriptor {

	private final String sortKey;
	private final boolean descending;

	/**
	 * Construct with a sort key.
	 * 
	 * <p>
	 * Ascending order will be used.
	 * </p>
	 * 
	 * @param sortKey
	 *        the sort key
	 */
	public SimpleSortDescriptor(String sortKey) {
		this(sortKey, true);
	}

	public SimpleSortDescriptor(String sortKey, boolean descending) {
		super();
		this.sortKey = sortKey;
		this.descending = descending;
	}

	@Override
	public String getSortKey() {
		return sortKey;
	}

	@Override
	public boolean isDescending() {
		return descending;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @sine 1.2
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (descending ? 1231 : 1237);
		result = prime * result + ((sortKey == null) ? 0 : sortKey.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @sine 1.2
	 */
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof SimpleSortDescriptor) ) {
			return false;
		}
		SimpleSortDescriptor other = (SimpleSortDescriptor) obj;
		if ( descending != other.descending ) {
			return false;
		}
		if ( sortKey == null ) {
			if ( other.sortKey != null ) {
				return false;
			}
		} else if ( !sortKey.equals(other.sortKey) ) {
			return false;
		}
		return true;
	}

}
