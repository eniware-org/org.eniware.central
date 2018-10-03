/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.Filter;

/**
 * Generic object search criteria API.
 * @version $Revision$
 */
public interface ObjectCriteria<T extends Filter> {

	/**
	 * Search filter boolean join types.
	 */
	enum JoinType {
		
		/** Join all enclosed AttributeSearchFilter objects with a logical AND (default join mode). */
		AND,
		
		/** Join all enclosed AttributeSearchFilter objects with a logical OR. */
		OR,
		
		/** Join all enclosed AttributeSearchFilter objects with a logical NOT. */
		NOT;
		
		@Override
		public String toString() {
			switch (this) {
				case AND: return "&";
				case OR: return "|";
				case NOT: return "!";
				default: throw new AssertionError(this);
			}
		}
	}
	
	/**
	 * Search match types.
	 */
	enum MatchType {

		/** Match exactly this attribute value. */
		EQUAL,

		/** Match anything but exactly this attribute value. */
		NOT_EQUAL,

		/** Match attribute values less than this attribute value. */
		LESS_THAN,

		/** Match attribute values less than or equal to this attribute value. */
		LESS_THAN_EQUAL,

		/** Match attribute values greater than this attribute value. */
		GREATER_THAN,

		/** Match attribute values greater than or equal to this attribute value. */
		GREATER_THAN_EQUAL,

		/** Match a substring (this attribute value) within attribute values. */
		SUBSTRING,

		/** Match a substring (this attribute value) at the start of an attribute value. */
		SUBSTRING_AT_START,

		/** Match if the attribute name is present, regardless of its value. */
		PRESENT,

		/** Approximately match the attribute value to this attribute value. */
		APPROX,
		
		/** For array comparison, an overlap operator. */
		OVERLAP;

		@Override
		public String toString() {
			switch (this) {
				case EQUAL: return "=";
				case NOT_EQUAL: return "<>";
				case LESS_THAN: return "<";
				case LESS_THAN_EQUAL: return "<=";
				case GREATER_THAN: return ">";
				case GREATER_THAN_EQUAL: return ">=";
				case SUBSTRING: return "**";
				case SUBSTRING_AT_START: return "*";
				case PRESENT: return "?";
				case APPROX: return "~";
				case OVERLAP: return "&&";
				default: throw new AssertionError(this);
			}
		}
	}
	
	/**
	 * Get a simple filter object.
	 * 
	 * @return simple filter objecct
	 */
	T getSimpleFilter();
	
	/**
	 * Get the simple filter join type.
	 * 
	 * @return join type
	 */
	JoinType getSimpleJoinType();
	
	/**
	 * Get the simple filter match type.
	 * 
	 * @return match type
	 */
	MatchType getSimpleMatchType();
	
	/**
	 * Get a result offset.
	 * 
	 * @return result offset
	 */
	Integer getResultOffset();
	
	/**
	 * Get the maximum number of results.
	 * 
	 * @return result max
	 */
	Integer getResultMax();
	
}
