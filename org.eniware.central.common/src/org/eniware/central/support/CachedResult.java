/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.util.concurrent.TimeUnit;

/**
 * A cached object holder.
 * @version 1.1
 * @param <T>
 *        The type of object that is cached.
 * @deprecated use {@link org.eniware.util.CachedResult} directly
 */
@Deprecated
public class CachedResult<T> extends org.eniware.util.CachedResult<T> {

	/**
	 * Constructor. The current time will be used for the {@code created}
	 * property.
	 * 
	 * @param result
	 *        The result to cache.
	 * @param ttl
	 *        The time to live, after which the result should be considered
	 *        expired.
	 * @param unit
	 *        The time unit for the {@code expiration}.
	 */
	public CachedResult(T result, long ttl, TimeUnit unit) {
		super(result, System.currentTimeMillis(), ttl, unit);
	}

	/**
	 * Constructor.
	 * 
	 * @param result
	 *        The result to cache.
	 * @param created
	 *        The creation time to use for the result.
	 * @param ttl
	 *        The time to live, after which the result should be considered
	 *        expired.
	 * @param unit
	 *        The time unit for the {@code expiration}.
	 */
	public CachedResult(T result, long created, long ttl, TimeUnit unit) {
		super(result, created, ttl, unit);
	}

}
