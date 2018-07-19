/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.support;

import java.io.IOException;
import java.io.InputStream;

import org.eniware.central.security.AuthorizationException;
import org.eniware.central.user.biz.RegistrationBiz;
import org.eniware.central.user.domain.NewNodeRequest;
import org.eniware.central.user.domain.PasswordEntry;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.domain.UserEdgeCertificateRenewal;
import org.eniware.domain.NetworkAssociation;
import org.eniware.domain.NetworkCertificate;
import org.eniware.domain.RegistrationReceipt;
import org.joda.time.ReadablePeriod;

/**
 * Delegating implementation of {@link RegistrationBiz}, mostly to help with
 * AOP.
 * 
 * @version 1.1
 */
public class DelegatingRegistrationBiz implements RegistrationBiz {

	private final RegistrationBiz delegate;

	/**
	 * Construct with a delegate;
	 * 
	 * @param delegate
	 *        the delegate
	 */
	public DelegatingRegistrationBiz(RegistrationBiz delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public RegistrationReceipt registerUser(User user) throws AuthorizationException {
		return delegate.registerUser(user);
	}

	@Override
	public RegistrationReceipt createReceipt(String username, String confirmationCode) {
		return delegate.createReceipt(username, confirmationCode);
	}

	@Override
	public User confirmRegisteredUser(RegistrationReceipt receipt) throws AuthorizationException {
		return delegate.confirmRegisteredUser(receipt);
	}

	@Override
	public NetworkAssociation createNodeAssociation(NewNodeRequest request) {
		return delegate.createNodeAssociation(request);
	}

	@Override
	public NetworkAssociation getNodeAssociation(Long userNodeConfirmationId)
			throws AuthorizationException {
		return delegate.getNodeAssociation(userNodeConfirmationId);
	}

	@Override
	public void cancelNodeAssociation(Long userNodeConfirmationId) throws AuthorizationException {
		delegate.cancelNodeAssociation(userNodeConfirmationId);
	}

	@SuppressWarnings("deprecation")
	@Override
	public NetworkCertificate confirmNodeAssociation(String username, String confirmationKey)
			throws AuthorizationException {
		return delegate.confirmNodeAssociation(username, confirmationKey);
	}

	@Override
	public NetworkCertificate confirmNodeAssociation(NetworkAssociation association)
			throws AuthorizationException {
		return delegate.confirmNodeAssociation(association);
	}

	@Override
	public NetworkCertificate getNodeCertificate(NetworkAssociation association) {
		return delegate.getNodeCertificate(association);
	}

	@Override
	public NetworkCertificate renewNodeCertificate(InputStream pkcs12InputStream,
			String keystorePassword) throws IOException {
		return delegate.renewNodeCertificate(pkcs12InputStream, keystorePassword);
	}

	@Override
	public ReadablePeriod getNodeCertificateRenewalPeriod() {
		return delegate.getNodeCertificateRenewalPeriod();
	}

	@Override
	public UserEdgeCertificateRenewal renewNodeCertificate(UserEdge userNode, String keystorePassword) {
		return delegate.renewNodeCertificate(userNode, keystorePassword);
	}

	@Override
	public UserEdgeCertificateRenewal getPendingNodeCertificateRenewal(UserEdge userNode,
			String confirmationKey) {
		return delegate.getPendingNodeCertificateRenewal(userNode, confirmationKey);
	}

	@Override
	public User updateUser(User userEntry) {
		return delegate.updateUser(userEntry);
	}

	@Override
	public RegistrationReceipt generateResetPasswordReceipt(String email) throws AuthorizationException {
		return delegate.generateResetPasswordReceipt(email);
	}

	@Override
	public void resetPassword(RegistrationReceipt receipt, PasswordEntry password) {
		delegate.resetPassword(receipt, password);
	}

}
