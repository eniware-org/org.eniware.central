/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.common.dao.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.sql.DataSource;

import org.eniware.central.domain.PingTest;
import org.eniware.central.domain.PingTestResult;

/**
 * {@link PingTest} to verify a {@link DataSource} connection is available. This
 * test expects the configured {@code query} to return a
 * {@link java.sql.Timestamp} as the first column of the query result.
 * @version 1.0
 */
public class DataSourcePingTest implements PingTest {

	private final DataSource dataSource;
	private final String query;

	public DataSourcePingTest(DataSource dataSource, String query) {
		super();
		this.dataSource = dataSource;
		this.query = query;
	}

	@Override
	public String getPingTestId() {
		return getClass().getName();
	}

	@Override
	public String getPingTestName() {
		return "JDBC Pool DataSource Connection";
	}

	@Override
	public long getPingTestMaximumExecutionMilliseconds() {
		return 500;
	}

	@Override
	public PingTestResult performPingTest() throws Exception {
		if ( dataSource == null ) {
			return new PingTestResult(false, "No DataSource configured.");
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Timestamp ts = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(true);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				ts = rs.getTimestamp(1);
				break;
			}
		} finally {
			if ( rs != null ) {
				rs.close();
			}
			if ( stmt != null ) {
				stmt.close();
			}
			if ( conn != null ) {
				conn.close();
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return new PingTestResult(ts != null, ts != null ? sdf.format(ts) : "No timestamp available.");
	}

}
