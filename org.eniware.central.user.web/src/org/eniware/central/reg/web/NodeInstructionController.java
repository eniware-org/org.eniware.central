/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package net.solarnetwork.central.reg.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.solarnetwork.central.instructor.biz.InstructorBiz;
import net.solarnetwork.web.support.WebUtils;

import org.eniware.central.instructor.domain.Instruction;
import org.eniware.central.instructor.domain.NodeInstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for managing node instructions.
 * 
 * @version $Revision$
 */
@Controller
@RequestMapping("/instr")
public class NodeInstructionController {

	/** The model key for the primary result object. */
	public static final String MODEL_KEY_RESULT = "result";

	/** The default view name. */
	public static final String DEFAULT_VIEW_NAME = "xml";

	@Autowired private InstructorBiz instructorBiz;
	
	/**
	 * Queue a new node instruction.
	 * 
	 * @param request the servlet request
	 * @param input the instruction input
	 * @param model the model
	 * @return view name
	 */
	// FIXME: remove GET support, only for testing
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/add.*")
	public String queueInstruction(HttpServletRequest request, NodeInstruction input,
			Model model) {	
		NodeInstruction instr = instructorBiz.queueInstruction(input.getNodeId(), input);
		model.asMap().clear();
		model.addAttribute(MODEL_KEY_RESULT, instr);
		return WebUtils.resolveViewFromUrlExtension(request, null);
	}
	
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/viewActive.*")
	public String activeInstructions(HttpServletRequest request, 
			@RequestParam("nodeId") Long nodeId, Model model) {
		List<Instruction> instructions = instructorBiz.getActiveInstructionsForNode(nodeId);
		model.asMap().clear();
		model.addAttribute(MODEL_KEY_RESULT, instructions);
		return WebUtils.resolveViewFromUrlExtension(request, null);
	}
	
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/view.*")
	public String viewInstruction(HttpServletRequest request, 
			@RequestParam("id") Long instructionId, Model model) {
		Instruction instruction = instructorBiz.getInstruction(instructionId);
		model.asMap().clear();
		model.addAttribute(MODEL_KEY_RESULT, instruction);
		return WebUtils.resolveViewFromUrlExtension(request, null);
	}
	
}
