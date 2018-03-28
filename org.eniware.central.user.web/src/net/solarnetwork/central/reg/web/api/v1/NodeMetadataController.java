/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package net.solarnetwork.central.reg.web.api.v1;

import static net.solarnetwork.web.domain.Response.response;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eniware.central.biz.SolarNodeMetadataBiz;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.SolarNodeMetadataFilterMatch;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.domain.UserNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.solarnetwork.central.datum.domain.DatumFilterCommand;
import net.solarnetwork.central.security.SecurityUtils;
import net.solarnetwork.central.web.support.WebServiceControllerSupport;
import net.solarnetwork.domain.GeneralDatumMetadata;
import net.solarnetwork.web.domain.Response;

/**
 * Controller for node metadata.
 * 
 * @version 1.0
 * @since 1.18
 */
@Controller("v1NodeMetadataController")
@RequestMapping(value = "/v1/sec/nodes/meta")
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
			List<UserNode> nodes = userBiz.getUserNodes(SecurityUtils.getCurrentActorUserId());
			if ( nodes != null && !nodes.isEmpty() ) {
				Long[] nodeIds = new Long[nodes.size()];
				for ( ListIterator<UserNode> itr = nodes.listIterator(); itr.hasNext(); ) {
					nodeIds[itr.nextIndex()] = itr.next().getId();
				}
				criteria.setNodeIds(nodeIds);
			}
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

	/**
	 * Add metadata to a node. The metadata is merged only, and will not replace
	 * existing values.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param meta
	 *        the metadata to merge
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{nodeId}" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@PathVariable("nodeId") Long nodeId,
			@RequestBody GeneralDatumMetadata meta) {
		solarNodeMetadataBiz.addSolarNodeMetadata(nodeId, meta);
		return response(null);
	}

	/**
	 * Completely replace the metadata for a given node ID, or create it if it
	 * doesn't already exist.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param meta
	 *        the metadata to store
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{nodeId}" }, method = RequestMethod.PUT)
	public Response<Object> replaceMetadata(@PathVariable("nodeId") Long nodeId,
			@RequestBody GeneralDatumMetadata meta) {
		solarNodeMetadataBiz.storeSolarNodeMetadata(nodeId, meta);
		return response(null);
	}

	/**
	 * Completely remove the metadata for a given node ID.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{nodeId}" }, method = RequestMethod.DELETE)
	public Response<Object> deleteMetadata(@PathVariable("nodeId") Long nodeId) {
		solarNodeMetadataBiz.removeSolarNodeMetadata(nodeId);
		return response(null);
	}

}
