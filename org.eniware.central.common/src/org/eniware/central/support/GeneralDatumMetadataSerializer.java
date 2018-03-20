/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.io.IOException;

import org.eniware.domain.GeneralDatumMetadata;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * JSON serializer for {@link GeneralDatumMetadata}.
 * 
 * @author matt
 * @version 1.1
 */
public class GeneralDatumMetadataSerializer extends StdSerializer<GeneralDatumMetadata> {

	private static final long serialVersionUID = -6242470880807088734L;

	public GeneralDatumMetadataSerializer() {
		super(GeneralDatumMetadata.class);
	}

	@Override
	public void serialize(GeneralDatumMetadata meta, JsonGenerator generator,
			SerializerProvider provider) throws IOException, JsonGenerationException {
		JsonUtils.writeMetadata(generator, meta);
	}

}
