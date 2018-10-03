/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.web.domain;

/**
 * A simple service response envelope object.
 
 * @version 1.1
 * @param <T>
 *        the object type
 * @deprecated use the org.eniware.web.domain.Response class directly
 */
@Deprecated
public class Response<T> extends org.eniware.web.domain.Response<T> {

	/**
	 * Construct a successful response with no data.
	 */
	public Response() {
		super();
	}

	/**
	 * Construct a successful response with just data.
	 * 
	 * @param data
	 *        the data
	 */
	public Response(T data) {
		super(data);
	}

	/**
	 * Constructor.
	 * 
	 * @param success
	 *        flag of success
	 * @param code
	 *        optional code, e.g. error code
	 * @param message
	 *        optional descriptive message
	 * @param data
	 *        optional data in the response
	 */
	public Response(Boolean success, String code, String message, T data) {
		super(success, code, message, data);
	}

	/**
	 * Helper method to construct instance using generic return type inference.
	 * 
	 * <p>
	 * If you import this static method, then in your code you can write
	 * {@code return response(myData)} instead of
	 * {@code new Response&lt;Object&gt;(myData)}.
	 * </p>
	 * 
	 * @param data
	 *        the data
	 * @return the response
	 */
	public static <V> Response<V> response(V data) {
		return new Response<V>(data);
	}

}
