/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web.api.v1;

import static org.eniware.web.domain.Response.response;
import java.util.List;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.support.BasicFilterResults;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.domain.UserEdgeConfirmation;
import org.eniware.support.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.eniware.central.security.SecurityUtils;
import org.eniware.central.web.support.WebServiceControllerSupport;
import org.eniware.web.domain.Response;

/**
 * Controller for user Edges web service API.
 * 
 * @version 1.1
 */
@Controller("v1EdgesController")
@RequestMapping(value = "/v1/sec/Edges")
public class EdgesController extends WebServiceControllerSupport {

	public final UserBiz userBiz;
	public final CertificateService certificateService;

	/**
	 * Constructor.
	 * 
	 * @param userBiz
	 *        The {@link UserBiz}.
	 * @param certificateService
	 *        The {@link CertificateService}.
	 */
	@Autowired
	public EdgesController(UserBiz userBiz, CertificateService certificateService) {
		super();
		this.userBiz = userBiz;
		this.certificateService = certificateService;
	}

	/**
	 * Get a listing of Edges for the active user.
	 * 
	 * @return The list of Edges available to the active user.
	 */
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	@ResponseBody
	public Response<FilterResults<UserEdge>> getMyEdges() {
		List<UserEdge> Edges = userBiz.getUserEdges(SecurityUtils.getCurrentActorUserId());
		FilterResults<UserEdge> result = new BasicFilterResults<UserEdge>(Edges, (long) Edges.size(), 0,
				Edges.size());
		return response(result);
	}

	/**
	 * Get a listing of pending Edge confirmations for the active user.
	 * 
	 * @return The list of pending Edge confirmations for the active user.
	 */
	@RequestMapping(value = "/pending", method = RequestMethod.GET)
	@ResponseBody
	public Response<FilterResults<UserEdgeConfirmation>> getPendingEdges() {
		List<UserEdgeConfirmation> pending = userBiz
				.getPendingUserEdgeConfirmations(SecurityUtils.getCurrentActorUserId());
		FilterResults<UserEdgeConfirmation> result = new BasicFilterResults<UserEdgeConfirmation>(
				pending, (long) pending.size(), 0, pending.size());
		return response(result);
	}

	/**
	 * Get a list of all archived Edges.
	 * 
	 * @return All archived Edges.
	 * @since 1.1
	 */
	@RequestMapping(value = "/archived", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<UserEdge>> getArchivedEdges() {
		List<UserEdge> Edges = userBiz.getArchivedUserEdges(SecurityUtils.getCurrentActorUserId());
		return Response.response(Edges);
	}

	/**
	 * Update the archived status of a set of Edges.
	 * 
	 * @param EdgeIds
	 *        The Edge IDs to update the archived status of.
	 * @param archived
	 *        {@code true} to archive, {@code false} to un-archive
	 * @return A success response.
	 * @since 1.1
	 */
	@RequestMapping(value = "/archived", method = RequestMethod.POST)
	@ResponseBody
	public Response<Object> updateArchivedStatus(@RequestParam("EdgeIds") Long[] EdgeIds,
			@RequestParam("archived") boolean archived) {
		userBiz.updateUserEdgeArchivedStatus(SecurityUtils.getCurrentActorUserId(), EdgeIds, archived);
		return Response.response(null);
	}
}
