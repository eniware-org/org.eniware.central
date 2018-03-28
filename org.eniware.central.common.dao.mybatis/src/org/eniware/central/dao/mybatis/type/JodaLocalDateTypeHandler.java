/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis.type;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.LocalDate;

/**
 * Implementation of {@link TypeHandler} for dealing with Joda Time
 * {@link LocalDate} objects.
 * @version 1.0
 */
public class JodaLocalDateTypeHandler extends BaseTypeHandler<LocalDate> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setDate(i, new java.sql.Date(parameter.toDate().getTime()));
	}

	@Override
	public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Date ts = rs.getDate(columnName);
		return getResult(ts);
	}

	@Override
	public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Date ts = rs.getDate(columnIndex);
		return getResult(ts);
	}

	@Override
	public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Date ts = cs.getDate(columnIndex);
		return getResult(ts);
	}

	private LocalDate getResult(java.sql.Date d) {
		if ( d == null ) {
			return null;
		}
		return new LocalDate(d);
	}

}
