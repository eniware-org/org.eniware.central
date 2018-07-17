/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web.api.v1;

import static org.eniware.web.domain.Response.response;
import java.util.List;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.support.BasicFilterResults;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.domain.UserNode;
import org.eniware.central.user.domain.UserNodeConfirmation;
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
 * Controller for user nodes web service API.
 * 
 * @version 1.1
 */
@Controller("v1nodesController")
@RequestMapping(value = "/v1/sec/nodes")
public class NodesController extends WebServiceControllerSupport {

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
	public NodesController(UserBiz userBiz, CertificateService certificateService) {
		super();
		this.userBiz = userBiz;
		this.certificateService = certificateService;
	}

	/**
	 * Get a listing of nodes for the active user.
	 * 
	 * @return The list of nodes available to the active user.
	 */
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	@ResponseBody
	public Response<FilterResults<UserNode>> getMyNodes() {
		List<UserNode> nodes = userBiz.getUserNodes(SecurityUtils.getCurrentActorUserId());
		FilterResults<UserNode> result = new BasicFilterResults<UserNode>(nodes, (long) nodes.size(), 0,
				nodes.size());
		return response(result);
	}

	/**
	 * Get a listing of pending node confirmations for the active user.
	 * 
	 * @return The list of pending node confirmations for the active user.
	 */
	@RequestMapping(value = "/pending", method = RequestMethod.GET)
	@ResponseBody
	public Response<FilterResults<UserNodeConfirmation>> getPendingNodes() {
		List<UserNodeConfirmation> pending = userBiz
				.getPendingUserNodeConfirmations(SecurityUtils.getCurrentActorUserId());
		FilterResults<UserNodeConfirmation> result = new BasicFilterResults<UserNodeConfirmation>(
				pending, (long) pending.size(), 0, pending.size());
		return response(result);
	}

	/**
	 * Get a list of all archived nodes.
	 * 
	 * @return All archived nodes.
	 * @since 1.1
	 */
	@RequestMapping(value = "/archived", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<UserNode>> getArchivedNodes() {
		List<UserNode> nodes = userBiz.getArchivedUserNodes(SecurityUtils.getCurrentActorUserId());
		return Response.response(nodes);
	}

	/**
	 * Update the archived status of a set of nodes.
	 * 
	 * @param nodeIds
	 *        The node IDs to update the archived status of.
	 * @param archived
	 *        {@code true} to archive, {@code false} to un-archive
	 * @return A success response.
	 * @since 1.1
	 */
	@RequestMapping(value = "/archived", method = RequestMethod.POST)
	@ResponseBody
	public Response<Object> updateArchivedStatus(@RequestParam("nodeIds") Long[] nodeIds,
			@RequestParam("archived") boolean archived) {
		userBiz.updateUserNodeArchivedStatus(SecurityUtils.getCurrentActorUserId(), nodeIds, archived);
		return Response.response(null);
	}
}
