/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web.api.v1;

import static org.eniware.web.domain.Response.response;
import java.util.List;
import java.util.Set;

import org.eniware.central.instructor.domain.Instruction;
import org.eniware.central.instructor.domain.InstructionState;
import org.eniware.central.instructor.domain.EdgeInstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.eniware.central.instructor.biz.InstructorBiz;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;

/**
 * Controller for Edge instruction web service API.
 * 
 * @version 1.2
 */
@Controller("v1EdgeInstructionController")
@RequestMapping(value = "/v1/sec/instr")
public class EdgeInstructionController extends WebServiceControllerSupport {

	@Autowired
	private InstructorBiz instructorBiz;

	/**
	 * View a single instruction, based on its primary key.
	 * 
	 * @param instructionId
	 *        the ID of the instruction to view
	 * @return the instruction
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET, params = "!ids")
	@ResponseBody
	public Response<Instruction> viewInstruction(@RequestParam("id") Long instructionId) {
		Instruction instruction = instructorBiz.getInstruction(instructionId);
		return response(instruction);
	}

	/**
	 * View a set of instructions, based on their primary keys.
	 * 
	 * @param instructionIds
	 *        the IDs of the instructions to view
	 * @return the instruction
	 * @since 1.2
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET, params = "ids")
	@ResponseBody
	public Response<List<EdgeInstruction>> viewInstruction(
			@RequestParam("ids") Set<Long> instructionIds) {
		List<EdgeInstruction> results = instructorBiz.getInstructions(instructionIds);
		return response(results);
	}

	/**
	 * Get a list of all active instructions for a specific Edge.
	 * 
	 * @param EdgeId
	 *        the ID of the Edge to get instructions for
	 * @return the active instructions for the Edge
	 */
	@RequestMapping(value = "/viewActive", method = RequestMethod.GET, params = "!EdgeIds")
	@ResponseBody
	public Response<List<Instruction>> activeInstructions(@RequestParam("EdgeId") Long EdgeId) {
		List<Instruction> instructions = instructorBiz.getActiveInstructionsForEdge(EdgeId);
		return response(instructions);
	}

	/**
	 * Get a list of all active instructions for a set of Edges.
	 * 
	 * @param EdgeIds
	 *        the IDs of the Edges to get instructions for
	 * @return the active instructions for the Edges
	 * @since 1.2
	 */
	@RequestMapping(value = "/viewActive", method = RequestMethod.GET, params = "EdgeIds")
	@ResponseBody
	public Response<List<EdgeInstruction>> activeInstructions(
			@RequestParam("EdgeIds") Set<Long> EdgeIds) {
		List<EdgeInstruction> instructions = instructorBiz.getActiveInstructionsForEdges(EdgeIds);
		return response(instructions);
	}

	/**
	 * Get a list of all pending instructions for a specific Edge.
	 * 
	 * @param EdgeId
	 *        the ID of the Edge to get instructions for
	 * @return the pending instructions for the Edge
	 * @since 1.1
	 */
	@RequestMapping(value = "/viewPending", method = RequestMethod.GET, params = "!EdgeIds")
	@ResponseBody
	public Response<List<Instruction>> pendingInstructions(@RequestParam("EdgeId") Long EdgeId) {
		List<Instruction> instructions = instructorBiz.getPendingInstructionsForEdge(EdgeId);
		return response(instructions);
	}

	/**
	 * Get a list of all pending instructions for a set of Edges.
	 * 
	 * @param EdgeIds
	 *        the IDs of the Edges to get instructions for
	 * @return the pending instructions for the Edges
	 * @since 1.2
	 */
	@RequestMapping(value = "/viewPending", method = RequestMethod.GET, params = "EdgeIds")
	@ResponseBody
	public Response<List<EdgeInstruction>> pendingInstructions(
			@RequestParam("EdgeIds") Set<Long> EdgeIds) {
		List<EdgeInstruction> instructions = instructorBiz.getPendingInstructionsForEdges(EdgeIds);
		return response(instructions);
	}

	/**
	 * Enqueue a new instruction.
	 * 
	 * @param input
	 *        the instruction data to add to the queue
	 * @return the Edge instruction
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, params = "!EdgeIds")
	@ResponseBody
	public Response<EdgeInstruction> queueInstruction(EdgeInstruction input) {
		EdgeInstruction instr = instructorBiz.queueInstruction(input.getEdgeId(), input);
		return response(instr);
	}

	/**
	 * Enqueue one instruction for multiple Edges.
	 * 
	 * @param EdgeIds
	 *        a set of Edge IDs to enqueue the instruction on
	 * @param input
	 *        the instruction data to add to the queue
	 * @return the Edge instructions
	 * @since 1.2
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, params = "EdgeIds")
	@ResponseBody
	public Response<List<EdgeInstruction>> queueInstruction(@RequestParam("EdgeIds") Set<Long> EdgeIds,
			EdgeInstruction input) {
		List<EdgeInstruction> results = instructorBiz.queueInstructions(EdgeIds, input);
		return response(results);
	}

	/**
	 * Update the state of an existing instruction.
	 * 
	 * @param instructionId
	 *        the ID of the instruction to update
	 * @param state
	 *        the desired state
	 */
	@RequestMapping(value = "/updateState", method = RequestMethod.POST, params = "!ids")
	@ResponseBody
	public Response<EdgeInstruction> updateInstructionState(@RequestParam("id") Long instructionId,
			@RequestParam("state") InstructionState state) {
		instructorBiz.updateInstructionState(instructionId, state);
		return response(null);
	}

	/**
	 * Update the state of an existing instruction.
	 * 
	 * @param instructionIds
	 *        the IDs of the instructions to update
	 * @param state
	 *        the desired state
	 * @since 1.2
	 */
	@RequestMapping(value = "/updateState", method = RequestMethod.POST, params = "ids")
	@ResponseBody
	public Response<EdgeInstruction> updateInstructionState(
			@RequestParam("ids") Set<Long> instructionIds,
			@RequestParam("state") InstructionState state) {
		instructorBiz.updateInstructionsState(instructionIds, state);
		return response(null);
	}

}
