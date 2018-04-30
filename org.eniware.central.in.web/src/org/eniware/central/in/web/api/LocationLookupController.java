/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web.api;

import static org.eniware.web.domain.Response.response;

import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.in.biz.DataCollectorBiz;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;

import org.eniware.central.ValidationException;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.EniwareLocation;
import org.eniware.central.domain.SourceLocationMatch;
import org.eniware.central.in.web.GenericSourceLocationFilter.LocationType;
import org.eniware.central.support.PriceLocationFilter;
import org.eniware.central.support.SourceLocationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for querying location data.
 *
 * @version 1.2
 */
@Controller("v1LocationLookupController")
@RequestMapping({ "/api/v1/pub/location", "/api/v1/sec/location" })
public class LocationLookupController extends WebServiceControllerSupport {

	private final DataCollectorBiz dataCollectorBiz;

	/**
	 * Constructor.
	 * 
	 * @param dataCollectorBiz
	 *        the DataCollectorBiz to use
	 */
	@Autowired
	public LocationLookupController(DataCollectorBiz dataCollectorBiz) {
		super();
		this.dataCollectorBiz = dataCollectorBiz;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
	}

	/**
	 * Query for general location datum metadata.
	 * 
	 * @param query
	 *        a general search query
	 * @param criteria
	 *        specific criteria, such as source ID, sort order, max results,
	 *        etc.
	 * @return the results
	 * @since 1.2
	 */
	@ResponseBody
	@RequestMapping(value = { "", "/", "/query" }, method = RequestMethod.GET, params = "!type")
	public Response<FilterResults<GeneralLocationDatumMetadataFilterMatch>> findGeneralLocationMetadata(
			@RequestParam(value = "query", required = false) String query, DatumFilterCommand command) {
		EniwareLocation loc;
		if ( command != null ) {
			loc = new EniwareLocation(command.getLocation());
		} else {
			loc = new EniwareLocation();
		}
		if ( query != null ) {
			loc.setRegion(query);
		}
		DatumFilterCommand criteria = new DatumFilterCommand(loc);
		if ( command != null ) {
			if ( command.getLocationIds() != null ) {
				criteria.setLocationIds(command.getLocationIds());
			}
			if ( command.getSourceIds() != null ) {
				criteria.setSourceIds(command.getSourceIds());
			}
			if ( command.getTags() != null ) {
				criteria.setTags(command.getTags());
			}
		}
		FilterResults<GeneralLocationDatumMetadataFilterMatch> results = dataCollectorBiz
				.findGeneralLocationDatumMetadata(criteria, command.getSortDescriptors(),
						command.getOffset(), command.getMax());
		return response(results);
	}

	/**
	 * Query for general location datum metadata.
	 * 
	 * @param query
	 *        a general search query
	 * @param criteria
	 *        specific criteria, such as source ID, sort order, max results,
	 *        etc.
	 * @return the results
	 * @since 1.2
	 */
	@ResponseBody
	@RequestMapping(value = { "/{locationId}" }, method = RequestMethod.GET)
	public Response<GeneralLocationDatumMetadataFilterMatch> getGeneralLocationMetadata(
			@PathVariable("locationId") Long locationId,
			@RequestParam(value = "sourceId") String sourceId) {
		DatumFilterCommand criteria = new DatumFilterCommand();
		criteria.setLocationId(locationId);
		criteria.setSourceId(sourceId);
		FilterResults<GeneralLocationDatumMetadataFilterMatch> results = dataCollectorBiz
				.findGeneralLocationDatumMetadata(criteria, null, 0, 1);
		if ( results.getReturnedResultCount() < 1 ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, sourceId);
		}
		return response(results.getResults().iterator().next());
	}

	@Deprecated
	@ResponseBody
	@RequestMapping(value = { "", "/", "/query" }, method = RequestMethod.GET, params = "type")
	public Response<FilterResults<SourceLocationMatch>> findLocations(SourceLocationFilter criteria,
			@RequestParam("type") String locationType) {
		LocationType type = null;
		try {
			type = LocationType.valueOf(locationType);
		} catch ( IllegalArgumentException e ) {
			// ignore
		}

		if ( type == LocationType.Price ) {
			return findPriceLocations(new PriceLocationFilter(criteria));
		} else if ( type == LocationType.Weather ) {
			return findWeatherLocations(criteria);
		} else {
			BindException errors = new BindException(criteria, "criteria");
			errors.reject("error.field.invalid", new Object[] { "locationType" }, "Invalid value.");
			throw new ValidationException(errors);
		}
	}

	@Deprecated
	@ResponseBody
	@RequestMapping(value = "/price", method = RequestMethod.GET)
	public Response<FilterResults<SourceLocationMatch>> findPriceLocations(PriceLocationFilter criteria) {
		// convert empty strings to null
		criteria.removeEmptyValues();

		FilterResults<SourceLocationMatch> matches = dataCollectorBiz.findPriceLocations(criteria,
				criteria.getSortDescriptors(), criteria.getOffset(), criteria.getMax());
		return response(matches);
	}

	@Deprecated
	@ResponseBody
	@RequestMapping(value = "/weather", method = RequestMethod.GET)
	public Response<FilterResults<SourceLocationMatch>> findWeatherLocations(
			SourceLocationFilter criteria) {
		// convert empty strings to null
		criteria.removeEmptyValues();

		FilterResults<SourceLocationMatch> matches = dataCollectorBiz.findWeatherLocations(criteria,
				criteria.getSortDescriptors(), criteria.getOffset(), criteria.getMax());
		return response(matches);
	}

}
