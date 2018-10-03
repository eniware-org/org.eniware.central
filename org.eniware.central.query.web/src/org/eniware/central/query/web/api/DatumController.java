/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.web.api;

import static org.eniware.central.query.web.api.ReportableIntervalController.filterSources;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;

import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.domain.FilterResults;
import org.eniware.util.JodaDateFormatEditor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.eniware.central.query.biz.QueryBiz;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;

/**
 * Controller for querying datum related data.
 * 
 * @version 2.1
 */
@Controller("v1DatumController")
@RequestMapping({ "/api/v1/sec/datum", "/api/v1/pub/datum" })
public class DatumController extends WebServiceControllerSupport {

	private final QueryBiz queryBiz;
	private final PathMatcher pathMatcher;

	private String[] requestDateFormats = new String[] { DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_FORMAT };

	/**
	 * Constructor.
	 * 
	 * @param queryBiz
	 *        the QueryBiz to use
	 */
	public DatumController(QueryBiz queryBiz) {
		this(queryBiz, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param queryBiz
	 *        the QueryBiz to use
	 * @param pathMatcher
	 *        the source ID path matcher to use
	 * @since 2.1
	 */
	@Autowired
	public DatumController(QueryBiz queryBiz,
			@Qualifier("sourceIdPathMatcher") PathMatcher pathMatcher) {
		super();
		this.queryBiz = queryBiz;
		this.pathMatcher = pathMatcher;
	}

	/**
	 * Web binder initialization.
	 * 
	 * @param binder
	 *        the binder to initialize
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(DateTime.class,
				new JodaDateFormatEditor(this.requestDateFormats, TimeZone.getTimeZone("UTC")));
	}

	private void resolveSourceIdPattern(DatumFilterCommand cmd) {
		if ( cmd == null || pathMatcher == null || queryBiz == null ) {
			return;
		}
		String sourceId = cmd.getSourceId();
		if ( sourceId != null && pathMatcher.isPattern(sourceId) && cmd.getEdgeIds() != null ) {
			Set<String> allSources = new LinkedHashSet<String>();
			for ( Long EdgeId : cmd.getEdgeIds() ) {
				Set<String> data = queryBiz.getAvailableSources(EdgeId, cmd.getStartDate(),
						cmd.getEndDate());
				if ( data != null ) {
					allSources.addAll(data);
				}
			}
			allSources = filterSources(allSources, pathMatcher, sourceId);
			if ( !allSources.isEmpty() ) {
				cmd.setSourceIds(allSources.toArray(new String[allSources.size()]));
			}
		}
	}

	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.GET, params = "!type")
	public Response<FilterResults<?>> filterGeneralDatumData(final DatumFilterCommand cmd) {

		// support filtering based on sourceId path pattern, by simply finding the sources that match first
		resolveSourceIdPattern(cmd);

		FilterResults<?> results;
		if ( cmd.getAggregation() != null ) {
			results = queryBiz.findFilteredAggregateGeneralEdgeDatum(cmd, cmd.getSortDescriptors(),
					cmd.getOffset(), cmd.getMax());
		} else {
			results = queryBiz.findFilteredGeneralEdgeDatum(cmd, cmd.getSortDescriptors(),
					cmd.getOffset(), cmd.getMax());
		}
		return new Response<FilterResults<?>>(results);
	}

	@ResponseBody
	@RequestMapping(value = "/mostRecent", method = RequestMethod.GET, params = "!type")
	public Response<FilterResults<?>> getMostRecentGeneralEdgeDatumData(final DatumFilterCommand cmd) {
		cmd.setMostRecent(true);
		return filterGeneralDatumData(cmd);
	}

	public void setRequestDateFormats(String[] requestDateFormats) {
		this.requestDateFormats = requestDateFormats;
	}

}
