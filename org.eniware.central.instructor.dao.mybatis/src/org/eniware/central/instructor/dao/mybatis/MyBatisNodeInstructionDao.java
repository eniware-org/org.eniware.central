/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.dao.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.eniware.central.dao.mybatis.support.BaseMyBatisFilterableDao;
import org.eniware.central.domain.EntityMatch;
import org.eniware.central.instructor.dao.NodeInstructionDao;
import org.eniware.central.instructor.domain.InstructionFilter;
import org.eniware.central.instructor.domain.InstructionParameter;
import org.eniware.central.instructor.domain.NodeInstruction;
import org.joda.time.DateTime;

/**
 * MyBatis implementation of {@link NodeInstructionDao}.
 * 
 * @author matt
 * @version 1.1
 */
public class MyBatisNodeInstructionDao extends
		BaseMyBatisFilterableDao<NodeInstruction, EntityMatch, InstructionFilter, Long> implements
		NodeInstructionDao {

	public static final String UPDATE_PURGE_COMPLETED_INSTRUCTIONS = "delete-NodeInstruction-completed";

	/**
	 * Default constructor.
	 */
	public MyBatisNodeInstructionDao() {
		super(NodeInstruction.class, Long.class, EntityMatch.class);
	}

	@Override
	protected Long handleInsert(NodeInstruction datum) {
		Long result = super.handleInsert(datum);
		handleRelation(result, datum.getParameters(), InstructionParameter.class, null);
		return result;
	}

	@Override
	public long purgeCompletedInstructions(DateTime olderThanDate) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("date", olderThanDate);
		getSqlSession().update(UPDATE_PURGE_COMPLETED_INSTRUCTIONS, params);
		Long result = (Long) params.get("result");
		return (result == null ? 0 : result.longValue());
	}
}
