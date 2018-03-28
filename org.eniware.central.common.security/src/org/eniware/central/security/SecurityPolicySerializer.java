/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.eniware.central.domain.Aggregation;
import org.eniware.central.domain.LocationPrecision;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * JSON serializer for {@link SecurityPolicy}.

 * @version 1.2
 */
public class SecurityPolicySerializer extends StdSerializer<SecurityPolicy> {

	private static final long serialVersionUID = -5542903806671694581L;

	public SecurityPolicySerializer() {
		super(SecurityPolicy.class);
	}

	@Override
	public void serialize(SecurityPolicy policy, JsonGenerator generator, SerializerProvider provider)
			throws IOException, JsonGenerationException {
		if ( policy == null ) {
			generator.writeNull();
			return;
		}
		generator.writeStartObject();
		if ( policy.getNodeIds() != null && !policy.getNodeIds().isEmpty() ) {
			generator.writeArrayFieldStart("nodeIds");

			// maintain node IDs in natural sort order
			Long[] ids = policy.getNodeIds().toArray(new Long[policy.getNodeIds().size()]);
			Arrays.sort(ids);
			for ( Long id : ids ) {
				generator.writeNumber(id);
			}

			generator.writeEndArray();
		}
		if ( policy.getSourceIds() != null && !policy.getSourceIds().isEmpty() ) {
			generator.writeArrayFieldStart("sourceIds");
			for ( String id : policy.getSourceIds() ) {
				generator.writeString(id);
			}
			generator.writeEndArray();
		}

		Set<Aggregation> aggregations = policy.getAggregations();
		if ( policy.getMinAggregation() != null ) {
			generator.writeStringField("minAggregation", policy.getMinAggregation().name());
		} else if ( aggregations != null && !aggregations.isEmpty() ) {
			generator.writeArrayFieldStart("aggregations");
			for ( Aggregation val : aggregations ) {
				generator.writeString(val.name());
			}
			generator.writeEndArray();
		}

		Set<LocationPrecision> locationPrecisions = policy.getLocationPrecisions();
		if ( policy.getMinLocationPrecision() != null ) {
			generator.writeStringField("minLocationPrecision", policy.getMinLocationPrecision().name());
		} else if ( locationPrecisions != null && !locationPrecisions.isEmpty() ) {
			generator.writeArrayFieldStart("locationPrecisions");
			for ( LocationPrecision val : locationPrecisions ) {
				generator.writeString(val.name());
			}
			generator.writeEndArray();
		}

		Set<String> nodeMetadataPaths = policy.getNodeMetadataPaths();
		if ( nodeMetadataPaths != null && !nodeMetadataPaths.isEmpty() ) {
			generator.writeArrayFieldStart("nodeMetadataPaths");
			for ( String path : nodeMetadataPaths ) {
				generator.writeString(path);
			}
			generator.writeEndArray();
		}

		Set<String> userMetadataPaths = policy.getUserMetadataPaths();
		if ( userMetadataPaths != null && !userMetadataPaths.isEmpty() ) {
			generator.writeArrayFieldStart("userMetadataPaths");
			for ( String path : userMetadataPaths ) {
				generator.writeString(path);
			}
			generator.writeEndArray();
		}

		generator.writeEndObject();

	}

}
