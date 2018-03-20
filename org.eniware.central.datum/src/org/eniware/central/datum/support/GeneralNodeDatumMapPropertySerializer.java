/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.support;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eniware.central.datum.domain.GeneralNodeDatum;
import org.eniware.central.datum.domain.ReportingDatum;
import org.eniware.domain.GeneralNodeDatumSamples;
import org.eniware.util.PropertySerializer;
import org.eniware.util.StringUtils;

/**
 * Serialize a {@link GeneralNodeDatum} to a {@code Map}. The
 * {@link ReportingDatum} API is also supported (those properties will be added
 * to the output if a {@link GeneralNodeDatum} subclass implements that
 * interface).
 * 
 * @author matt
 * @version 1.0
 */
public class GeneralNodeDatumMapPropertySerializer implements PropertySerializer {

	@Override
	public Object serialize(Object data, String propertyName, Object propertyValue) {
		GeneralNodeDatum datum = (GeneralNodeDatum) propertyValue;
		Map<String, Object> props = new LinkedHashMap<String, Object>(8);
		props.put("created", datum.getCreated());
		if ( datum instanceof ReportingDatum ) {
			ReportingDatum rd = (ReportingDatum) datum;
			props.put("localDate", rd.getLocalDate());
			props.put("localTime", rd.getLocalTime());
		}
		props.put("nodeId", datum.getNodeId());
		props.put("sourceId", datum.getSourceId());

		GeneralNodeDatumSamples samples = datum.getSamples();
		if ( samples != null ) {
			addProps(props, samples.getInstantaneous());
			addProps(props, samples.getAccumulating());
			addProps(props, samples.getStatus());
			String tagString = StringUtils.delimitedStringFromCollection(samples.getTags(), ";");
			if ( tagString != null ) {
				props.put("tags", tagString);
			}
		}
		return props;
	}

	private void addProps(Map<String, Object> props, Map<String, ?> data) {
		if ( data == null ) {
			return;
		}
		for ( Map.Entry<String, ?> me : data.entrySet() ) {
			if ( !props.containsKey(me.getKey()) ) {
				props.put(me.getKey(), me.getValue());
			}
		}
	}

}
