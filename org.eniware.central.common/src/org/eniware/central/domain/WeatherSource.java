/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

/**
 * A source of weather information.
 * 
 * @author matt
 * @version $Revision$
 */
public class WeatherSource extends BaseEntity implements Cloneable, Serializable {

	private static final long serialVersionUID = -5211548203098381471L;

	private String name;

	@Override
	public String toString() {
		return "WeatherSource{id=" +getId() +",name=" +this.name +'}';
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
