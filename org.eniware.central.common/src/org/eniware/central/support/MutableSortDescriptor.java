/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import org.eniware.central.domain.SortDescriptor;

/**
 * Mutable implementation of {@link SortDescriptor}.
 * 
 * <p>
 * The {@code descending} property defaults to <em>false</em>.
 * </p>
 * @version 1.1
 */
public class MutableSortDescriptor implements SortDescriptor {

	private String sortKey;
	private boolean descending = false;

	@Override
	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	@Override
	public boolean isDescending() {
		return descending;
	}

	public void setDescending(boolean descending) {
		this.descending = descending;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
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
	 * @since 1.1
	 */
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( !(obj instanceof MutableSortDescriptor) ) {
			return false;
		}
		MutableSortDescriptor other = (MutableSortDescriptor) obj;
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
