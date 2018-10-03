/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.io.IOException;

import org.eniware.central.domain.EdgeMetadata;
import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * JSON serializer for {@link EdgeMetadata}.
 * @version 1.1
 */
public class EdgeMetadataSerializer extends StdSerializer<EdgeMetadata> {

	private static final long serialVersionUID = 6524627619550315956L;

	public EdgeMetadataSerializer() {
		super(EdgeMetadata.class);
	}

	@Override
	public void serialize(EdgeMetadata meta, JsonGenerator generator, SerializerProvider provider)
			throws IOException, JsonGenerationException {
		if ( meta == null ) {
			generator.writeNull();
			return;
		}
		generator.writeStartObject();
		Long l = meta.getEdgeId();
		if ( l != null ) {
			generator.writeNumberField("EdgeId", l);
		}

		DateTime dt = meta.getCreated();
		if ( dt != null ) {
			generator.writeObjectField("created", dt);
		}

		dt = meta.getUpdated();
		if ( dt != null ) {
			generator.writeObjectField("updated", dt);
		}

		GeneralDatumMetadata metadata = meta.getMetadata();
		if ( metadata != null ) {
			JsonUtils.writeMetadata(generator, metadata);
		}

		generator.writeEndObject();

	}

}
