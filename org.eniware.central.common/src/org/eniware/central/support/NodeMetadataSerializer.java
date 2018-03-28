/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.io.IOException;

import org.eniware.central.domain.NodeMetadata;
import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * JSON serializer for {@link NodeMetadata}.
 * @version 1.1
 */
public class NodeMetadataSerializer extends StdSerializer<NodeMetadata> {

	private static final long serialVersionUID = 6524627619550315956L;

	public NodeMetadataSerializer() {
		super(NodeMetadata.class);
	}

	@Override
	public void serialize(NodeMetadata meta, JsonGenerator generator, SerializerProvider provider)
			throws IOException, JsonGenerationException {
		if ( meta == null ) {
			generator.writeNull();
			return;
		}
		generator.writeStartObject();
		Long l = meta.getNodeId();
		if ( l != null ) {
			generator.writeNumberField("nodeId", l);
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
