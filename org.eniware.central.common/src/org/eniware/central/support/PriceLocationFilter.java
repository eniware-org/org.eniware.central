/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eniware.central.domain.Location;
import org.eniware.central.domain.PriceLocation;
import org.eniware.central.domain.EniwareLocation;
import org.eniware.central.domain.SourceLocation;
import org.springframework.util.StringUtils;

/**
 * Filter for {@link PriceLocation}.
 * @version 1.1
 */
public class PriceLocationFilter extends SourceLocationFilter {

	private static final long serialVersionUID = 8489957378330089844L;

	private String currency;

	/**
	 * Default constructor.
	 */
	public PriceLocationFilter() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param source
	 *        the source name
	 * @param locationName
	 *        the location name
	 */
	public PriceLocationFilter(String source, String locationName) {
		super(source, locationName);
	}

	/**
	 * Copy constructor for a {@link SourceLocation}.
	 * 
	 * @param sourceLocation
	 *        the object to copy
	 */
	public PriceLocationFilter(SourceLocation sourceLocation) {
		super();
		setId(sourceLocation.getId());
		setSource(sourceLocation.getSource());
		Location loc = sourceLocation.getLocation();
		if ( loc instanceof EniwareLocation ) {
			setLocation((EniwareLocation) loc);
		} else {
			setLocation(new EniwareLocation(sourceLocation.getLocation()));
		}
	}

	/**
	 * Change values that are non-null but empty to null.
	 * 
	 * <p>
	 * This method is helpful for web form submission, to remove filter values
	 * that are empty and would otherwise try to match on empty string values.
	 * </p>
	 */
	@Override
	public void removeEmptyValues() {
		super.removeEmptyValues();
		if ( !StringUtils.hasText(currency) ) {
			currency = null;
		}
	}

	@Override
	public Map<String, ?> getFilter() {
		Map<String, ?> filter = super.getFilter();
		if ( currency != null ) {
			Map<String, Object> f = new LinkedHashMap<String, Object>(filter);
			if ( currency != null ) {
				f.put("currency", currency);
			}
			filter = f;
		}
		return filter;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
