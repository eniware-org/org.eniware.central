/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.web.api;

import static net.solarnetwork.web.domain.Response.response;

import net.solarnetwork.central.query.biz.QueryBiz;
import net.solarnetwork.central.web.support.WebServiceControllerSupport;
import net.solarnetwork.web.domain.Response;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.LocationMatch;
import org.eniware.central.support.SourceLocationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for querying location data.
 * 
 * @version 2.0
 */
@Controller("v1LocationLookupController")
@RequestMapping({ "/api/v1/pub/location", "/api/v1/sec/location" })
public class LocationLookupController extends WebServiceControllerSupport {

	private final QueryBiz queryBiz;

	/**
	 * Constructor.
	 * 
	 * @param queryBiz
	 *        the QueryBiz to use
	 */
	@Autowired
	public LocationLookupController(QueryBiz queryBiz) {
		super();
		this.queryBiz = queryBiz;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
	}

	/**
	 * Search for locations.
	 * 
	 * @param cmd
	 *        the search criteria
	 * @return the search results
	 * @since 1.2
	 */
	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public Response<FilterResults<LocationMatch>> findLocations(SourceLocationFilter cmd) {
		if ( cmd == null ) {
			return new Response<FilterResults<LocationMatch>>(false, null, "Search filter is required.",
					null);
		}
		// convert empty strings to null
		cmd.removeEmptyValues();

		FilterResults<LocationMatch> results = queryBiz.findFilteredLocations(cmd.getLocation(),
				cmd.getSortDescriptors(), cmd.getOffset(), cmd.getMax());
		return response(results);
	}

}
