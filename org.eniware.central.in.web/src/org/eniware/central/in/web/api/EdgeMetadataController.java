/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web.api;

import static org.eniware.web.domain.Response.response;

import org.eniware.central.domain.FilterResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.datum.domain.GeneralEdgeDatumMetadataFilterMatch;
import org.eniware.central.in.biz.DataCollectorBiz;
import org.eniware.central.security.SecurityUtils;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.domain.GeneralDatumMetadata;
import org.eniware.web.domain.Response;

/**
 * Controller for Edge metadata actions.
 *
 * @version 1.0
 * @since 1.21
 */
@Controller("v1EdgeMetadataController")
@RequestMapping({ "/api/v1/pub/Edges/meta", "/api/v1/sec/Edges/meta" })
public class EdgeMetadataController extends WebServiceControllerSupport {

	private final DataCollectorBiz dataCollectorBiz;

	/**
	 * Constructor.
	 * 
	 * @param dataCollectorBiz
	 *        the DataCollectorBiz to use
	 */
	@Autowired
	public EdgeMetadataController(DataCollectorBiz dataCollectorBiz) {
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
			DatumFilterCommand criteria) {
		Long EdgeId = SecurityUtils.getCurrentEdge().getEdgeId();
		return findMetadata(EdgeId, criteria);
	}

	@ResponseBody
	@RequestMapping(value = { "/{EdgeId}" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralEdgeDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("EdgeId") Long EdgeId, DatumFilterCommand criteria) {
		DatumFilterCommand filter = new DatumFilterCommand();
		filter.setEdgeId(EdgeId);
		FilterResults<GeneralEdgeDatumMetadataFilterMatch> results = dataCollectorBiz
				.findGeneralEdgeDatumMetadata(filter, criteria.getSortDescriptors(),
						criteria.getOffset(), criteria.getMax());
		return response(results);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@RequestBody GeneralDatumMetadata meta) {
		Long EdgeId = SecurityUtils.getCurrentEdge().getEdgeId();
		return addMetadata(EdgeId, meta);
	}

	@ResponseBody
	@RequestMapping(value = { "/{EdgeId}" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@PathVariable("EdgeId") Long EdgeId,
			@RequestBody GeneralDatumMetadata meta) {
		dataCollectorBiz.addEniwareEdgeMetadata(EdgeId, meta);
		return response(null);
	}

}
