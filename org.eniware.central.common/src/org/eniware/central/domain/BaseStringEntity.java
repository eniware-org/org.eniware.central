/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;
import org.joda.time.DateTime;

/**
 * Base class for SolarNetwork entities using string primary keys.
 * @version 1.0
 */
public class BaseStringEntity extends BaseStringIdentity implements Entity<String>, Cloneable,
		Serializable {

	private static final long serialVersionUID = -4431159141680300599L;

	private DateTime created = null;

	@Override
	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

}
