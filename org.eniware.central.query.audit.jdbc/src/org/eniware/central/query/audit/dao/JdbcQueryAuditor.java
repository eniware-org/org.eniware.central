/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.audit.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eniware.central.datum.domain.GeneralEdgeDatumFilter;
import org.eniware.central.datum.domain.GeneralEdgeDatumPK;
import org.eniware.central.domain.FilterMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.query.biz.QueryAuditor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * {@link QueryAuditor} implementation that uses JDBC statements to update audit
 * data.
 * 
 * @version 1.0
 */
public class JdbcQueryAuditor implements QueryAuditor {

	/** The default value for the {@code updateDelay} property. */
	public static final long DEFAULT_UPDATE_DELAY = 100;

	/** The default value for the {@code flushDelay} property. */
	public static final long DEFAULT_FLUSH_DELAY = 10000;

	/** The default value for the {@code connecitonRecoveryDelay} property. */
	public static final long DEFAULT_CONNECTION_RECOVERY_DELAY = 15000;

	/** The default value for the {@code EdgeSourceIncrementSql} property. */
	public static final String DEFAULT_Edge_SOURCE_INCREMENT_SQL = "{call eniwareagg.aud_inc_datum_query_count(?, ?, ?, ?)}";

	/**
	 * A regular expression that matches if a JDBC statement is a
	 * {@link CallableStatement}.
	 */
	public static final Pattern CALLABLE_STATEMENT_REGEX = Pattern.compile("^\\{call\\s.*\\}",
			Pattern.CASE_INSENSITIVE);

	private final JdbcOperations jdbcOps;
	private final ConcurrentMap<GeneralEdgeDatumPK, AtomicInteger> EdgeSourceCounters;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private WriterThread writerThread;
	private long updateDelay;
	private long flushDelay;
	private long connectionRecoveryDelay;
	private String EdgeSourceIncrementSql;

	/**
	 * Constructor.
	 * 
	 * @param jdbcOperations
	 *        the JDBC accessor to use
	 */
	public JdbcQueryAuditor(JdbcOperations jdbcOperations) {
		this(jdbcOperations, new ConcurrentHashMap<>(64));
	}

	/**
	 * Constructor.
	 * 
	 * @param jdbcOperations
	 *        the JDBC accessor to use
	 * @param EdgeSourceCounters
	 *        the map to use for tracking counts for Edge datum
	 */
	public JdbcQueryAuditor(JdbcOperations jdbcOperations,
			ConcurrentMap<GeneralEdgeDatumPK, AtomicInteger> EdgeSourceCounters) {
		super();
		this.jdbcOps = jdbcOperations;
		this.EdgeSourceCounters = EdgeSourceCounters;
		setConnectionRecoveryDelay(DEFAULT_CONNECTION_RECOVERY_DELAY);
		setFlushDelay(DEFAULT_FLUSH_DELAY);
		setUpdateDelay(DEFAULT_UPDATE_DELAY);
		setEdgeSourceIncrementSql(DEFAULT_Edge_SOURCE_INCREMENT_SQL);
	}

	@Override
	public <T extends FilterMatch<GeneralEdgeDatumPK>> void auditEdgeDatumFilterResults(
			GeneralEdgeDatumFilter filter, FilterResults<T> results) {
		final int returnedCount = (results.getReturnedResultCount() != null
				? results.getReturnedResultCount()
				: 0);
		// if no results, no count
		if ( results == null || returnedCount < 1 ) {
			return;
		}

		// configure date to current hour (floored)
		DateTime hour = new DateTime();
		hour = hour.withTime(hour.getHourOfDay(), 0, 0, 0);

		// try shortcut for single Edge + source
		Long[] EdgeIds = filter.getEdgeIds();
		String[] sourceIds = filter.getSourceIds();
		if ( EdgeIds != null && EdgeIds.length == 1 && sourceIds != null && sourceIds.length == 1 ) {
			GeneralEdgeDatumPK pk = EdgeDatumKey(hour, EdgeIds[0], sourceIds[0]);
			addEdgeSourceCount(pk, returnedCount);
			return;
		}

		// coalesce counts by key first to simplify inserts into counters
		Map<GeneralEdgeDatumPK, Integer> counts = new HashMap<>(returnedCount);
		for ( FilterMatch<GeneralEdgeDatumPK> result : results ) {
			GeneralEdgeDatumPK id = result.getId();
			GeneralEdgeDatumPK pk = EdgeDatumKey(hour, id.getEdgeId(), id.getSourceId());
			counts.compute(pk, (k, v) -> v == null ? 1 : v.intValue() + 1);
		}

		// insert counts
		for ( Map.Entry<GeneralEdgeDatumPK, Integer> me : counts.entrySet() ) {
			addEdgeSourceCount(me.getKey(), me.getValue());
		}
	}

