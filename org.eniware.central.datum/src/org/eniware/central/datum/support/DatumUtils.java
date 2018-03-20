/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.support;

import java.math.BigDecimal;

import org.eniware.central.datum.domain.NodeDatum;
import org.eniware.central.support.JsonUtils;
import org.eniware.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities for Datum domain classes.
 * 
 * @author matt
 * @version 1.2
 */
public final class DatumUtils {

	private static final Logger LOG = LoggerFactory.getLogger(DatumUtils.class);

	// can't construct me
	private DatumUtils() {
		super();
	}

	/**
	 * Convert an object to a JSON string. This is designed for simple values.
	 * An internal {@link ObjectMapper} will be used, and null values will not
	 * be included in the output. All exceptions while serializing the object
	 * are caught and ignored.
	 * 
	 * @param o
	 *        the object to serialize to JSON
	 * @param defaultValue
	 *        a default value to use if {@code o} is <em>null</em> or if any
	 *        error occurs serializing the object to JSON
	 * @return the JSON string
	 * @since 1.1
	 * @see JsonUtils#getJSONString(Object, String)
	 */
	public static String getJSONString(final Object o, final String defaultValue) {
		return JsonUtils.getJSONString(o, defaultValue);
	}

	/**
	 * Convert a JSON string to an object. This is designed for simple values.
	 * An internal {@link ObjectMapper} will be used, and all floating point
	 * values will be converted to {@link BigDecimal} values to faithfully
	 * represent the data. All exceptions while deserializing the object are
	 * caught and ignored.
	 * 
	 * @param json
	 *        the JSON string to convert
	 * @param clazz
	 *        the type of Object to map the JSON into
	 * @return the object
	 * @since 1.1
	 * @see JsonUtils#getJSONString(Object, String)
	 */
	public static <T> T getObjectFromJSON(final String json, Class<T> clazz) {
		return JsonUtils.getObjectFromJSON(json, clazz);
	}

	/**
	 * Get a {@link NodeDatum} {@link Class} for a given name.
	 * 
	 * <p>
	 * If {@code name} contains a period, it will be treated as a
	 * fully-qualified class name. Otherwise a FQCN will be constructed as
	 * residing in the same package as {@link NodeDatum} named by capitalizing
	 * {@code name} and appending {@code Datum} to the end. For example, a
	 * {@code name} value of <em>power</em> would result in a class named
	 * {@code PowerDatum} in the same package as {@link NodeDatum} (e.g.
	 * {@code net.solarnetwork.central.datum.domain.PowerDatum}).
	 * 
	 * @param name
	 *        the node datum class name
	 * @return the class, or <em>null</em> if not available
	 */
	public static Class<? extends NodeDatum> nodeDatumClassForName(String name) {
		if ( name == null ) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		if ( name.indexOf('.') < 0 ) {
			buf.append(NodeDatum.class.getPackage().getName());
			buf.append('.');

			// fix case and append "Datum"
			name = name.toLowerCase();
			buf.append(name.substring(0, 1).toUpperCase());
			if ( name.length() > 1 ) {
				buf.append(name.substring(1));
			}
			buf.append("Datum");
		} else {
			// contains a period, so treat as FQCN
			buf.append(name);
		}
		Class<? extends NodeDatum> result = null;
		try {
			result = ClassUtils.loadClass(name, NodeDatum.class);
		} catch ( RuntimeException e ) {
			LOG.debug("Exception loading NodeDatum class {}", name, e);
		}
		return result;
	}

}
