/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis.type;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * Base {@link org.apache.ibatis.type.TypeHandler} for SQL arrays.
 * 
 * @author matt
 * @version 1.0
 */
public abstract class BaseArrayTypeHandler extends BaseTypeHandler<Object> {

	final protected String elementJdbcType;

	/**
	 * Constructor.
	 * 
	 * @param elementJdbcType
	 *        the element JDBC type
	 */
	public BaseArrayTypeHandler(String elementJdbcType) {
		this.elementJdbcType = elementJdbcType;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
			throws SQLException {
		if ( parameter == null ) {
			ps.setNull(i, Types.ARRAY);
		} else {
			Connection conn = ps.getConnection();
			Array loc = conn.createArrayOf(elementJdbcType, (Object[]) parameter);
			ps.setArray(i, loc);
		}
	}

	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Array result = rs.getArray(columnName);
		return (result == null ? null : result.getArray());
	}

	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Array result = rs.getArray(columnIndex);
		return (result == null ? null : result.getArray());
	}

	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Array result = cs.getArray(columnIndex);
		return (result == null ? null : result.getArray());
	}

}
