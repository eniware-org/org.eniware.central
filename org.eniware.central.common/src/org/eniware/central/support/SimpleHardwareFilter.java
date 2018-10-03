/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eniware.central.domain.HardwareFilter;
import org.eniware.util.SerializeIgnore;

/**
 * Simple implementation of {@link HardwareFilter}.
 * 
 * @Nikolai Manchev
 * @version $Revision$
 */
public class SimpleHardwareFilter implements HardwareFilter {

	private Long hardwareId;
	private String name;
	
	@Override
	@SerializeIgnore
	public Map<String, ?> getFilter() {
		Map<String, Object> f = new LinkedHashMap<String, Object>(2);
		if ( name != null ) {
			f.put("name", name);
		}
		if ( hardwareId != null ) {
			f.put("hardwareId", hardwareId);
		}
		return f;
	}

	@Override
	public Long getHardwareId() {
		return hardwareId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setHardwareId(Long hardwareId) {
		this.hardwareId = hardwareId;
	}
	public void setName(String name) {
		this.name = name;
	}

}
