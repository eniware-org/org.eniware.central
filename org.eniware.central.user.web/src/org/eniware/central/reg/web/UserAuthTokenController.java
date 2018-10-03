/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eniware.central.domain.Aggregation;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.domain.UserAuthToken;
import org.eniware.central.user.domain.UserAuthTokenStatus;
import org.eniware.central.user.domain.UserAuthTokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.eniware.central.security.BasicSecurityPolicy;
import org.eniware.central.security.SecurityPolicy;
import org.eniware.central.security.SecurityUser;
import org.eniware.central.security.SecurityUtils;
import org.eniware.web.domain.Response;

/**
 * Controller for user authorization ticket management.
 *
 * @version 1.2
 */
@Controller
@RequestMapping("/sec/auth-tokens")
public class UserAuthTokenController extends ControllerSupport {

	private final UserBiz userBiz;

	@Autowired
	public UserAuthTokenController(UserBiz userBiz) {
		super();
		this.userBiz = userBiz;
	}

	@ModelAttribute("policyAggregations")
	public Set<Aggregation> policyAggregations() {
		return EnumSet.of(Aggregation.FiveMinute, Aggregation.TenMinute, Aggregation.FifteenMinute,
				Aggregation.ThirtyMinute, Aggregation.Hour, Aggregation.Day, Aggregation.Week,
				Aggregation.Month, Aggregation.RunningTotal);
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String view(Model model) {
		final SecurityUser user = SecurityUtils.getCurrentUser();
		List<UserAuthToken> tokens = userBiz.getAllUserAuthTokens(user.getUserId());
		if ( tokens != null ) {
			List<UserAuthToken> userTokens = new ArrayList<UserAuthToken>(tokens.size());
			List<UserAuthToken> dataTokens = new ArrayList<UserAuthToken>(tokens.size());
			for ( UserAuthToken token : tokens ) {
				switch (token.getType()) {
					case User:
						userTokens.add(token);
						break;

					case ReadEdgeData:
						dataTokens.add(token);
						break;
				}
			}
			model.addAttribute("userAuthTokens", userTokens);
			model.addAttribute("dataAuthTokens", dataTokens);
		}
		model.addAttribute("userEdges", userBiz.getUserEdges(user.getUserId()));
		return "auth-tokens/view";
	}

	@RequestMapping(value = "/generateUser", method = RequestMethod.POST)
	@ResponseBody
	public Response<UserAuthToken> generateUserToken() {
		final SecurityUser user = SecurityUtils.getCurrentUser();
		UserAuthToken token = userBiz.generateUserAuthToken(user.getUserId(), UserAuthTokenType.User,
				(SecurityPolicy) null);
		return new Response<UserAuthToken>(token);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Response<Object> deleteUserToken(@RequestParam("id") String tokenId) {
		final SecurityUser user = SecurityUtils.getCurrentUser();
		userBiz.deleteUserAuthToken(user.getUserId(), tokenId);
		return new Response<Object>();
	}

	@RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
	@ResponseBody
	public Response<Object> changeStatus(@RequestParam("id") String tokenId,
			@RequestParam("status") UserAuthTokenStatus status) {
		final SecurityUser user = SecurityUtils.getCurrentUser();
		userBiz.updateUserAuthTokenStatus(user.getUserId(), tokenId, status);
		return new Response<Object>();
	}

	@RequestMapping(value = "/generateData", method = RequestMethod.POST)
	@ResponseBody
	public Response<UserAuthToken> generateDataToken(
			@RequestParam(value = "EdgeId", required = false) Set<Long> EdgeIds,
			@RequestParam(value = "sourceId", required = false) Set<String> sourceIds,
			@RequestParam(value = "minAggregation", required = false) Aggregation minAggregation,
			@RequestParam(value = "EdgeMetadataPath", required = false) Set<String> EdgeMetadataPaths,
			@RequestParam(value = "userMetadataPath", required = false) Set<String> userMetadataPaths) {
		final SecurityUser user = SecurityUtils.getCurrentUser();
		UserAuthToken token = userBiz.generateUserAuthToken(user.getUserId(),
				UserAuthTokenType.ReadEdgeData,
				new BasicSecurityPolicy.Builder().withEdgeIds(EdgeIds).withSourceIds(sourceIds)
						.withMinAggregation(minAggregation).withEdgeMetadataPaths(EdgeMetadataPaths)
						.withUserMetadataPaths(userMetadataPaths).build());
		return new Response<UserAuthToken>(token);
	}

}
