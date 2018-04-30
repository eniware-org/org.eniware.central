/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.eniware.util.SerializeIgnore;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A location entity.
 * @version 1.4
 */
public class EniwareLocation extends BaseEntity implements Cloneable, Serializable, Location,
		LocationMatch {

	private static final long serialVersionUID = -3752573628286835489L;

	private String name;
	private String country;
	private String region;
	private String stateOrProvince;
	private String locality;
	private String postalCode;
	private String street;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private BigDecimal elevation;
	private String timeZoneId;

	/**
	 * Default constructor.
	 */
	public EniwareLocation() {
		super();
	}

	/**
	 * Copy constructor for {@link Location} objects.
	 * 
	 * @param loc
	 *        the location to copy
	 */
	public EniwareLocation(Location loc) {
		super();
		setName(loc.getName());
		setCountry(loc.getCountry());
		setRegion(loc.getRegion());
		setStateOrProvince(loc.getStateOrProvince());
		setLocality(loc.getLocality());
		setPostalCode(loc.getPostalCode());
		setStreet(loc.getStreet());
		setLatitude(loc.getLatitude());
		setLongitude(loc.getLongitude());
		setElevation(loc.getElevation());
		setTimeZoneId(loc.getTimeZoneId());
	}

	/**
	 * Change values that are non-null but empty to null.
	 * 
	 * <p>
	 * This method is helpful for web form submission, to remove filter values
	 * that are empty and would otherwise try to match on empty string values.
	 * </p>
	 */
	public void removeEmptyValues() {
		if ( !StringUtils.hasText(country) ) {
			country = null;
		}
		if ( !StringUtils.hasText(locality) ) {
			locality = null;
		}
		if ( !StringUtils.hasText(name) ) {
			name = null;
		}
		if ( !StringUtils.hasText(postalCode) ) {
			postalCode = null;
		}
		if ( !StringUtils.hasText(region) ) {
			region = null;
		}
		if ( !StringUtils.hasText(stateOrProvince) ) {
			stateOrProvince = null;
		}
		if ( !StringUtils.hasText(street) ) {
			street = null;
		}
		if ( !StringUtils.hasText(timeZoneId) ) {
			timeZoneId = null;
		}
	}

	/**
	 * Return a new EniwareLocation with normalized values from another Location.
	 * 
	 * @param loc
	 *        the location to normalize
	 * @return the normalized location
	 * @since 1.3
	 */
	public static EniwareLocation normalizedLocation(Location loc) {
		assert loc != null;
		EniwareLocation norm = new EniwareLocation();
		if ( loc.getName() != null ) {
			String name = loc.getName().trim();
			if ( name.length() > 0 ) {
				norm.setName(name);
			}
		}
		if ( loc.getCountry() != null && loc.getCountry().length() >= 2 ) {
			String country = loc.getCountry();
			if ( country.length() > 2 ) {
				country = country.substring(0, 2);
			}
			norm.setCountry(country.toUpperCase());
		}
		if ( loc.getTimeZoneId() != null ) {
			TimeZone tz = TimeZone.getTimeZone(loc.getTimeZoneId());
			if ( tz != null ) {
				norm.setTimeZoneId(tz.getID());
			}
		}
		if ( loc.getRegion() != null ) {
			String region = loc.getRegion().trim();
			if ( region.length() > 0 ) {
				norm.setRegion(region);
			}
		}
		if ( loc.getStateOrProvince() != null ) {
			String state = loc.getStateOrProvince().trim();
			if ( state.length() > 0 ) {
				norm.setStateOrProvince(state);
			}
		}
		if ( loc.getLocality() != null ) {
			String locality = loc.getLocality().trim();
			if ( locality.length() > 0 ) {
				norm.setLocality(locality);
			}
		}
		if ( loc.getPostalCode() != null ) {
			String postalCode = loc.getPostalCode().trim().toUpperCase();
			if ( postalCode.length() > 0 ) {
				norm.setPostalCode(postalCode);
			}
		}
		if ( loc.getStreet() != null ) {
			String street = loc.getStreet().trim();
			if ( street.length() > 0 ) {
				norm.setStreet(street);
			}
		}
		norm.setLatitude(loc.getLatitude());
		norm.setLongitude(loc.getLongitude());
		norm.setElevation(loc.getElevation());
		return norm;
	}

	@Override
	@SerializeIgnore
	@JsonIgnore
	public Map<String, ?> getFilter() {
		Map<String, Object> filter = new LinkedHashMap<String, Object>();
		if ( name != null ) {
			filter.put("name", name);
		}
		if ( country != null ) {
			filter.put("c", country);
		}
		if ( region != null ) {
			filter.put("region", region);
		}
		if ( stateOrProvince != null ) {
			filter.put("st", stateOrProvince);
		}
		if ( postalCode != null ) {
			filter.put("postalCode", postalCode);
		}
		if ( locality != null ) {
			filter.put("l", locality);
		}
		if ( street != null ) {
			filter.put("street", street);
		}
		if ( latitude != null ) {
			filter.put("latitude", latitude);
		}
		if ( longitude != null ) {
			filter.put("longitude", longitude);
		}
		if ( elevation != null ) {
			filter.put("elevation", elevation);
		}
		if ( timeZoneId != null ) {
			filter.put("tz", timeZoneId);
		}
		return filter;
	}

	@Override
	public String toString() {
		return "EniwareLocation{id=" + (getId() == null ? "" : getId()) + ",name="
				+ (name == null ? "" : name) + '}';
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Override
	public String getStateOrProvince() {
		return stateOrProvince;
	}

	public void setStateOrProvince(String stateOrProvince) {
		this.stateOrProvince = stateOrProvince;
	}

	@Override
	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	@Override
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Override
	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	@Override
	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	@Override
	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	@Override
	public BigDecimal getElevation() {
		return elevation;
	}

	public void setElevation(BigDecimal elevation) {
		this.elevation = elevation;
	}

}
