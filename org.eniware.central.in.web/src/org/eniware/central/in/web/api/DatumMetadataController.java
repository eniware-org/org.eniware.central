/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web.api;

import static org.eniware.web.domain.Response.response;
import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilterMatch;
import org.eniware.central.in.biz.DataCollectorBiz;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.domain.GeneralDatumMetadata;
import org.eniware.web.domain.Response;

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

	private final DataCollectorBiz dataCollectorBiz;

	/**
	 * Constructor.
	 * 
	 * @param dataCollectorBiz
	 *        the DataCollectorBiz to use
	 */
	@Autowired
	public DatumMetadataController(DataCollectorBiz dataCollectorBiz) {
		super();
		this.dataCollectorBiz = dataCollectorBiz;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralEdgeDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("EdgeId") Long EdgeId, DatumFilterCommand criteria) {
		return findMetadata(EdgeId, null, criteria);
	}

	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralEdgeDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("EdgeId") Long EdgeId, @PathVariable("sourceId") String sourceId,
			DatumFilterCommand criteria) {
		DatumFilterCommand filter = new DatumFilterCommand();
		filter.setEdgeId(EdgeId);
		filter.setSourceId(sourceId);
		FilterResults<GeneralEdgeDatumMetadataFilterMatch> results = dataCollectorBiz
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

	@ResponseBody
	@RequestMapping(value = { "/{sourceId}" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@PathVariable("EdgeId") Long EdgeId,
			@PathVariable("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		dataCollectorBiz.addGeneralEdgeDatumMetadata(EdgeId, sourceId, meta);
		return response(null);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.POST, params = { "sourceId" })
	public Response<Object> addMetadataAlt(@PathVariable("EdgeId") Long EdgeId,
			@RequestParam("sourceId") String sourceId, @RequestBody GeneralDatumMetadata meta) {
		return addMetadata(EdgeId, sourceId, meta);
	}

}
