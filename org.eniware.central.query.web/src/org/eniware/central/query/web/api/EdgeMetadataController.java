/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.web.api;

import static org.eniware.web.domain.Response.response;
import java.util.NoSuchElementException;

import org.eniware.central.biz.EniwareEdgeMetadataBiz;
import org.eniware.central.datum.domain.DatumFilterCommand;
import org.eniware.central.domain.FilterResults;
import org.eniware.central.domain.EniwareEdgeMetadataFilterMatch;
import org.eniware.central.user.biz.UserBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;

/**
 * Controller for read-only Edge metadata access.
 * 
 * @version 1.0
 */
@Controller("v1EdgeMetadataController")
@RequestMapping({ "/api/v1/pub/Edges/meta", "/api/v1/sec/Edges/meta" })
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
			criteria.setEdgeIds(authorizedEdgeIdsForCurrentActor(userBiz));
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

}
