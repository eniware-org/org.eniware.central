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
import org.eniware.central.datum.domain.GeneralNodeDatumMetadataFilterMatch;
import org.eniware.central.in.biz.DataCollectorBiz;
import org.eniware.central.security.SecurityUtils;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.domain.GeneralDatumMetadata;
import org.eniware.web.domain.Response;

/**
 * Controller for node metadata actions.
 *
 * @version 1.0
 * @since 1.21
 */
@Controller("v1NodeMetadataController")
@RequestMapping({ "/api/v1/pub/nodes/meta", "/api/v1/sec/nodes/meta" })
public class NodeMetadataController extends WebServiceControllerSupport {

	private final DataCollectorBiz dataCollectorBiz;

	/**
	 * Constructor.
	 * 
	 * @param dataCollectorBiz
	 *        the DataCollectorBiz to use
	 */
	@Autowired
	public NodeMetadataController(DataCollectorBiz dataCollectorBiz) {
		super();
		this.dataCollectorBiz = dataCollectorBiz;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralNodeDatumMetadataFilterMatch>> findMetadata(
			DatumFilterCommand criteria) {
		Long nodeId = SecurityUtils.getCurrentNode().getNodeId();
		return findMetadata(nodeId, criteria);
	}

	@ResponseBody
	@RequestMapping(value = { "/{nodeId}" }, method = RequestMethod.GET)
	public Response<FilterResults<GeneralNodeDatumMetadataFilterMatch>> findMetadata(
			@PathVariable("nodeId") Long nodeId, DatumFilterCommand criteria) {
		DatumFilterCommand filter = new DatumFilterCommand();
		filter.setNodeId(nodeId);
		FilterResults<GeneralNodeDatumMetadataFilterMatch> results = dataCollectorBiz
				.findGeneralNodeDatumMetadata(filter, criteria.getSortDescriptors(),
						criteria.getOffset(), criteria.getMax());
		return response(results);
	}

	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@RequestBody GeneralDatumMetadata meta) {
		Long nodeId = SecurityUtils.getCurrentNode().getNodeId();
		return addMetadata(nodeId, meta);
	}

	@ResponseBody
	@RequestMapping(value = { "/{nodeId}" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@PathVariable("nodeId") Long nodeId,
			@RequestBody GeneralDatumMetadata meta) {
		dataCollectorBiz.addEniwareEdgeMetadata(nodeId, meta);
		return response(null);
	}

}
