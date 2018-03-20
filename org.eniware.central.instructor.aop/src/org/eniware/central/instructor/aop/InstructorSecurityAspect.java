/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.instructor.aop;

import java.util.List;
import java.util.Set;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eniware.central.instructor.biz.InstructorBiz;
import org.eniware.central.instructor.dao.NodeInstructionDao;
import org.eniware.central.instructor.domain.NodeInstruction;
import org.eniware.central.user.dao.UserNodeDao;
import org.eniware.central.user.support.AuthorizationSupport;

/**
 * Security aspect for {@link InstructorBiz}.
 * 
 * @author matt
 * @version 1.1
 */
@Aspect
public class InstructorSecurityAspect extends AuthorizationSupport {

	private final NodeInstructionDao nodeInstructionDao;

	/**
	 * Constructor.
	 * 
	 * @param userNodeDao
	 *        the UserNodeDao to use
	 */
	public InstructorSecurityAspect(UserNodeDao userNodeDao, NodeInstructionDao nodeInstructionDao) {
		super(userNodeDao);
		this.nodeInstructionDao = nodeInstructionDao;
	}

	// Hmm, can't use execution(* net.solarnetwork.central.instructor.biz.InstructorBiz.getActiveInstructionsForNode(..))
	// because end up with AspectJ exception "can't determine superclass of missing type 
	// net.solarnetwork.central.instructor.aop.InstructorSecurityAspect" which is being thrown because the OSGi
	// base ClassLoader is somehow being used after trying to inspect the osgi:service exporting the
	// advised bean. All very strange, and I've given up trying to figure it out, after finding tweaking
	// the execution() expression lets the whole thing work.
	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.instructor.biz.*.get*ForNode(..)) && args(nodeId)")
	public void instructionsForNode(Long nodeId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.instructor.biz.*.get*ForNodes(..)) && args(nodeIds)")
	public void instructionsForNodes(Set<Long> nodeIds) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.instructor.biz.*.queueInstruction(..)) && args(nodeId,..)")
	public void queueInstruction(Long nodeId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.instructor.biz.*.queueInstructions(..)) && args(nodeIds,..)")
	public void queueInstructions(Set<Long> nodeIds) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.instructor.biz.*.getInstruction(..)) && args(instructionId,..)")
	public void viewInstruction(Long instructionId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.instructor.biz.*.getInstructions(..)) && args(instructionIds,..)")
	public void viewInstructions(Set<Long> instructionIds) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.instructor.biz.*.updateInstructionState(..)) && args(instructionId,..)")
	public void updateInstructionState(Long instructionId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.instructor.biz.*.updateInstructionsState(..)) && args(instructionIds,..)")
	public void updateInstructionsState(Set<Long> instructionIds) {
	}

	/**
	 * Allow the current user (or current node) access to node instructions.
	 * 
	 * @param nodeId
	 *        the ID of the node to verify
	 */
	@Before("instructionsForNode(nodeId) || queueInstruction(nodeId)")
	public void instructionsForNodeCheck(Long nodeId) {
		if ( nodeId == null ) {
			return;
		}
		requireNodeWriteAccess(nodeId);
	}

	/**
	 * Allow the current user (or current node) access to node instructions.
	 * 
	 * @param nodeIds
	 *        the IDs of the nodes to verify
	 */
	@Before("instructionsForNodes(nodeIds) || queueInstructions(nodeIds)")
	public void instructionsForNodesCheck(Set<Long> nodeIds) {
		if ( nodeIds == null ) {
			return;
		}
		for ( Long nodeId : nodeIds ) {
			instructionsForNodeCheck(nodeId);
		}
	}

	/**
	 * Allow the current user (or current node) access to viewing instructions
	 * by ID.
	 * 
	 * @param instructionId
	 *        the instruction ID
	 * @param instruction
	 *        the instruction
	 */
	@AfterReturning(pointcut = "viewInstruction(instructionId)", returning = "instruction")
	public void viewInstructionAccessCheck(Long instructionId, NodeInstruction instruction) {
		if ( instructionId == null ) {
			return;
		}
		final Long nodeId = (instruction != null ? instruction.getNodeId() : null);
		if ( nodeId == null ) {
			return;
		}
		requireNodeWriteAccess(nodeId);
	}

	/**
	 * Allow the current user (or current node) access to viewing instructions
	 * by IDs.
	 * 
	 * @param instructionIds
	 *        the instruction IDs
	 * @param instruction
	 *        the instruction
	 */
	@AfterReturning(pointcut = "viewInstructions(instructionIds)", returning = "instructions")
	public void viewInstructionsAccessCheck(Set<Long> instructionIds,
			List<NodeInstruction> instructions) {
		if ( instructionIds == null || instructions == null ) {
			return;
		}
		for ( NodeInstruction instr : instructions ) {
			viewInstructionAccessCheck(instr.getNodeId(), instr);
		}
	}

	/**
	 * Allow the current user (or current node) access to updating instructions
	 * by ID.
	 * 
	 * @param instructionId
	 *        the ID of the instruction being updated
	 */
	@Before("updateInstructionState(instructionId)")
	public void updateInstructionAccessCheck(Long instructionId) {
		if ( instructionId == null ) {
			return;
		}
		final NodeInstruction instruction = nodeInstructionDao.get(instructionId);
		if ( instruction == null ) {
			return;
		}
		final Long nodeId = instruction.getNodeId();
		if ( nodeId == null ) {
			return;
		}
		requireNodeWriteAccess(nodeId);
	}

	/**
	 * Allow the current user (or current node) access to updating instructions
	 * by ID.
	 * 
	 * @param instructionId
	 *        the ID of the instruction being updated
	 */
	@Before("updateInstructionsState(instructionIds)")
	public void updateInstructionsAccessCheck(Set<Long> instructionIds) {
		if ( instructionIds == null ) {
			return;
		}
		for ( Long instructionId : instructionIds ) {
			updateInstructionAccessCheck(instructionId);
		}
	}
}
