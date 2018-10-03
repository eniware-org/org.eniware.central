/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.dao.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.eniware.central.dao.mybatis.support.BaseMyBatisFilterableDao;
import org.eniware.central.domain.EntityMatch;
import org.eniware.central.instructor.dao.EdgeInstructionDao;
import org.eniware.central.instructor.domain.InstructionFilter;
import org.eniware.central.instructor.domain.InstructionParameter;
import org.eniware.central.instructor.domain.EdgeInstruction;
import org.joda.time.DateTime;

/**
 * MyBatis implementation of {@link EdgeInstructionDao}.
 * 
 * @version 1.1
 */
public class MyBatisEdgeInstructionDao extends
		BaseMyBatisFilterableDao<EdgeInstruction, EntityMatch, InstructionFilter, Long> implements
		EdgeInstructionDao {

	public static final String UPDATE_PURGE_COMPLETED_INSTRUCTIONS = "delete-EdgeInstruction-completed";

	/**
	 * Default constructor.
	 */
	public MyBatisEdgeInstructionDao() {
		super(EdgeInstruction.class, Long.class, EntityMatch.class);
	}

	@Override
	protected Long handleInsert(EdgeInstruction datum) {
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
