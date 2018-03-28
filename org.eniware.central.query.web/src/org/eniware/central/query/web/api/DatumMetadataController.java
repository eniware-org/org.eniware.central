/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.web.api;

import static net.solarnetwork.web.domain.Response.response;
import net.solarnetwork.central.datum.biz.DatumMetadataBiz;
import net.solarnetwork.central.web.support.WebServiceControllerSupport;
import net.solarnetwork.domain.GeneralDatumMetadata;
import net.solarnetwork.web.domain.Response;

import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilterMatch;
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
@RequestMapping({ "/api/v1/pub/datum/meta/{nodeId}", "/api/v1/sec/datum/meta/{nodeId}" })
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
	 * Find all metadata for a node ID.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param criteria
	 *        any sort or limit criteria
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralNodeDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("nodeId") Long nodeId, DatumFilterCommand criteria) {
		return findMetadata(nodeId, null, criteria);
	}

	/**
	 * Get metadata for a single node ID and source ID combination.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param sourceId
	 *        the source ID
	 * @param criteria
	 *        any sort or limit criteria
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralNodeDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("nodeId") Long nodeId, @PathVariable("sourceId") String sourceId,
			DatumFilterCommand criteria) {
		DatumFilterCommand filter = new DatumFilterCommand();
		filter.setNodeId(nodeId);
		filter.setSourceId(sourceId);
		FilterResults<GeneralNodeDatumMetadataFilterMatch> results = datumMetadataBiz
				.findGeneralNodeDatumMetadata(filter, criteria.getSortDescriptors(),
						criteria.getOffset(), criteria.getMax());
		return response(results);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET, params = { "sourceId" })
	public Response<FilterResults<GeneralNodeDatumMetadataFilterMatch>> findMetadataAlt(
			@PathVariable("nodeId") Long nodeId, @RequestParam("sourceId") String sourceId,
			DatumFilterCommand criteria) {
		return findMetadata(nodeId, sourceId, criteria);
	}

	/**
	 * Add metadata to a source. The metadata is merged only, and will not
	 * replace existing values.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param sourceId
	 *        the source ID
	 * @param meta
	 *        the metadata to merge
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@PathVariable("nodeId") Long nodeId,
			@PathVariable("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		datumMetadataBiz.addGeneralNodeDatumMetadata(nodeId, sourceId, meta);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.POST, params = { "sourceId" })
	public Response<Object> addMetadataAlt(@PathVariable("nodeId") Long nodeId,
			@RequestParam("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		return addMetadata(nodeId, sourceId, meta);
	}

	/**
	 * Completely replace the metadata for a given source ID, or create it if it
	 * doesn't already exist.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param sourceId
	 *        the source ID
	 * @param meta
	 *        the metadata to store
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.PUT)
	public Response<Object> replaceMetadata(@PathVariable("nodeId") Long nodeId,
			@PathVariable("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		datumMetadataBiz.storeGeneralNodeDatumMetadata(nodeId, sourceId, meta);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.PUT, params = { "sourceId" })
	public Response<Object> replaceMetadataAlt(@PathVariable("nodeId") Long nodeId,
			@RequestParam("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		return replaceMetadata(nodeId, sourceId, meta);
	}

	/**
	 * Completely remove the metadata for a given source ID.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param sourceId
	 *        the source ID
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.DELETE)
	public Response<Object> deleteMetadata(@PathVariable("nodeId") Long nodeId,
			@PathVariable("sourceId") String sourceId) {
		datumMetadataBiz.removeGeneralNodeDatumMetadata(nodeId, sourceId);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.DELETE, params = { "sourceId" })
	public Response<Object> deleteMetadataAlt(@PathVariable("nodeId") Long nodeId,
			@RequestParam("sourceId") String sourceId) {
		return deleteMetadata(nodeId, sourceId);
	}

}
