/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;
import org.joda.time.DateTime;

/**
 * Base implementation of {@link Entity} using a comparable, serializable
 * primary key.
 * @version 1.0
 * @since 1.34
 */
public class BaseObjectEntity<PK extends Comparable<PK> & Serializable> extends BaseObjectIdentity<PK>
		implements Cloneable, Serializable, Entity<PK> {

	private static final long serialVersionUID = 6151623706137372281L;

	private DateTime created = null;
	private DateTime modified = null;

	/**
	 * Get the creation date.
	 * 
	 * @return the created
	 */
	@Override
	public DateTime getCreated() {
		return created;
	}

	/**
	 * Set the creation date.
	 * 
	 * @param created
	 *        the created to set
	 */
	public void setCreated(DateTime created) {
		this.created = created;
	}

	/**
	 * Get the modification date.
	 * 
	 * @return the modification date
	 */
	public DateTime getModified() {
		return modified;
	}

	/**
	 * Set the modification date.
	 * 
	 * @param modified
	 *        the modification date to set
	 */
	public void setModified(DateTime modified) {
		this.modified = modified;
	}

}
