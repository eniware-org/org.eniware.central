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
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

/**
 * Implementation of {@link TypeHandler} for dealing with Joda Time
 * {@link LocalDateTime} objects.
 * 
 * @author matt
 * @version 1.0
 */
public class JodaLocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter,
			JdbcType jdbcType) throws SQLException {
		switch (jdbcType) {
			case DATE:
				ps.setDate(i, new java.sql.Date(parameter.toDateTime(DateTimeZone.UTC).getMillis()));
				break;

			default:
				ps.setTimestamp(i, new Timestamp(parameter.toDateTime(DateTimeZone.UTC).getMillis()));
				break;
		}
	}

	@Override
	public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnName);
		return getResult(ts);
	}

	@Override
	public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnIndex);
		return getResult(ts);
	}

	@Override
	public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Timestamp ts = cs.getTimestamp(columnIndex);
		return getResult(ts);
	}

	private LocalDateTime getResult(Timestamp ts) {
		if ( ts == null ) {
			return null;
		}
		return new LocalDateTime(ts.getTime());
	}

}
