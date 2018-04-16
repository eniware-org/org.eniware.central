/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.web.api;

import static org.eniware.web.domain.Response.response;
import org.eniware.central.datum.biz.DatumMetadataBiz;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.domain.GeneralDatumMetadata;
import org.eniware.web.domain.Response;

import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.datum.domain.GeneralLocationDatumMetadataFilterMatch;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SolarLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for location metadata actions.
 * 
 * @version 1.1
 */
@Controller("v1LocationMetadataController")
@RequestMapping({ "/api/v1/pub/location/meta", "/api/v1/sec/location/meta" })
public class LocationMetadataController extends WebServiceControllerSupport {

	private final DatumMetadataBiz datumMetadataBiz;

	/**
	 * Constructor.
	 * 
	 * @param datumMetadataBiz
	 *        the DatumMetadataBiz to use
	 */
	@Autowired
	public LocationMetadataController(DatumMetadataBiz datumMetadataBiz) {
		super();
		this.datumMetadataBiz = datumMetadataBiz;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
	}

	/**
	 * Query for metadata.
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
	@RequestMapping(value = { "", "/", "/query" }, method = RequestMethod.GET)
	public Response<?> findGeneralLocations(
			@RequestParam(value = "query", required = false) String query, DatumFilterCommand command) {
		SolarLocation loc;
		if ( command != null ) {
			loc = new SolarLocation(command.getLocation());
		} else {
			loc = new SolarLocation();
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
		FilterResults<GeneralLocationDatumMetadataFilterMatch> results = datumMetadataBiz
				.findGeneralLocationDatumMetadata(criteria, command.getSortDescriptors(),
						command.getOffset(), command.getMax());
		return response(results);
	}

	/**
	 * Find all metadata for a location ID.
	 * 
	 * @param locationId
	 *        the location ID
	 * @param criteria
	 *        any sort or limit criteria
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = "/{locationId}", method = RequestMethod.GET)
	public Response<FilterResults<GeneralLocationDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("locationId") Long locationId, DatumFilterCommand criteria) {
		return findMetadata(locationId, null, criteria);
	}

	/**
	 * Get metadata for a single location ID and source ID combination.
	 * 
	 * @param locationId
	 *        the location ID
	 * @param sourceId
	 *        the source ID
	 * @param criteria
	 *        any sort or limit criteria
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{locationId}/{sourceId}" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralLocationDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("locationId") Long locationId, @PathVariable("sourceId") String sourceId,
			DatumFilterCommand criteria) {
		DatumFilterCommand filter = new DatumFilterCommand();
		filter.setLocationId(locationId);
		filter.setSourceId(sourceId);
		FilterResults<GeneralLocationDatumMetadataFilterMatch> results = datumMetadataBiz
				.findGeneralLocationDatumMetadata(filter, criteria.getSortDescriptors(),
						criteria.getOffset(), criteria.getMax());
		return response(results);
	}

	@ResponseBody
	@RequestMapping(value = "/{locationId}", method = RequestMethod.GET, params = { "sourceId" })
	public Response<FilterResults<GeneralLocationDatumMetadataFilterMatch>> findMetadataAlt(
			@PathVariable("locationId") Long locationId, @RequestParam("sourceId") String sourceId,
			DatumFilterCommand criteria) {
		return findMetadata(locationId, sourceId, criteria);
	}

	/**
	 * Add metadata to a source. The metadata is merged only, and will not
	 * replace existing values.
	 * 
	 * @param locationId
	 *        the location ID
	 * @param sourceId
	 *        the source ID
	 * @param meta
	 *        the metadata to merge
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{locationId}/{sourceId}" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@PathVariable("locationId") Long locationId,
			@PathVariable("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		datumMetadataBiz.addGeneralLocationDatumMetadata(locationId, sourceId, meta);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = "/{locationId}", method = RequestMethod.POST, params = { "sourceId" })
	public Response<Object> addMetadataAlt(@PathVariable("locationId") Long locationId,
			@RequestParam("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		return addMetadata(locationId, sourceId, meta);
	}

	/**
	 * Completely replace the metadata for a given source ID, or create it if it
	 * doesn't already exist.
	 * 
	 * @param locationId
	 *        the location ID
	 * @param sourceId
	 *        the source ID
	 * @param meta
	 *        the metadata to store
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{locationId}/{sourceId}" }, method = RequestMethod.PUT)
	public Response<Object> replaceMetadata(@PathVariable("locationId") Long locationId,
			@PathVariable("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		datumMetadataBiz.storeGeneralLocationDatumMetadata(locationId, sourceId, meta);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = "/{locationId}", method = RequestMethod.PUT, params = { "sourceId" })
	public Response<Object> replaceMetadataAlt(@PathVariable("locationId") Long locationId,
			@RequestParam("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		return replaceMetadata(locationId, sourceId, meta);
	}

	/**
	 * Completely remove the metadata for a given source ID.
	 * 
	 * @param locationId
	 *        the location ID
	 * @param sourceId
	 *        the source ID
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = "/{locationId}/{sourceId}", method = RequestMethod.DELETE)
	public Response<Object> deleteMetadata(@PathVariable("locationId") Long locationId,
			@PathVariable("sourceId") String sourceId) {
		datumMetadataBiz.removeGeneralLocationDatumMetadata(locationId, sourceId);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = "/{locationId}", method = RequestMethod.DELETE, params = { "sourceId" })
	public Response<Object> deleteMetadataAlt(@PathVariable("locationId") Long locationId,
			@RequestParam("sourceId") String sourceId) {
		return deleteMetadata(locationId, sourceId);
	}

}
