/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web;

import java.beans.PropertyEditor;
import java.util.List;
import javax.annotation.Resource;

import org.eniware.central.in.biz.DataCollectorBiz;
import org.eniware.central.instructor.biz.InstructorBiz;
import org.eniware.central.security.AuthenticatedEdge;
import org.eniware.central.security.SecurityException;

import org.eniware.central.dao.EniwareEdgeDao;
import org.eniware.central.domain.EniwareEdge;
import org.eniware.central.instructor.domain.Instruction;
import org.eniware.util.JodaDateFormatEditor;
import org.eniware.util.OptionalServiceTracker;
import org.eniware.util.JodaDateFormatEditor.ParseMode;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Base class for data collector implementations.
 *
 * @version 1.1
 */
public abstract class AbstractDataCollector {

	/** The default value for the {@code viewName} property. */
	public static final String DEFAULT_VIEW_NAME = "xml";

	/** The model key for the {@code Datum} result. */
	public static final String MODEL_KEY_RESULT = "result";

	/** The model key for the collection of {@code Instruction} results. */
	public static final String MODEL_KEY_INSTRUCTIONS = "instructions";

	/** The model key for the Edge's {@code TimeZone}. */
	public static final String MODEL_KEY_Edge_TZ = "Edge-tz";

	/** The date format to use for parsing dates. */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";

	/** The date format to use for parsing dates. */
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	/** The date format to use for parsing times. */
	public static final String TIME_FORMAT = "HH:mm";

	/** A cloneable PropertyEditor for the {@link #DATE_FORMAT}. */
	protected static final JodaDateFormatEditor LOCAL_DATE_EDITOR = new JodaDateFormatEditor(
			DATE_FORMAT, ParseMode.LocalDate);

	/** A cloneable PropertyEditor for the {@link #TIME_FORMAT}. */
	protected static final JodaDateFormatEditor LOCAL_TIME_EDITOR = new JodaDateFormatEditor(
			TIME_FORMAT, ParseMode.LocalTime);

	/** A cloneable PropertyEditor for the {@link #DATE_TIME_FORMAT}. */
	protected static final JodaDateFormatEditor DATE_TIME_EDITOR = new JodaDateFormatEditor(
			DATE_TIME_FORMAT, ParseMode.DateTime);

	private DataCollectorBiz dataCollectorBiz;
	private EniwareEdgeDao eniwareEdgeDao;
	private String viewName = DEFAULT_VIEW_NAME;

	@Resource
	private OptionalServiceTracker<InstructorBiz> instructorBiz;

	/** A class-level logger. */
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Warn that POST is required.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getData() {
		return "post-required";
	}

	/**
	 * Default handling of Edge instructions.
	 * 
	 * <p>
	 * This method will use the configured {@link #getInstructorBiz()}, if
	 * available, and put the returned instructions on the {@code Model} on the
	 * {@link #MODEL_KEY_INSTRUCTIONS} key.
	 * </p>
	 * 
	 * @param EdgeId
	 *        the Edge ID to get instructions for
	 * @param model
	 *        the model
	 */
	protected void defaultHandleEdgeInstructions(Long EdgeId, Model model) {
		// look for instructions to return for the given Edge
		if ( getInstructorBiz() != null && getInstructorBiz().isAvailable() ) {
			List<Instruction> instructions = getInstructorBiz().getService()
					.getActiveInstructionsForEdge(EdgeId);
			if ( instructions.size() > 0 ) {
				model.addAttribute(MODEL_KEY_INSTRUCTIONS, instructions);
			}
		}
	}

	/**
	 * Add a EniwareEdge's TimeZone to the result model.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param model
	 *        the model
	 * @return the EniwareEdge entity
	 */
	protected EniwareEdge setupEdgeTimeZone(Long EdgeId, Model model) {
		EniwareEdge Edge = eniwareEdgeDao.get(EdgeId);
		model.asMap().remove("weatherDatum");
		if ( Edge != null ) {
			model.addAttribute(MODEL_KEY_Edge_TZ, Edge.getTimeZone());
		}
		return Edge;
	}

	/**
	 * Web binder initialization.
	 * 
	 * @param binder
	 *        the binder to initialize
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(DateTime.class, (PropertyEditor) DATE_TIME_EDITOR.clone());
	}

	/**
	 * Get the currently authenticated Edge.
	 * 
	 * @param required
	 *        <em>true</em> if AuthenticatedEdge is required, or <em>false</em>
	 *        if not
	 * @return AuthenticatedEdge
	 * @throws SecurityException
	 *         if an AuthenticatedEdge is not available and {@code required} is
	 *         <em>true</em>
	 */
	protected AuthenticatedEdge getAuthenticatedEdge(boolean required) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if ( principal instanceof AuthenticatedEdge ) {
			return (AuthenticatedEdge) principal;
		}
		if ( required ) {
			throw new SecurityException("Authenticated Edge required but not avaialble");
		}
		return null;
	}

	/**
	 * @return the eniwareEdgeDao
	 */
	public EniwareEdgeDao getEniwareEdgeDao() {
		return eniwareEdgeDao;
	}

	/**
	 * @param eniwareEdgeDao
	 *        the eniwareEdgeDao to set
	 */
	public void setEniwareEdgeDao(EniwareEdgeDao eniwareEdgeDao) {
		this.eniwareEdgeDao = eniwareEdgeDao;
	}

	/**
	 * @return the viewName
	 */
	public String getViewName() {
		return viewName;
	}

	/**
	 * @param viewName
	 *        the viewName to set
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * @return the dataCollectorBiz
	 */
	public DataCollectorBiz getDataCollectorBiz() {
		return dataCollectorBiz;
	}

	/**
	 * @param dataCollectorBiz
	 *        the dataCollectorBiz to set
	 */
	public void setDataCollectorBiz(DataCollectorBiz dataCollectorBiz) {
		this.dataCollectorBiz = dataCollectorBiz;
	}

	/**
	 * @return the instructorBiz
	 */
	public OptionalServiceTracker<InstructorBiz> getInstructorBiz() {
		return instructorBiz;
	}

	/**
	 * @param instructorBiz
	 *        the instructorBiz to set
	 */
	public void setInstructorBiz(OptionalServiceTracker<InstructorBiz> instructorBiz) {
		this.instructorBiz = instructorBiz;
	}

}
