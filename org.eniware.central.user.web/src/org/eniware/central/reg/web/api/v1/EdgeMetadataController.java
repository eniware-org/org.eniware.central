/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web.api.v1;

import static org.eniware.web.domain.Response.response;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eniware.central.biz.EniwareEdgeMetadataBiz;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.EniwareEdgeMetadataFilterMatch;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.domain.UserEdge;
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
import org.eniware.central.security.SecurityUtils;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.domain.GeneralDatumMetadata;
import org.eniware.web.domain.Response;

/**
 * Controller for Edge metadata.
 * 
 * @version 1.0
 * @since 1.18
 */
@Controller("v1EdgeMetadataController")
@RequestMapping(value = "/v1/sec/Edges/meta")
public class EdgeMetadataController extends WebServiceControllerSupport {

	private final UserBiz userBiz;
	private final EniwareEdgeMetadataBiz eniwareEdgeMetadataBiz;

	/**
	 * Constructor.
	 * 
	 * @param userBiz
	 *        the UserBiz to use
	 * @param eniwareEdgeMetadataBiz
	 *        the EniwareEdgeMetadataBiz to use
	 */
	@Autowired
	public EdgeMetadataController(UserBiz userBiz, EniwareEdgeMetadataBiz eniwareEdgeMetadataBiz) {
		super();
		this.userBiz = userBiz;
		this.eniwareEdgeMetadataBiz = eniwareEdgeMetadataBiz;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
	}

	/**
	 * Find all metadata for any number of Edge IDs.
	 * 
	 * @param criteria
	 *        any sort or limit criteria
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public Response<FilterResults<EniwareEdgeMetadataFilterMatch>> findMetadata(
			DatumFilterCommand criteria) {
		if ( criteria.getEdgeId() == null ) {
			// default to all Edges for actor
			List<UserEdge> Edges = userBiz.getUserEdges(SecurityUtils.getCurrentActorUserId());
			if ( Edges != null && !Edges.isEmpty() ) {
				Long[] EdgeIds = new Long[Edges.size()];
				for ( ListIterator<UserEdge> itr = Edges.listIterator(); itr.hasNext(); ) {
					EdgeIds[itr.nextIndex()] = itr.next().getId();
				}
				criteria.setEdgeIds(EdgeIds);
			}
		}
		FilterResults<EniwareEdgeMetadataFilterMatch> results = eniwareEdgeMetadataBiz.findEniwareEdgeMetadata(
				criteria, criteria.getSortDescriptors(), criteria.getOffset(), criteria.getMax());
		return response(results);
	}

	/**
	 * Find all metadata for a specific Edge ID.
	 * 
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{EdgeId}" }, method = RequestMethod.GET)
	public Response<EniwareEdgeMetadataFilterMatch> getMetadata(@PathVariable("EdgeId") Long EdgeId) {
		DatumFilterCommand criteria = new DatumFilterCommand();
		criteria.setEdgeId(EdgeId);
		FilterResults<EniwareEdgeMetadataFilterMatch> results = eniwareEdgeMetadataBiz
				.findEniwareEdgeMetadata(criteria, null, null, null);
		EniwareEdgeMetadataFilterMatch result = null;
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
	 * Add metadata to a Edge. The metadata is merged only, and will not replace
	 * existing values.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param meta
	 *        the metadata to merge
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{EdgeId}" }, method = RequestMethod.POST)
	public Response<Object> addMetadata(@PathVariable("EdgeId") Long EdgeId,
			@RequestBody GeneralDatumMetadata meta) {
		eniwareEdgeMetadataBiz.addEniwareEdgeMetadata(EdgeId, meta);
		return response(null);
	}

	/**
	 * Completely replace the metadata for a given Edge ID, or create it if it
	 * doesn't already exist.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param meta
	 *        the metadata to store
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{EdgeId}" }, method = RequestMethod.PUT)
	public Response<Object> replaceMetadata(@PathVariable("EdgeId") Long EdgeId,
			@RequestBody GeneralDatumMetadata meta) {
		eniwareEdgeMetadataBiz.storeEniwareEdgeMetadata(EdgeId, meta);
		return response(null);
	}

	/**
	 * Completely remove the metadata for a given Edge ID.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @return the results
	 */
	@ResponseBody
	@RequestMapping(value = { "/{EdgeId}" }, method = RequestMethod.DELETE)
	public Response<Object> deleteMetadata(@PathVariable("EdgeId") Long EdgeId) {
		eniwareEdgeMetadataBiz.removeEniwareEdgeMetadata(EdgeId);
		return response(null);
	}

}
