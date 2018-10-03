/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.support;

import java.io.IOException;
import java.io.InputStream;

import org.eniware.central.security.AuthorizationException;
import org.eniware.central.user.biz.RegistrationBiz;
import org.eniware.central.user.domain.NewEdgeRequest;
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
	public NetworkAssociation createEdgeAssociation(NewEdgeRequest request) {
		return delegate.createEdgeAssociation(request);
	}

	@Override
	public NetworkAssociation getEdgeAssociation(Long userEdgeConfirmationId)
			throws AuthorizationException {
		return delegate.getEdgeAssociation(userEdgeConfirmationId);
	}

	@Override
	public void cancelEdgeAssociation(Long userEdgeConfirmationId) throws AuthorizationException {
		delegate.cancelEdgeAssociation(userEdgeConfirmationId);
	}

	@SuppressWarnings("deprecation")
	@Override
	public NetworkCertificate confirmEdgeAssociation(String username, String confirmationKey)
			throws AuthorizationException {
		return delegate.confirmEdgeAssociation(username, confirmationKey);
	}

	@Override
	public NetworkCertificate confirmEdgeAssociation(NetworkAssociation association)
			throws AuthorizationException {
		return delegate.confirmEdgeAssociation(association);
	}

	@Override
	public NetworkCertificate getEdgeCertificate(NetworkAssociation association) {
		return delegate.getEdgeCertificate(association);
	}

	@Override
	public NetworkCertificate renewEdgeCertificate(InputStream pkcs12InputStream,
			String keystorePassword) throws IOException {
		return delegate.renewEdgeCertificate(pkcs12InputStream, keystorePassword);
	}

	@Override
	public ReadablePeriod getEdgeCertificateRenewalPeriod() {
		return delegate.getEdgeCertificateRenewalPeriod();
	}

	@Override
	public UserEdgeCertificateRenewal renewEdgeCertificate(UserEdge userEdge, String keystorePassword) {
		return delegate.renewEdgeCertificate(userEdge, keystorePassword);
	}

	@Override
	public UserEdgeCertificateRenewal getPendingEdgeCertificateRenewal(UserEdge userEdge,
			String confirmationKey) {
		return delegate.getPendingEdgeCertificateRenewal(userEdge, confirmationKey);
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
