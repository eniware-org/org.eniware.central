/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eniware.central.domain.FilterResults;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Basic implementation of {@link FilterResults}.
 * 
 * @param T
 *        the result type
 * @version 1.1
 */
@JsonPropertyOrder({ "totalResults", "startingOffset", "returnedResultCount", "results" })
public class BasicFilterResults<T> implements FilterResults<T> {

	private Iterable<T> results;
	private Long totalResults;
	private Integer startingOffset;
	private Integer returnedResultCount;

	public BasicFilterResults(Iterable<T> results, Long totalResults, Integer startingOffset,
			Integer returnedResultCount) {
		super();
		this.results = results;
		this.totalResults = totalResults;
		this.startingOffset = startingOffset;
		this.returnedResultCount = returnedResultCount;
	}

	public BasicFilterResults(Iterable<T> results) {
		this(results, null, null, null);
	}

	@Override
	public Iterator<T> iterator() {
		if ( results == null ) {
			Set<T> emptyResult = Collections.emptySet();
			return emptyResult.iterator();
		}
		return results.iterator();
	}

	@Override
	public Iterable<T> getResults() {
		return results;
	}

	@Override
	public Long getTotalResults() {
		return totalResults;
	}

	@Override
	public Integer getStartingOffset() {
		return startingOffset;
	}

	@Override
	public Integer getReturnedResultCount() {
		return returnedResultCount;
	}

}
