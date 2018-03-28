/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.support;

import java.io.IOException;

import org.eniware.central.support.JsonUtils;
import org.eniware.central.user.domain.UserMetadata;
import org.eniware.domain.GeneralDatumMetadata;
import org.joda.time.DateTime;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * JSON serializer for {@link UserMetadata}.
 * 
 * @version 1.1
 */
public class UserMetadataSerializer extends StdSerializer<UserMetadata> {

	private static final long serialVersionUID = -1846926348224405629L;

	public UserMetadataSerializer() {
		super(UserMetadata.class);
	}

	@Override
	public void serialize(UserMetadata meta, JsonGenerator generator, SerializerProvider provider)
			throws IOException, JsonGenerationException {
		if ( meta == null ) {
			generator.writeNull();
			return;
		}
		generator.writeStartObject();
		Long l = meta.getUserId();
		if ( l != null ) {
			generator.writeNumberField("userId", l);
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
