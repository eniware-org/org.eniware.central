/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

/**
 * Exception for security errors.
 * 
 * @author matt
 * @version $Id$
 */
public class SecurityException extends RuntimeException {
	
	private static final long serialVersionUID = 4715317846353024503L;

	public SecurityException() {
		super();
	}

	public SecurityException(String msg, Throwable t) {
		super(msg, t);
	}

	public SecurityException(String msg) {
		super(msg);
	}

	public SecurityException(Throwable t) {
		super(t);
	}

}
