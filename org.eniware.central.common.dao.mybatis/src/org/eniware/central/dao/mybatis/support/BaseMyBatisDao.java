/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis.support;

import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;

/**
 * Base DAO support for MyBatis implementations
 * @version 1.1
 */
public abstract class BaseMyBatisDao extends SqlSessionDaoSupport {

	/** A RowBounds instance that returns at most the first row. */
	public static final RowBounds FIRST_ROW = new RowBounds(0, 1);

	/**
	 * Select the first available result from a query. This is similar to
	 * {@link SqlSession#selectOne(String, Object)} except that the
	 * {@link BaseMyBatisGenericDao#FIRST_ROW} bounds is passed to the database.
	 * 
	 * @param statement
	 *        the name of the SQL statement to execute
	 * @param parameters
	 *        any parameters to pass to the statement
	 * @param <E>
	 *        the result type
	 * @return the first result, or <em>null</em> if none matched the query
	 */
	protected final <E> E selectFirst(String statement, Object parameters) {
		List<E> results = getSqlSession().selectList(statement, parameters, FIRST_ROW);
		if ( results.size() > 0 ) {
			return results.get(0);
		}
		return null;
	}

	/**
	 * Select a list with optional support for row bounds.
	 * 
	 * @param statement
	 *        the name of the SQL statement to execute
	 * @param parameters
	 *        any parameters to pass to the statement
	 * @param offset
	 *        a result offset, or <em>null</em> for no offset
	 * @param max
	 *        the maximum number of results, or <em>null</em> for no maximum
	 * @param <E>
	 *        the result type
	 * @return the first result, or <em>null</em> if none matched the query
	 */
	protected final <E> List<E> selectList(final String statement, Object parameters, Integer offset,
			Integer max) {
		List<E> rows = null;
		if ( max != null && max > 0 ) {
			rows = getSqlSession().selectList(statement, parameters, new RowBounds(
					(offset == null || offset.intValue() < 0 ? 0 : offset.intValue()), max));
		} else {
			rows = getSqlSession().selectList(statement, parameters);
		}
		return rows;
	}

	/**
	 * Execute a {@code SELECT} query that returns a single long value.
	 * 
	 * @param statement
	 *        the name of the SQL statement to execute
	 * @param parameters
	 *        any parameters to pass to the statement
	 * @return the result as a long, or {@literal null}
	 * @since 1.1
	 */
	protected Long selectLong(final String statement, final Object parameters) {
		Number n = getSqlSession().selectOne(statement, parameters);
		if ( n != null ) {
			return n.longValue();
		}
		return null;
	}

}
