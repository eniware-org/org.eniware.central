/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import org.eniware.central.dao.EniwareEdgeDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.domain.EniwareEdge;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link EniwareEdgeDao}.
 * @version 1.0
 */
public class MyBatisEniwareEdgeDao extends BaseMyBatisGenericDao<EniwareEdge, Long> implements EniwareEdgeDao {

	/** The query name used for {@link #getUnusedEdgeId(String)}. */
	public static final String QUERY_FOR_NEXT_Edge_ID = "get-next-Edge-id";

	/**
	 * Default constructor.
	 */
	public MyBatisEniwareEdgeDao() {
		super(EniwareEdge.class, Long.class);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public Long getUnusedEdgeId() {
		return getSqlSession().selectOne(QUERY_FOR_NEXT_Edge_ID);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Long store(EniwareEdge datum) {
		// because we allow the Edge ID to be pre-assigned (i.e. from a
		// previous call to getUnusedEdgeId() we have to test if the Edge
		// ID exists in the database yet, and if so perform an update, 
		// otherwise perform an insert

		if ( datum.getId() != null ) {
			EniwareEdge entity = get(datum.getId());
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
		Long id = getUnusedEdgeId();
		datum.setId(id);
		preprocessInsert(datum);
		getSqlSession().insert(getInsert(), datum);
		return id;
	}

}
