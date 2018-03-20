/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import java.util.Map;

import org.eniware.central.domain.Filter;

/**
 * Filter API for {@link User}.
 * 
 * @author matt
 * @version 1.0
 */
public interface UserFilter extends Filter {

	/**
	 * Get an email criteria.
	 * 
	 * @return the email, or {@literal null}
	 */
	public String getEmail();

	/**
	 * Get arbitrary internal data criteria.
	 * 
	 * @return the internal data criteria
	 */
	public Map<String, Object> getInternalData();

}
