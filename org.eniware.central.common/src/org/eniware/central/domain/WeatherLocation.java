/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;

import org.eniware.util.SerializeIgnore;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Information about a specific weather location.
 * @version 1.2
 */
public class WeatherLocation extends BaseEntity implements Cloneable, Serializable, SourceLocationMatch {

	private static final long serialVersionUID = -3594930550501316172L;

	private EniwareLocation location;
	private WeatherSource source;
	private String sourceData;

	@Override
	public String toString() {
		return "WeatherLocation{id=" + getId() + ",source=" + this.source + ",location=" + this.location
				+ '}';
	}

	@Override
	public String getSourceName() {
		return source == null ? null : source.getName();
	}

	@Override
	@SerializeIgnore
	@JsonIgnore
	public Long getLocationId() {
		return location == null ? null : location.getId();
	}

	@Override
	public String getLocationName() {
		if ( location == null ) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		if ( location.getLocality() != null ) {
			buf.append(location.getLocality());
		} else if ( location.getRegion() != null ) {
			buf.append(location.getRegion());
		}
		if ( buf.length() > 0 ) {
			buf.append(", ");
		}
		buf.append(location.getCountry());
		return buf.toString();
	}

	public EniwareLocation getLocation() {
		return location;
	}

	public void setLocation(EniwareLocation location) {
		this.location = location;
	}

	@SerializeIgnore
	@JsonIgnore
	public WeatherSource getSource() {
		return source;
	}

	public void setSource(WeatherSource source) {
		this.source = source;
	}

	public String getSourceData() {
		return sourceData;
	}

	public void setSourceData(String sourceData) {
		this.sourceData = sourceData;
	}

}
