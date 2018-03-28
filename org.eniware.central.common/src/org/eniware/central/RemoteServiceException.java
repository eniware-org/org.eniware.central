/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central;

/**
 * Exception thrown when interacting with a remote service.
 * @version 1.0
 * @since 1.36
 */
public class RemoteServiceException extends RuntimeException {

	private static final long serialVersionUID = 6050177744319149194L;

	/**
	 * Construct with a message.
	 * 
	 * @param message
	 *        the message
	 */
	public RemoteServiceException(String message) {
		super(message);
	}

	/**
	 * Construct with a nested exception.
	 * 
	 * @param cause
	 *        the cause
	 */
	public RemoteServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct with a message and nested exception.
	 * 
	 * @param message
	 *        the message
	 * @param cause
	 *        the cause
	 */
	public RemoteServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
