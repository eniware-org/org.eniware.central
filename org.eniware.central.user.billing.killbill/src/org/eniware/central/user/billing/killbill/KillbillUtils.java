/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.billing.killbill;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eniware.util.BigDecimalStringSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Global Killbill helpers.
 * 
 * @version 1.0
 */
public final class KillbillUtils {

	/** The default payment method data to add to new accounts. */
	public static final Map<String, Object> EXTERNAL_PAMENT_METHOD_DATA = externalPaymentMethodData();

	private static Map<String, Object> externalPaymentMethodData() {
		Map<String, Object> map = new HashMap<>();
		map.put("pluginName", "__EXTERNAL_PAYMENT__");
		map.put("pluginInfo", Collections.emptyMap());
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Get an {@link ObjectMapper} instance configured with standard support for
	 * Killbill interaction.
	 * 
	 * @return an ObjectMapper
	 */
	public static final ObjectMapper defaultObjectMapper() {
		return Jackson2ObjectMapperBuilder.json().modules(new JodaModule())
				.serializerByType(BigDecimal.class, BigDecimalStringSerializer.INSTANCE)
				.serializationInclusion(Include.NON_NULL).build();
	}

}
