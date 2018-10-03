/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */
package org.eniware.central.web.domain;

import java.util.Map;

/**
 * A request envelope object.
 
 * @version 1.1
 * @deprecated use the org.eniware.web.domain.Request class directly
 */
@Deprecated
public class Request extends org.eniware.web.domain.Request {

	public Request(String username, String password, Map<String, Object> data) {
		super(username, password, data);
	}

}
