/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * Base class for SolarNetwork entities.
 * @version $Revision$
 */
public abstract class BaseEntity extends BaseIdentity 
implements Entity<Long>, Cloneable, Serializable {

	private static final long serialVersionUID = -8223419752036028667L;

	private DateTime created = null;

	/**
	 * @return the created
	 */
	public DateTime getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(DateTime created) {
		this.created = created;
	}

}
