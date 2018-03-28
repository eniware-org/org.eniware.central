/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import org.eniware.central.dao.SolarNodeDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.domain.SolarNode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link SolarNodeDao}.
 * @version 1.0
 */
public class MyBatisSolarNodeDao extends BaseMyBatisGenericDao<SolarNode, Long> implements SolarNodeDao {

	/** The query name used for {@link #getUnusedNodeId(String)}. */
	public static final String QUERY_FOR_NEXT_NODE_ID = "get-next-node-id";

	/**
	 * Default constructor.
	 */
	public MyBatisSolarNodeDao() {
		super(SolarNode.class, Long.class);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public Long getUnusedNodeId() {
		return getSqlSession().selectOne(QUERY_FOR_NEXT_NODE_ID);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Long store(SolarNode datum) {
		// because we allow the node ID to be pre-assigned (i.e. from a
		// previous call to getUnusedNodeId() we have to test if the node
		// ID exists in the database yet, and if so perform an update, 
		// otherwise perform an insert

		if ( datum.getId() != null ) {
			SolarNode entity = get(datum.getId());
			if ( entity == null ) {
				// insert here
				preprocessInsert(datum);
				getSqlSession().insert(getInsert(), datum);
			} else {
				// update here
				getSqlSession().update(getUpdate(), datum);
			}
			return datum.getId();
		}

		// assign new ID now
		Long id = getUnusedNodeId();
		datum.setId(id);
		preprocessInsert(datum);
		getSqlSession().insert(getInsert(), datum);
		return id;
	}

}
