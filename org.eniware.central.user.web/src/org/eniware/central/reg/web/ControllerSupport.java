/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web;

import javax.servlet.http.HttpServletResponse;
import org.eniware.central.security.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Supporting base class for other controllers.
 * 
 * @version 1.0
 */
public class ControllerSupport {

	/** A class-level logger. */
	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * AuthorizationException handler.
	 * 
	 * <p>
	 * Logs a WARN log and returns HTTP 403 (Forbidden).
	 * </p>
	 * 
	 * @param e
	 *        the exception
	 * @param res
	 *        the servlet response
	 */
	@ExceptionHandler(AuthorizationException.class)
	public void handleSecurityException(AuthorizationException e, HttpServletResponse res) {
		if ( log.isWarnEnabled() ) {
			log.warn("Authorization exception: " + e.getMessage());
		}
		res.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

}
