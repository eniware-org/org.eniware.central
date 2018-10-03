/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web;

import java.util.Set;
import java.util.TimeZone;

import org.eniware.util.JodaDateFormatEditor;
import org.eniware.util.JodaDateFormatEditor.ParseMode;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.eniware.central.query.biz.QueryBiz;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;

/**
 * REST controller to support data queries.
 * 
 * @version 1.0
 */
@RestController("v1EdgeDataController")
@RequestMapping(value = "/sec/Edge-data")
public class EdgeDataController extends WebServiceControllerSupport {

	private final QueryBiz queryBiz;

	@Autowired
	public EdgeDataController(QueryBiz queryBiz) {
		super();
		this.queryBiz = queryBiz;
	}

	/**
	 * Web binder initialization.
	 * 
	 * <p>
	 * Registers a {@link LocalDate} property editor using the
	 * {@link #DEFAULT_DATE_FORMAT} pattern.
	 * </p>
	 * 
	 * @param binder
	 *        the binder to initialize
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(LocalDate.class,
				new JodaDateFormatEditor(DEFAULT_DATE_FORMAT, ParseMode.LocalDate));
		binder.registerCustomEditor(DateTime.class,
				new JodaDateFormatEditor(new String[] { DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_FORMAT },
						TimeZone.getTimeZone("UTC")));
	}

	/**
	 * Get the set of source IDs available for the available GeneralEdgeData for
	 * a single Edge, optionally constrained within a date range.
	 * 
	 * @param cmd
	 *        the input command
	 * @return the available sources
	 */
	@ResponseBody
	@RequestMapping(value = "/{EdgeId}/sources", method = RequestMethod.GET)
	public Response<Set<String>> getAvailableSources(@PathVariable("EdgeId") Long EdgeId) {
		Set<String> data = queryBiz.getAvailableSources(EdgeId, null, null);
		return new Response<Set<String>>(data);
	}

}
