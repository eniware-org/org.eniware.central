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
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilterMatch;
import org.eniware.central.domain.FilterResults;
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
 * Controller for datum metadata actions.
 * 
 * @version 1.0
 */
@Controller("v1DatumMetadataController")
@RequestMapping({ "/api/v1/pub/datum/meta/{EdgeId}", "/api/v1/sec/datum/meta/{EdgeId}" })
public class DatumMetadataController extends WebServiceControllerSupport {

	private final DatumMetadataBiz datumMetadataBiz;

	/**
	 * Constructor.
	 * 
	 * @param datumMetadataBiz
	 *        the DatumMetadataBiz to use
	 */
	@Autowired
	public DatumMetadataController(DatumMetadataBiz datumMetadataBiz) {
		super();
		this.datumMetadataBiz = datumMetadataBiz;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
	}

	/**
	 * Find all metadata for a Edge ID.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param criteria
	 *        any sort or limit criteria
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralEdgeDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("EdgeId") Long EdgeId, DatumFilterCommand criteria) {
		return findMetadata(EdgeId, null, criteria);
	}

	/**
	 * Get metadata for a single Edge ID and source ID combination.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param sourceId
	 *        the source ID
	 * @param criteria
	 *        any sort or limit criteria
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralEdgeDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("EdgeId") Long EdgeId, @PathVariable("sourceId") String sourceId,
			DatumFilterCommand criteria) {
		DatumFilterCommand filter = new DatumFilterCommand();
		filter.setEdgeId(EdgeId);
		filter.setSourceId(sourceId);
		FilterResults<GeneralEdgeDatumMetadataFilterMatch> results = datumMetadataBiz
				.findGeneralEdgeDatumMetadata(filter, criteria.getSortDescriptors(),
						criteria.getOffset(), criteria.getMax());
		return response(results);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET, params = { "sourceId" })
	public Response<FilterResults<GeneralEdgeDatumMetadataFilterMatch>> findMetadataAlt(
			@PathVariable("EdgeId") Long EdgeId, @RequestParam("sourceId") String sourceId,
			DatumFilterCommand criteria) {
		return findMetadata(EdgeId, sourceId, criteria);
	}

	/**
	 * Add metadata to a source. The metadata is merged only, and will not
	 * replace existing values.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param sourceId
	 *        the source ID
	 * @param meta
	 *        the metadata to merge
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@PathVariable("EdgeId") Long EdgeId,
			@PathVariable("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		datumMetadataBiz.addGeneralEdgeDatumMetadata(EdgeId, sourceId, meta);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.POST, params = { "sourceId" })
	public Response<Object> addMetadataAlt(@PathVariable("EdgeId") Long EdgeId,
			@RequestParam("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		return addMetadata(EdgeId, sourceId, meta);
	}

	/**
	 * Completely replace the metadata for a given source ID, or create it if it
	 * doesn't already exist.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param sourceId
	 *        the source ID
	 * @param meta
	 *        the metadata to store
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.PUT)
	public Response<Object> replaceMetadata(@PathVariable("EdgeId") Long EdgeId,
			@PathVariable("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		datumMetadataBiz.storeGeneralEdgeDatumMetadata(EdgeId, sourceId, meta);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.PUT, params = { "sourceId" })
	public Response<Object> replaceMetadataAlt(@PathVariable("EdgeId") Long EdgeId,
			@RequestParam("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		return replaceMetadata(EdgeId, sourceId, meta);
	}

	/**
	 * Completely remove the metadata for a given source ID.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param sourceId
	 *        the source ID
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.DELETE)
	public Response<Object> deleteMetadata(@PathVariable("EdgeId") Long EdgeId,
			@PathVariable("sourceId") String sourceId) {
		datumMetadataBiz.removeGeneralEdgeDatumMetadata(EdgeId, sourceId);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.DELETE, params = { "sourceId" })
	public Response<Object> deleteMetadataAlt(@PathVariable("EdgeId") Long EdgeId,
			@RequestParam("sourceId") String sourceId) {
		return deleteMetadata(EdgeId, sourceId);
	}

}
