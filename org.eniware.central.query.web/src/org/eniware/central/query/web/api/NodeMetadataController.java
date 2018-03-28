/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.web.api;

import static net.solarnetwork.web.domain.Response.response;
import java.util.NoSuchElementException;

import org.eniware.central.biz.SolarNodeMetadataBiz;
import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SolarNodeMetadataFilterMatch;
import org.eniware.central.user.biz.UserBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.solarnetwork.central.web.support.WebServiceControllerSupport;
import net.solarnetwork.web.domain.Response;

/**
 * Controller for read-only node metadata access.
 * 
 * @version 1.0
 */
@Controller("v1NodeMetadataController")
@RequestMapping({ "/api/v1/pub/nodes/meta", "/api/v1/sec/nodes/meta" })
public class NodeMetadataController extends WebServiceControllerSupport {

	private final UserBiz userBiz;
	private final SolarNodeMetadataBiz solarNodeMetadataBiz;

	/**
	 * Constructor.
	 * 
	 * @param userBiz
	 *        the UserBiz to use
	 * @param solarNodeMetadataBiz
	 *        the SolarNodeMetadataBiz to use
	 */
	@Autowired
	public NodeMetadataController(UserBiz userBiz, SolarNodeMetadataBiz solarNodeMetadataBiz) {
		super();
		this.userBiz = userBiz;
		this.solarNodeMetadataBiz = solarNodeMetadataBiz;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
	}

	/**
	 * Find all metadata for any number of node IDs.
	 * 
	 * @param criteria
	 *        any sort or limit criteria
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public Response<FilterResults<SolarNodeMetadataFilterMatch>> findMetadata(
			DatumFilterCommand criteria) {
		if ( criteria.getNodeId() == null ) {
			// default to all nodes for actor
			criteria.setNodeIds(authorizedNodeIdsForCurrentActor(userBiz));
		}
		FilterResults<SolarNodeMetadataFilterMatch> results = solarNodeMetadataBiz.findSolarNodeMetadata(
				criteria, criteria.getSortDescriptors(), criteria.getOffset(), criteria.getMax());
		return response(results);
	}

	/**
	 * Find all metadata for a specific node ID.
	 * 
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{nodeId}" }, method = RequestMethod.GET)
	public Response<SolarNodeMetadataFilterMatch> getMetadata(@PathVariable("nodeId") Long nodeId) {
		DatumFilterCommand criteria = new DatumFilterCommand();
		criteria.setNodeId(nodeId);
		FilterResults<SolarNodeMetadataFilterMatch> results = solarNodeMetadataBiz
				.findSolarNodeMetadata(criteria, null, null, null);
		SolarNodeMetadataFilterMatch result = null;
		if ( results != null ) {
			try {
				result = results.iterator().next();
			} catch ( NoSuchElementException e ) {
				// ignore
			}
		}
		return response(result);
	}

}
