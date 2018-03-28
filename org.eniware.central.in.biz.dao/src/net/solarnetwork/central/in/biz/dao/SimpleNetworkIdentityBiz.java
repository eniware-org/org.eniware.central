/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package net.solarnetwork.central.in.biz.dao;

import java.util.Map;

import org.eniware.central.dao.NetworkAssociationDao;
import org.eniware.central.in.biz.NetworkIdentityBiz;
import org.eniware.domain.BasicNetworkIdentity;
import org.eniware.domain.NetworkAssociation;
import org.eniware.domain.NetworkIdentity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simple implementation of {@link NetworkIdentityBiz}.
 *
 * @version 1.1
 */
public class SimpleNetworkIdentityBiz implements NetworkIdentityBiz {

	private String networkIdentityKey;
	private String termsOfService;
	private String host;
	private Integer port;
	private boolean forceTLS;
	private Map<String, String> networkServiceURLs;

	private NetworkAssociationDao networkAssociationDao;

	@Override
	public NetworkIdentity getNetworkIdentity() {
		BasicNetworkIdentity ident = new BasicNetworkIdentity(networkIdentityKey, termsOfService, host,
				port, forceTLS);
		ident.setNetworkServiceURLs(networkServiceURLs);
		return ident;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public NetworkAssociation getNetworkAssociation(String username, String confirmationKey) {
		return networkAssociationDao.getNetworkAssociationForConfirmationKey(username, confirmationKey);
	}

	public void setNetworkIdentityKey(String networkIdentityKey) {
		this.networkIdentityKey = networkIdentityKey;
	}

	public void setTermsOfService(String termsOfService) {
		this.termsOfService = termsOfService;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setForceTLS(boolean forceTLS) {
		this.forceTLS = forceTLS;
	}

	public void setNetworkAssociationDao(NetworkAssociationDao networkAssociationDao) {
		this.networkAssociationDao = networkAssociationDao;
	}

	public void setNetworkServiceURLs(Map<String, String> networkServiceURLs) {
		this.networkServiceURLs = networkServiceURLs;
	}

}
