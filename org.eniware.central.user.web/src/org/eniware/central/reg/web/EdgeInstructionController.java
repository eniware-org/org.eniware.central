/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eniware.central.instructor.biz.InstructorBiz;
import org.eniware.web.support.WebUtils;

import org.eniware.central.instructor.domain.Instruction;
import org.eniware.central.instructor.domain.EdgeInstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for managing Edge instructions.
 * 
 * @version $Revision$
 */
@Controller
@RequestMapping("/instr")
public class EdgeInstructionController {

	/** The model key for the primary result object. */
	public static final String MODEL_KEY_RESULT = "result";

	/** The default view name. */
	public static final String DEFAULT_VIEW_NAME = "xml";

	@Autowired private InstructorBiz instructorBiz;
	
	/**
	 * Queue a new Edge instruction.
	 * 
	 * @param request the servlet request
	 * @param input the instruction input
	 * @param model the model
	 * @return view name
	 */
	// FIXME: remove GET support, only for testing
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/add.*")
	public String queueInstruction(HttpServletRequest request, EdgeInstruction input,
			Model model) {	
		EdgeInstruction instr = instructorBiz.queueInstruction(input.getEdgeId(), input);
		model.asMap().clear();
		model.addAttribute(MODEL_KEY_RESULT, instr);
		return WebUtils.resolveViewFromUrlExtension(request, null);
	}
	
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/viewActive.*")
	public String activeInstructions(HttpServletRequest request, 
			@RequestParam("EdgeId") Long EdgeId, Model model) {
		List<Instruction> instructions = instructorBiz.getActiveInstructionsForEdge(EdgeId);
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