	private static GeneralEdgeDatumPK EdgeDatumKey(DateTime date, Long EdgeId, String sourceId) {
		GeneralEdgeDatumPK pk = new GeneralEdgeDatumPK();
		pk.setCreated(date);
		pk.setEdgeId(EdgeId);
		pk.setSourceId(sourceId);
		return pk;
	}

	private void addEdgeSourceCount(GeneralEdgeDatumPK key, int count) {
		EdgeSourceCounters.computeIfAbsent(key, k -> new AtomicInteger(0)).addAndGet(count);
	}

	private void flushEdgeSourceData(PreparedStatement stmt) throws SQLException, InterruptedException {
		for ( Iterator<Map.Entry<GeneralEdgeDatumPK, AtomicInteger>> itr = EdgeSourceCounters.entrySet()
				.iterator(); itr.hasNext(); ) {
			Map.Entry<GeneralEdgeDatumPK, AtomicInteger> me = itr.next();
			GeneralEdgeDatumPK key = me.getKey();
			AtomicInteger counter = me.getValue();
			final int count = counter.getAndSet(0);
			if ( count < 1 ) {
				// clean out stale 0 valued counter
				itr.remove();
				continue;
			}
			try {
				stmt.setTimestamp(1, new java.sql.Timestamp(key.getCreated().getMillis()));
				stmt.setLong(2, key.getEdgeId());
				stmt.setString(3, key.getSourceId());
				stmt.setInt(4, count);
				stmt.execute();
				if ( updateDelay > 0 ) {
					Thread.sleep(updateDelay);
				}
			} catch ( SQLException | InterruptedException e ) {
				addEdgeSourceCount(key, count);
				throw e;
			} catch ( Exception e ) {
				addEdgeSourceCount(key, count);
				RuntimeException re;
				if ( e instanceof RuntimeException ) {
					re = (RuntimeException) e;
				} else {
					re = new RuntimeException("Exception flushing Edge source audit data", e);
				}
				throw re;
			}
		}
	}

	private boolean isCallableStatement(String sql) {
		Matcher m = CALLABLE_STATEMENT_REGEX.matcher(sql);
		return m.matches();
	}

	private class WriterThread extends Thread {

		private final AtomicBoolean keepGoingWithConnection = new AtomicBoolean(true);
		private final AtomicBoolean keepGoing = new AtomicBoolean(true);
		private boolean started = false;

		public boolean hasStarted() {
			return started;
		}

		public boolean isGoing() {
			return keepGoing.get();
		}

		public void reconnect() {
			keepGoingWithConnection.compareAndSet(true, false);
		}

		public void exit() {
			keepGoing.compareAndSet(true, false);
			keepGoingWithConnection.compareAndSet(true, false);
		}

		@Override
		public void run() {
			while ( keepGoing.get() ) {
				keepGoingWithConnection.set(true);
				synchronized ( this ) {
					started = true;
					this.notifyAll();
				}
				try {
					keepGoing.compareAndSet(true, jdbcOps.execute(new ConnectionCallback<Boolean>() {

						@Override
						public Boolean doInConnection(Connection con)
								throws SQLException, DataAccessException {
							con.setAutoCommit(true); // we want every execution of our loop to commit immediately
							PreparedStatement stmt = isCallableStatement(EdgeSourceIncrementSql)
									? con.prepareCall(EdgeSourceIncrementSql)
									: con.prepareStatement(EdgeSourceIncrementSql);
							do {
								try {
									if ( Thread.interrupted() ) {
										throw new InterruptedException();
									}
									flushEdgeSourceData(stmt);
									Thread.sleep(flushDelay);
								} catch ( InterruptedException e ) {
									log.info("Writer thread interrupted: exiting now.");
									return false;
								}
							} while ( keepGoingWithConnection.get() );
							return true;
						}

					}));
				} catch ( DataAccessException e ) {
					log.warn("JDBC exception with query auditing", e);
					// sleep, then try again
					try {
						Thread.sleep(connectionRecoveryDelay);
					} catch ( InterruptedException e2 ) {
						log.info("Writer thread interrupted: exiting now.");
						keepGoing.set(false);
					}
				} catch ( RuntimeException e ) {
					log.warn("Exception with query auditing", e);
				}
			}
		}

	}

