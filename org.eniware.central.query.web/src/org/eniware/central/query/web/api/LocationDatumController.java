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
import org.eniware.central.query.domain.ReportableInterval;
import org.eniware.central.query.web.domain.GeneralReportableIntervalCommand;
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
 * Controller for location-based data.
 * 
 * @version 1.1
 */
@Controller("v1LocationDatumController")
@RequestMapping({ "/api/v1/sec/location/datum", "/api/v1/pub/location/datum" })
public class LocationDatumController extends WebServiceControllerSupport {

	private final QueryBiz queryBiz;
	private final PathMatcher pathMatcher;
	private String[] requestDateFormats = new String[] { DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_FORMAT };

	/**
	 * Constructor.
	 * 
	 * @param queryBiz
	 *        the QueryBiz to use
	 */
	public LocationDatumController(QueryBiz queryBiz) {
		this(queryBiz, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param queryBiz
	 *        the QueryBiz to use
	 * @param pathMatcher
	 *        the source ID path matcher to use
	 * @since 1.1
	 */
	@Autowired
	public LocationDatumController(QueryBiz queryBiz,
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

	/**
	 * Get the set of source IDs available for the available
	 * GeneralLocationDatum for a single location, optionally constrained within
	 * a date range.
	 * 
	 * <p>
	 * A <code>sourceId</code> path pattern may also be provided, to restrict
	 * the resulting source ID set to.
	 * </p>
	 * 
	 * <p>
	 * Example URL: <code>/api/v1/sec/location/datum/sources?locationId=1</code>
	 * </p>
	 * 
	 * <p>
	 * Example JSON response:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *   "success": true,
	 *   "data": [
	 *     "Main"
	 *   ]
	 * }
	 * </pre>
	 * 
	 * @param cmd
	 *        the input command
	 * @return the available sources
	 */
	@ResponseBody
	@RequestMapping(value = "/sources", method = RequestMethod.GET)
	public Response<Set<String>> getAvailableSources(GeneralReportableIntervalCommand cmd) {
		Set<String> data = queryBiz.getLocationAvailableSources(cmd.getLocationId(), cmd.getStartDate(),
				cmd.getEndDate());

		// support filtering based on sourceId path pattern
		data = filterSources(data, this.pathMatcher, cmd.getSourceId());

		return new Response<Set<String>>(data);
	}

	/**
	 * Get a date range of available GeneralLocationDatum for a single location
	 * and an optional source ID.
	 * 
	 * <p>
	 * This method returns a start/end date range.
	 * </p>
	 * 
	 * <p>
	 * Example URL:
	 * <code>/api/v1/sec/location/datum/interval?locationId=1</code>
	 * </p>
	 * 
	 * <p>
	 * Example JSON response:
	 * </p>
	 * 
	 * <pre>
	 * {
	 *   "success": true,
	 *   "data": {
	 *     "timeZone": "Pacific/Auckland",
	 *     "endDate": "2012-12-11 01:49",
	 *     "startDate": "2012-12-11 00:30",
	 *     "dayCount": 1683,
	 *     "monthCount": 56,
	 *     "yearCount": 6
	 *   }
	 * }
	 * </pre>
	 * 
	 * @param cmd
	 *        the input command
	 * @return the {@link ReportableInterval}
	 */
	@ResponseBody
	@RequestMapping(value = "/interval", method = RequestMethod.GET)
	public Response<ReportableInterval> getReportableInterval(GeneralReportableIntervalCommand cmd) {
		ReportableInterval data = queryBiz.getLocationReportableInterval(cmd.getLocationId(),
				cmd.getSourceId());
		return new Response<ReportableInterval>(data);
	}

	private void resolveSourceIdPattern(DatumFilterCommand cmd) {
		if ( cmd == null || pathMatcher == null || queryBiz == null ) {
			return;
		}
		String sourceId = cmd.getSourceId();
		if ( sourceId != null && pathMatcher.isPattern(sourceId) && cmd.getLocationIds() != null ) {
			Set<String> allSources = new LinkedHashSet<String>();
			for ( Long locationId : cmd.getLocationIds() ) {
				Set<String> data = queryBiz.getLocationAvailableSources(locationId, cmd.getStartDate(),
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
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Response<FilterResults<?>> filterGeneralDatumData(final DatumFilterCommand cmd) {

		// support filtering based on sourceId path pattern, by simply finding the sources that match first
		resolveSourceIdPattern(cmd);

		FilterResults<?> results;
		if ( cmd.getAggregation() != null ) {
			results = queryBiz.findAggregateGeneralLocationDatum(cmd, cmd.getSortDescriptors(),
					cmd.getOffset(), cmd.getMax());
		} else {
			results = queryBiz.findGeneralLocationDatum(cmd, cmd.getSortDescriptors(), cmd.getOffset(),
					cmd.getMax());
		}
		return new Response<FilterResults<?>>(results);
	}

	@ResponseBody
	@RequestMapping(value = "/mostRecent", method = RequestMethod.GET)
	public Response<FilterResults<?>> getMostRecentGeneralEdgeDatumData(final DatumFilterCommand cmd) {
		cmd.setMostRecent(true);
		return filterGeneralDatumData(cmd);
	}

	public void setRequestDateFormats(String[] requestDateFormats) {
		this.requestDateFormats = requestDateFormats;
	}

}
