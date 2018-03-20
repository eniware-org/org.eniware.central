/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.ibatis.type.BaseTypeHandler;
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
public class JodaDateTimeTypeHandler extends BaseTypeHandler<DateTime> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType)
			throws SQLException {
		if ( jdbcType == JdbcType.DATE ) {
			ps.setDate(i, new java.sql.Date(parameter.getMillis()));
		} else {
			ps.setTimestamp(i, new Timestamp(parameter.getMillis()));
		}
	}

	@Override
	public DateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnName);
		return getResult(ts);
	}

	@Override
	public DateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnIndex);
		return getResult(ts);
	}

	@Override
	public DateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Timestamp ts = cs.getTimestamp(columnIndex);
		return getResult(ts);
	}

	private DateTime getResult(Timestamp ts) {
		if ( ts == null ) {
			return null;
		}
		return new DateTime(ts.getTime());
	}

}
