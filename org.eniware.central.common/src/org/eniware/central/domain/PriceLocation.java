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
 * Information about a specific price location.
 * @version 1.2
 */
public class PriceLocation extends BaseEntity implements Cloneable, Serializable, SourceLocationMatch {

	private static final long serialVersionUID = -5102271666130994934L;

	private String name;
	private String currency;
	private String unit;
	private SolarLocation location;
	private PriceSource source;
	private String sourceData;

	@Override
	public String toString() {
		return "PriceLocation{id=" + getId() + ",name=" + this.name + ",currency=" + this.currency
				+ ",unit=" + this.unit + '}';
	}

	@Override
	public String getSourceName() {
		return (source == null ? null : source.getName());
	}

	@Override
	@SerializeIgnore
	@JsonIgnore
	public Long getLocationId() {
		return location == null ? null : location.getId();
	}

	@Override
	public String getLocationName() {
		return getName();
	}

	@SerializeIgnore
	@JsonIgnore
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@SerializeIgnore
	@JsonIgnore
	public PriceSource getSource() {
		return source;
	}

	public void setSource(PriceSource source) {
		this.source = source;
	}

	public String getSourceData() {
		return sourceData;
	}

	public void setSourceData(String sourceData) {
		this.sourceData = sourceData;
	}

	public SolarLocation getLocation() {
		return location;
	}

	public void setLocation(SolarLocation location) {
		this.location = location;
	}

}
