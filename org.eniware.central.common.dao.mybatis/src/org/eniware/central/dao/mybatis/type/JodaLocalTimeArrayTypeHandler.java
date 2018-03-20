/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis.type;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.LocalTime;

/**
 * Array handler for {@link LocalTime} objects stored as {@link Time} objects.
 * 
 * @author matt
 * @version 1.0
 */
public class JodaLocalTimeArrayTypeHandler extends BaseArrayTypeHandler {

	public JodaLocalTimeArrayTypeHandler() {
		super("time");
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
			throws SQLException {
		if ( parameter == null ) {
			ps.setNull(i, Types.ARRAY);
		} else {
			Object[] input = (Object[]) parameter;
			Time[] output = new Time[input.length];
			for ( int j = 0, len = input.length; j < len; j++ ) {
				output[j] = JodaLocalTimeTypeHandler.getTime(input[j]);
			}

			Connection conn = ps.getConnection();
			Array loc = conn.createArrayOf(elementJdbcType, output);
			ps.setArray(i, loc);
		}
	}
}
