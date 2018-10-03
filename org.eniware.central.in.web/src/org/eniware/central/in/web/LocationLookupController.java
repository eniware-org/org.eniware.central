/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web;

import java.util.List;
import javax.validation.Valid;

import org.eniware.central.in.biz.DataCollectorBiz;
import org.eniware.web.domain.Response;

import org.eniware.central.dao.EniwareEdgeDao;
import org.eniware.central.domain.EntityMatch;
import org.eniware.central.domain.SourceLocationMatch;
import org.eniware.central.support.SourceLocationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Web access to PriceLocation data.
 *
 * @version 1.2
 */
@Controller
public class LocationLookupController {

	/** The default value for the {@code viewName} property. */
	public static final String DEFAULT_VIEW_NAME = "xml";

	/** The model key for the {@code PriceLocation} result. */
	public static final String MODEL_KEY_RESULT = "result";

	private DataCollectorBiz dataCollectorBiz;
	private String viewName = DEFAULT_VIEW_NAME;

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Default constructor.
	 */
	public LocationLookupController() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param dataCollectorBiz
	 *        the {@link DataCollectorBiz} to use
	 * @param eniwareEdgeDao
	 *        the {@link EniwareEdgeDao} to use
	 */
	@Autowired
	public LocationLookupController(DataCollectorBiz dataCollectorBiz) {
		setDataCollectorBiz(dataCollectorBiz);
	}

	private class CriteriaValidator implements Validator {

		@Override
		public boolean supports(Class<?> clazz) {
			return true;
		}

		@Override
		public void validate(Object target, Errors errors) {
			if ( target instanceof SourceLocationFilter ) {
				boolean sourceRequired = true;
				SourceLocationFilter filter = (SourceLocationFilter) target;
				if ( filter.getId() != null
						|| (target instanceof GenericSourceLocationFilter && ((GenericSourceLocationFilter) target)
								.getType() == GenericSourceLocationFilter.LocationType.Basic) ) {
					sourceRequired = false;
				}
				if ( sourceRequired ) {
					if ( !StringUtils.hasText(filter.getSourceName()) ) {
						errors.rejectValue("sourceName", "error.field.required",
								new Object[] { "sourceName" }, "Field is required.");
					}
					if ( !StringUtils.hasText(filter.getLocationName()) ) {
						errors.rejectValue("locationName", "error.field.required",
								new Object[] { "locationName" }, "Field is required.");
					}
				}
			}
		}
	}

	/**
	 * Web binder initialization.
	 * 
	 * @param binder
	 *        the binder to initialize
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(new CriteriaValidator());
		binder.setIgnoreInvalidFields(true);
	}

	/**
	 * Handle an {@link RuntimeException}.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 */
	@ExceptionHandler({ BindException.class, RuntimeException.class })
	public ModelAndView handleRuntimeException(Exception e) {
		log.error("BindException in {} controller", getClass().getSimpleName(), e);
		ModelAndView mv = new ModelAndView(getViewName(), MODEL_KEY_RESULT, new Response<Object>(false,
				null, e.getMessage(), null));
		return mv;
	}

	/**
	 * Query for a WeatherLocation.
	 * 
	 * @param criteria
	 *        the search criteria
	 * @param model
	 *        the model
	 * @return the result view name
	 */
	@RequestMapping(method = RequestMethod.GET, value = { "/weatherLocationLookup.do",
			"/u/weatherLocationLookup.do" })
	public String findWeatherLocation(@Valid SourceLocationFilter criteria, Model model) {
		List<SourceLocationMatch> matches = getDataCollectorBiz().findWeatherLocations(criteria);
		if ( matches != null ) {
			model.asMap().clear();
			model.addAttribute(MODEL_KEY_RESULT, matches);
		}
		return getViewName();
	}

	/**
	 * Query for a PriceLocation.
	 * 
	 * @param criteria
	 *        the search criteria
	 * @param model
	 *        the model
	 * @param criteriaModelKey
	 *        the model key the criteria is stored on
	 * @return the result model name
	 */
	@RequestMapping(method = RequestMethod.GET, value = { "/priceLocationLookup.do",
			"/u/priceLocationLookup.do" })
	public String findPriceLocation(@Valid SourceLocationFilter criteria, Model model) {
		List<SourceLocationMatch> matches = getDataCollectorBiz().findPriceLocations(criteria);
		if ( matches != null && matches.size() > 0 ) {
			model.asMap().clear();
			model.addAttribute(MODEL_KEY_RESULT, matches.get(0));
		}
		return getViewName();
	}

	/**
	 * Query for a PriceLocation.
	 * 
	 * @param criteria
	 *        the search criteria
	 * @param model
	 *        the model
	 * @return the result view name
	 */
	@RequestMapping(method = RequestMethod.GET, value = { "/priceLocationSearch.*",
			"/u/priceLocationSearch.*" })
	public String searchForPriceLocation(@Valid SourceLocationFilter criteria, Model model) {
		List<SourceLocationMatch> matches = getDataCollectorBiz().findPriceLocations(criteria);
		if ( matches != null && matches.size() > 0 ) {
			model.asMap().clear();
			model.addAttribute(MODEL_KEY_RESULT, matches.get(0));
		}
		return getViewName();
	}

	/**
	 * Query for any supported location type.
	 * 
	 * @param criteria
	 *        the search criteria
	 * @param model
	 *        the model
	 * @return the result view name
	 */
	@RequestMapping(method = RequestMethod.GET, value = { "/locationSearch.*", "/u/locationSearch.*" })
	public String searchForLocations(@Valid GenericSourceLocationFilter criteria, Model model) {
		List<? extends EntityMatch> matches;
		switch (criteria.getType()) {
			case Price:
				matches = getDataCollectorBiz().findPriceLocations(criteria);
				break;

			case Weather:
				matches = getDataCollectorBiz().findWeatherLocations(criteria);
				break;

			default:
				matches = getDataCollectorBiz().findLocations(criteria.getLocation());
				break;
		}
		if ( matches != null && matches.size() > 0 ) {
			model.asMap().clear();
			model.addAttribute(MODEL_KEY_RESULT, matches);
		}
		return getViewName();
	}

	public DataCollectorBiz getDataCollectorBiz() {
		return dataCollectorBiz;
	}

	public void setDataCollectorBiz(DataCollectorBiz dataCollectorBiz) {
		this.dataCollectorBiz = dataCollectorBiz;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

}
