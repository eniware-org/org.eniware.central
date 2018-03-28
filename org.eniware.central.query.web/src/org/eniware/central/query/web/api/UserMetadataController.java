/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.query.web.api;

import static net.solarnetwork.web.domain.Response.response;
import java.util.NoSuchElementException;

import org.eniware.central.domain.FilterResults;
import org.eniware.central.user.biz.UserMetadataBiz;
import org.eniware.central.user.domain.UserFilterCommand;
import org.eniware.central.user.domain.UserMetadataFilterMatch;
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
 * Controller for read-only user metadata access.
 * 
 * @version 1.0
 */
@Controller("v1UserMetadataController")
@RequestMapping({ "/api/v1/pub/users/meta", "/api/v1/sec/users/meta" })
public class UserMetadataController extends WebServiceControllerSupport {

	private final UserMetadataBiz userMetadataBiz;

	/**
	 * Constructor.
	 * 
	 * @param userMetadataBiz
	 *        the UserMetadataBiz to use
	 */
	@Autowired
	public UserMetadataController(UserMetadataBiz userMetadataBiz) {
		super();
		this.userMetadataBiz = userMetadataBiz;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
	}

	/**
	 * Get metadata for a specific user ID.
	 * 
	 * @param userId
	 *        the user ID
	 * @return the result
	 */
	@ResponseBody
	@RequestMapping(value = { "/{userId}" }, method = RequestMethod.GET)
	public Response<UserMetadataFilterMatch> getMetadata(@PathVariable("userId") Long userId) {
		UserFilterCommand criteria = new UserFilterCommand();
		criteria.setUserId(userId);
		FilterResults<UserMetadataFilterMatch> results = userMetadataBiz.findUserMetadata(criteria, null,
				null, null);
		UserMetadataFilterMatch result = null;
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
