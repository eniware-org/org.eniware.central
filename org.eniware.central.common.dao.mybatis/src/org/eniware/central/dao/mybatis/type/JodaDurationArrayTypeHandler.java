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
import java.sql.Types;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;

/**
 * {@link TypeHandler} for {@link DateTime} objects mapped to {@code TIMESTAMP}
 * columns.
 * 
 * @author matt
 * @version 1.0
 */
public class JodaDurationArrayTypeHandler extends BaseArrayTypeHandler {

	/**
	 * Default constructor.
	 */
	public JodaDurationArrayTypeHandler() {
		super("interval");
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
			throws SQLException {
		if ( parameter == null ) {
			ps.setNull(i, Types.ARRAY);
		} else {
			Object[] input = (Object[]) parameter;
			String[] output = new String[input.length];
			for ( int j = 0, len = input.length; j < len; j++ ) {
				output[j] = JodaDurationTypeHandler.getDuration(input[j]);
			}

			Connection conn = ps.getConnection();
			Array loc = conn.createArrayOf(elementJdbcType, output);
			ps.setArray(i, loc);
		}
	}

}