	/**
	 * Cause the writing thread to re-connect to the database with a new
	 * connection.
	 */
	public synchronized void reconnectWriter() {
		if ( writerThread != null && writerThread.isGoing() ) {
			writerThread.reconnect();
		}
	}

	/**
	 * Enable writing, and wait until the writing thread is going.
	 */
	public synchronized void enableWriting() {
		if ( writerThread == null || !writerThread.isGoing() ) {
			writerThread = new WriterThread();
			writerThread.setName("JdbcQueryAuditorWriter");
			synchronized ( writerThread ) {
				writerThread.start();
				while ( !writerThread.hasStarted() ) {
					try {
						writerThread.wait(5000L);
					} catch ( InterruptedException e ) {
						// ignore
					}
				}
			}
		}
	}

	public synchronized void disableWriting() {
		if ( writerThread != null ) {
			writerThread.exit();
		}
	}

	/**
	 * Set the delay, in milliseconds, between flushing cached audit data.
	 * 
	 * @param flushDelay
	 *        the delay, in milliseconds; defaults to
	 *        {@link #DEFAULT_FLUSH_DELAY}
	 * @throws IllegalArgumentException
	 *         if {@code flushDelay} is &lt; 0
	 */
	public void setFlushDelay(long flushDelay) {
		if ( flushDelay < 0 ) {
			throw new IllegalArgumentException("flushDelay must be >= 0");
		}
		this.flushDelay = flushDelay;
	}

	/**
	 * Set the delay, in milliseconds, to wait after a JDBC connection error
	 * before trying to recover and connect again.
	 * 
	 * @param connectionRecoveryDelay
	 *        the delay, in milliseconds; defaults t[
	 *        {@link #DEFAULT_CONNECTION_RECOVERY_DELAY}
	 * @throws IllegalArgumentException
	 *         if {@code connectionRecoveryDelay} is &lt; 0
	 */
	public void setConnectionRecoveryDelay(long connectionRecoveryDelay) {
		if ( connectionRecoveryDelay < 0 ) {
			throw new IllegalArgumentException("connectionRecoveryDelay must be >= 0");
		}
		this.connectionRecoveryDelay = connectionRecoveryDelay;
	}

	/**
	 * Set the delay, in milliseconds, to wait after executing JDBC statements
	 * within a loop before executing another statement.
	 * 
	 * @param updateDelay
	 *        the delay, in milliseconds; defaults t[
	 *        {@link #DEFAULT_UPDATE_DELAY}
	 * @throws IllegalArgumentException
	 *         if {@code updateDelay} is &lt; 0
	 */
	public void setUpdateDelay(long updateDelay) {
		this.updateDelay = updateDelay;
	}

	/**
	 * The JDBC statement to execute for incrementing a count for a single date,
	 * Edge, and source.
	 * 
	 * <p>
	 * The statement must accept the following parameters:
	 * </p>
	 * 
	 * <ol>
	 * <li>timestamp - the audit date</li>
	 * <li>long - the Edge ID</li>
	 * <li>string - the source ID</li>
	 * </ol>
	 * 
	 * @param sql
	 *        the SQL statement to use; defaults to
	 *        {@link #DEFAULT_Edge_SOURCE_INCREMENT_SQL}
	 */
	public void setEdgeSourceIncrementSql(String sql) {
		if ( sql == null ) {
			throw new IllegalArgumentException("EdgeSourceIncrementSql must not be null");
		}
		if ( sql.equals(EdgeSourceIncrementSql) ) {
			return;
		}
		this.EdgeSourceIncrementSql = sql;
		reconnectWriter();
	}

}
