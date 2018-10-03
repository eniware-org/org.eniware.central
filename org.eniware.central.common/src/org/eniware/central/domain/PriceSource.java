/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

/**
 * A source of price information.
 * @version $Revision$
 */
public class PriceSource extends BaseEntity implements Cloneable, Serializable, EntityMatch {

	private static final long serialVersionUID = -583332500383425478L;

	private String name;

	@Override
	public String toString() {
		return "PriceSource{id=" +getId() +",name=" +this.name +'}';
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
