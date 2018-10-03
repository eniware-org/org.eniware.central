/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

/**
 * Implementation of {@link org.apache.ibatis.type.TypeHandler} for dealing with
 * Joda Time {@link Period} objects.
 * 
 * <p>
 * This implementation works by setting/getting String values of SQL INTERVAL
 * types, which are expected to be in standard ISO 8601 format.
 * </p>
 * @version 1.0
 */
public class JodaPeriodTypeHandler extends BaseTypeHandler<Period> {

	/** The ISO PeriodFormatter. */
	protected static final PeriodFormatter PERIOD_FORMAT = ISOPeriodFormat.standard();

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Period parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, PERIOD_FORMAT.print(parameter));
	}

	@Override
	public Period getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);
		return getPeriod(s);
	}

	@Override
	public Period getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String s = rs.getString(columnIndex);
		return getPeriod(s);
	}

	@Override
	public Period getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String s = cs.getString(columnIndex);
		return getPeriod(s);
	}

	private Period getPeriod(String s) {
		if ( s == null ) {
			return null;
		}
		return PERIOD_FORMAT.parsePeriod(s);
	}

}
