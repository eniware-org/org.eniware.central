/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.in.web;

import org.eniware.central.in.biz.NetworkIdentityBiz;
import org.eniware.domain.NetworkAssociation;
import org.eniware.domain.NetworkAssociationDetails;
import org.eniware.domain.NetworkIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for {@link NetworkIdentityBiz} requests.
 *
 * @version 1.1
 */
@Controller
public class NetworkIdentityController {

	/** The default value for the {@code viewName} property. */
	public static final String DEFAULT_VIEW_NAME = "xml";

	private NetworkIdentityBiz networkIdentityBiz;
	private String viewName = DEFAULT_VIEW_NAME;

	//private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Constructor.
	 * 
	 * @param networkIdentityBiz
	 *        the {@link NetworkIdentityBiz} to use
	 */
	@Autowired
	public NetworkIdentityController(NetworkIdentityBiz networkIdentityBiz) {
		this.networkIdentityBiz = networkIdentityBiz;
	}

	/**
	 * Get the network identity, optionally as a {@link NetworkAssociation}.
	 * 
	 * <p>
	 * If both {@code username} and {@code confirmationKey} are non-null, then a
	 * {@link NetworkAssociation} will be returned in the model, rather than a
	 * plain {@link NetworkIdentity}.
	 * </p>
	 * 
	 * @param model
	 *        the model
	 * @param username
	 *        the optional network association username
	 * @param confirmationKey
	 *        the optional network association confirmation key
	 * @return the view name
	 */
	@RequestMapping(value = "/identity.do", method = RequestMethod.GET)
	public String getNetworkIdentityKey(Model model,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "key", required = false) String confirmationKey) {
		NetworkIdentity ident = networkIdentityBiz.getNetworkIdentity();
		if ( username != null && confirmationKey != null ) {
			NetworkAssociation association = networkIdentityBiz.getNetworkAssociation(username,
					confirmationKey);
			if ( association != null ) {
				NetworkAssociationDetails details = new NetworkAssociationDetails(association);
				details.setHost(ident.getHost());
				details.setIdentityKey(ident.getIdentityKey());
				details.setPort(ident.getPort());
				details.setTermsOfService(ident.getTermsOfService());
				details.setForceTLS(ident.isForceTLS());
				details.setNetworkServiceURLs(ident.getNetworkServiceURLs());
				ident = details;
			}
		}
		model.addAttribute(ident);
		return viewName;
	}

	public NetworkIdentityBiz getNetworkIdentityBiz() {
		return networkIdentityBiz;
	}

	public void setNetworkIdentityBiz(NetworkIdentityBiz networkIdentityBiz) {
		this.networkIdentityBiz = networkIdentityBiz;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

}
