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
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * Text array type hanlder.
 * 
 * @author matt
 * @version 1.0
 */
public class TextArrayTypeHandler implements TypeHandler<String[]> {

	private String elementJdbcType;

	/**
	 * Default constructor.
	 */
	public TextArrayTypeHandler() {
		super();
		elementJdbcType = "text";
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType)
			throws SQLException {
		if ( parameter == null ) {
			ps.setNull(i, Types.ARRAY);
		} else {
			Connection conn = ps.getConnection();
			Array loc = conn.createArrayOf(elementJdbcType, parameter);
			ps.setArray(i, loc);
		}
	}

	@Override
	public String[] getResult(ResultSet rs, String columnName) throws SQLException {
		Array result = rs.getArray(columnName);
		return (result == null ? null : (String[]) result.getArray());
	}

	@Override
	public String[] getResult(ResultSet rs, int columnIndex) throws SQLException {
		Array result = rs.getArray(columnIndex);
		return (result == null ? null : (String[]) result.getArray());
	}

	@Override
	public String[] getResult(CallableStatement cs, int columnIndex) throws SQLException {
		Array result = cs.getArray(columnIndex);
		return (result == null ? null : (String[]) result.getArray());
	}

	public String getElementJdbcType() {
		return elementJdbcType;
	}

	public void setElementJdbcType(String elementJdbcType) {
		this.elementJdbcType = elementJdbcType;
	}

}
