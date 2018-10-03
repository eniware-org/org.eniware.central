/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eniware.central.support.SourceLocationFilter;

/**
 * Extension of {@link SourceLocationFilter} to add generic type support.
 *
 * @version 1.0
 */
public class GenericSourceLocationFilter extends SourceLocationFilter {

	private static final long serialVersionUID = 3714571144435365987L;

	public enum LocationType {
		Basic, Price, Weather;
	}

	private LocationType type = LocationType.Basic;

	/**
	 * Default constructor.
	 */
	public GenericSourceLocationFilter() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param type
	 *        the type
	 * @param source
	 *        the source name
	 * @param locationName
	 *        the location name
	 */
	public GenericSourceLocationFilter(LocationType type, String source, String locationName) {
		super(source, locationName);
		this.type = type;
	}

	@Override
	public Map<String, ?> getFilter() {
		Map<String, ?> filter = super.getFilter();
		if ( type != null ) {
			Map<String, Object> f = new LinkedHashMap<String, Object>(filter);
			f.put("locationType", type);
			filter = f;
		}
		return filter;
	}

	public LocationType getType() {
		return type;
	}

	public void setType(LocationType type) {
		this.type = type;
	}

}
