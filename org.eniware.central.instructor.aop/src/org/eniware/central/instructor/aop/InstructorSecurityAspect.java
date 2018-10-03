/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
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
import org.eniware.central.instructor.dao.EdgeInstructionDao;
import org.eniware.central.instructor.domain.EdgeInstruction;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.support.AuthorizationSupport;

/**
 * Security aspect for {@link InstructorBiz}.
 * 
 * @version 1.1
 */
@Aspect
public class InstructorSecurityAspect extends AuthorizationSupport {

	private final EdgeInstructionDao EdgeInstructionDao;

	/**
	 * Constructor.
	 * 
	 * @param userEdgeDao
	 *        the UserEdgeDao to use
	 */
	public InstructorSecurityAspect(UserEdgeDao userEdgeDao, EdgeInstructionDao EdgeInstructionDao) {
		super(userEdgeDao);
		this.EdgeInstructionDao = EdgeInstructionDao;
	}

	// Hmm, can't use execution(* org.eniware.central.instructor.biz.InstructorBiz.getActiveInstructionsForEdge(..))
	// because end up with AspectJ exception "can't determine superclass of missing type 
	// org.eniware.central.instructor.aop.InstructorSecurityAspect" which is being thrown because the OSGi
	// base ClassLoader is somehow being used after trying to inspect the osgi:service exporting the
	// advised bean. All very strange, and I've given up trying to figure it out, after finding tweaking
	// the execution() expression lets the whole thing work.
	@Pointcut("bean(aop*) && execution(* org.eniware.central.instructor.biz.*.get*ForEdge(..)) && args(EdgeId)")
	public void instructionsForEdge(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.instructor.biz.*.get*ForEdges(..)) && args(EdgeIds)")
	public void instructionsForEdges(Set<Long> EdgeIds) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.instructor.biz.*.queueInstruction(..)) && args(EdgeId,..)")
	public void queueInstruction(Long EdgeId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.instructor.biz.*.queueInstructions(..)) && args(EdgeIds,..)")
	public void queueInstructions(Set<Long> EdgeIds) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.instructor.biz.*.getInstruction(..)) && args(instructionId,..)")
	public void viewInstruction(Long instructionId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.instructor.biz.*.getInstructions(..)) && args(instructionIds,..)")
	public void viewInstructions(Set<Long> instructionIds) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.instructor.biz.*.updateInstructionState(..)) && args(instructionId,..)")
	public void updateInstructionState(Long instructionId) {
	}

	@Pointcut("bean(aop*) && execution(* org.eniware.central.instructor.biz.*.updateInstructionsState(..)) && args(instructionIds,..)")
	public void updateInstructionsState(Set<Long> instructionIds) {
	}

	/**
	 * Allow the current user (or current Edge) access to Edge instructions.
	 * 
	 * @param EdgeId
	 *        the ID of the Edge to verify
	 */
	@Before("instructionsForEdge(EdgeId) || queueInstruction(EdgeId)")
	public void instructionsForEdgeCheck(Long EdgeId) {
		if ( EdgeId == null ) {
			return;
		}
		requireEdgeWriteAccess(EdgeId);
	}

	/**
	 * Allow the current user (or current Edge) access to Edge instructions.
	 * 
	 * @param EdgeIds
	 *        the IDs of the Edges to verify
	 */
	@Before("instructionsForEdges(EdgeIds) || queueInstructions(EdgeIds)")
	public void instructionsForEdgesCheck(Set<Long> EdgeIds) {
		if ( EdgeIds == null ) {
			return;
		}
		for ( Long EdgeId : EdgeIds ) {
			instructionsForEdgeCheck(EdgeId);
		}
	}

	/**
	 * Allow the current user (or current Edge) access to viewing instructions
	 * by ID.
	 * 
	 * @param instructionId
	 *        the instruction ID
	 * @param instruction
	 *        the instruction
	 */
	@AfterReturning(pointcut = "viewInstruction(instructionId)", returning = "instruction")
	public void viewInstructionAccessCheck(Long instructionId, EdgeInstruction instruction) {
		if ( instructionId == null ) {
			return;
		}
		final Long EdgeId = (instruction != null ? instruction.getEdgeId() : null);
		if ( EdgeId == null ) {
			return;
		}
		requireEdgeWriteAccess(EdgeId);
	}

	/**
	 * Allow the current user (or current Edge) access to viewing instructions
	 * by IDs.
	 * 
	 * @param instructionIds
	 *        the instruction IDs
	 * @param instruction
	 *        the instruction
	 */
	@AfterReturning(pointcut = "viewInstructions(instructionIds)", returning = "instructions")
	public void viewInstructionsAccessCheck(Set<Long> instructionIds,
			List<EdgeInstruction> instructions) {
		if ( instructionIds == null || instructions == null ) {
			return;
		}
		for ( EdgeInstruction instr : instructions ) {
			viewInstructionAccessCheck(instr.getEdgeId(), instr);
		}
	}

	/**
	 * Allow the current user (or current Edge) access to updating instructions
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
		final EdgeInstruction instruction = EdgeInstructionDao.get(instructionId);
		if ( instruction == null ) {
			return;
		}
		final Long EdgeId = instruction.getEdgeId();
		if ( EdgeId == null ) {
			return;
		}
		requireEdgeWriteAccess(EdgeId);
	}

	/**
	 * Allow the current user (or current Edge) access to updating instructions
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
