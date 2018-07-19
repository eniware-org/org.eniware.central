/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.dao;

import org.eniware.central.dao.FilterableDao;
import org.eniware.central.dao.GenericDao;
import org.eniware.central.domain.EntityMatch;
import org.eniware.central.instructor.domain.InstructionFilter;
import org.eniware.central.instructor.domain.EdgeInstruction;
import org.joda.time.DateTime;

/**
 * DAO API for {@link EdgeInstruction}.
 *
 * @version 1.1
 */
public interface EdgeInstructionDao extends GenericDao<EdgeInstruction, Long>,
		FilterableDao<EntityMatch, Long, InstructionFilter> {

	/**
	 * Purge instructions that have reached a final state and are older than a
	 * given date.
	 * 
	 * @param olderThanDate
	 *        The maximum date for which to purge completed instructions.
	 * @return The number of instructions deleted.
	 */
	long purgeCompletedInstructions(DateTime olderThanDate);

}
