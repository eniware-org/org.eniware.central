/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis.support;

import org.apache.ibatis.session.SqlSession;

/**
 * Callback API for performing work with a {@link SqlSession}.
 * @version 1.0
 */
public interface SqlSessionCallback<T> {

	/**
	 * Perform some task with a {@link SqlSession}.
	 * 
	 * @param session
	 *        the session object
	 * @return some object (possibly <em>null</em>)
	 */
	T doWithSqlSession(SqlSession session);

}
