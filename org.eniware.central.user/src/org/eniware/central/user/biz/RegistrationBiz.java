/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.biz;

import java.io.IOException;
import java.io.InputStream;

import org.eniware.central.security.AuthorizationException;
import org.eniware.central.user.domain.NewEdgeRequest;
import org.eniware.central.user.domain.PasswordEntry;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.domain.UserEdgeCertificateRenewal;
import org.eniware.domain.NetworkAssociation;
import org.eniware.domain.NetworkAssociationDetails;
import org.eniware.domain.NetworkCertificate;
import org.eniware.domain.RegistrationReceipt;
import org.joda.time.ReadablePeriod;

/**
 * API for user registration tasks.
 * 
 * @version 1.5
 */
public interface RegistrationBiz {

	/**
	 * Flag for a String value that should not change.
	 * 
	 * <p>
	 * For example, when updating a User, the password field can be left
	 * unchanged when set to this value.
	 * </p>
	 */
	static final String DO_NOT_CHANGE_VALUE = "**DO_NOT_CHANGE**";

	/**
	 * Register a new user.
	 * 
	 * <p>
	 * Use this method to register a new user. After registration the user will
	 * be stored in the back end, but the user will require confirmation before
	 * they can officially log into the application (see
	 * {@link #confirmRegisteredUser(RegistrationReceipt)}).
	 * </p>
	 * 
	 * @param user
	 *        the new user to register
	 * @return a confirmation string suitable to pass to
	 *         {@link #confirmRegisteredUser(String, String, BizContext)}
	 * @throws AuthorizationException
	 *         if the desired login is taken already, this exception will be
	 *         thrown with the reason code
	 *         {@link AuthorizationException.Reason#DUPLICATE_LOGIN}
	 */
	RegistrationReceipt registerUser(User user) throws AuthorizationException;

	/**
	 * Helper method for creating a RegistrationReceipt instance from a username
	 * and code.
	 * 
	 * <p>
	 * This has been added to support web flows.
	 * </p>
	 * 
	 * @param username
	 *        the username
	 * @param confirmationCode
	 *        the confirmation code
	 * @return the receipt instance
	 */
	public RegistrationReceipt createReceipt(String username, String confirmationCode);

	/**
	 * Confirm a registered user.
	 * 
	 * <p>
	 * After a user has registered (see {@link #registerUser(User)}) they must
	 * confirm the registration via this method. After confirmation the user can
	 * login as a normal user.
	 * </p>
	 * 
	 * @param receipt
	 *        the registration receipt to confirm
	 * @return the confirmed user
	 * @throws AuthorizationException
	 *         if the receipt details do not match those returned from a
	 *         previous call to {@link #registerUser(User)} then the reason code
	 *         will be set to
	 *         {@link AuthorizationException.Reason#REGISTRATION_NOT_CONFIRMED};
	 *         if the login is not found then
	 *         {@link AuthorizationException.Reason#UNKNOWN_EMAIL}; if the
	 *         account has already been confirmed then
	 *         {@link AuthorizationException.Reason#REGISTRATION_ALREADY_CONFIRMED}
	 */
	User confirmRegisteredUser(RegistrationReceipt receipt) throws AuthorizationException;

	/**
	 * Generate a new {@link NetworkAssociationDetails} entity.
	 * 
	 * <p>
	 * This will return a new {@link NetworkAssociationDetails} and the system
	 * details associated with specified User. The Edge will still need to
	 * associate with the system before it will be recognized.
	 * </p>
	 * 
	 * @param request
	 *        the Edge request details
	 * 
	 * @return new EdgeAssociationDetails entity
	 */
	NetworkAssociation createEdgeAssociation(NewEdgeRequest request);

	/**
	 * Get a {@link NetworkAssociationDetails} previously created via
	 * {@link #createEdgeAssociation(Long)}
	 * 
	 * @param userEdgeConfirmationId
	 *        the UserEdgeConfirmation ID to create the details for
	 * @return the NetworkAssociationDetails
	 * @throws AuthorizationException
	 *         if the acting user does not have permission to view the requested
	 *         confirmation then
	 *         {@link AuthorizationException.Reason#ACCESS_DENIED}
	 */
	NetworkAssociation getEdgeAssociation(Long userEdgeConfirmationId) throws AuthorizationException;

	/**
	 * Cancel a {@link NetworkAssociationDetails} previously created via
	 * {@link #createEdgeAssociation(Long)}
	 * 
	 * @param userEdgeConfirmationId
	 *        the UserEdgeConfirmation ID to create the details for
	 * @throws AuthorizationException
	 *         if the acting user does not have permission to view the requested
	 *         confirmation then
	 *         {@link AuthorizationException.Reason#ACCESS_DENIED}
	 */
	void cancelEdgeAssociation(Long userEdgeConfirmationId) throws AuthorizationException;

	/**
	 * Confirm a Edge association previously created via
	 * {@link #createEdgeAssociation(User)}.
	 * 
	 * <p>
	 * This method must be called after a call to
	 * {@link #createEdgeAssociation(Long, String)} to confirm the Edge
	 * association.
	 * </p>
	 * 
	 * @param username
	 *        the username to associate the Edge with
	 * @param confirmationKey
	 *        the confirmation code from
	 *        {@link NetworkAssociation#getConfirmationKey()}
	 * @return new RegistrationReceipt object
	 * @throws AuthorizationException
	 *         if the details do not match those returned from a previous call
	 *         to {@link #createEdgeAssociation(User)} then the reason code will
	 *         be set to
	 *         {@link AuthorizationException.Reason#REGISTRATION_NOT_CONFIRMED};
	 *         if the Edge has already been confirmed then
	 *         {@link AuthorizationException.Reason#REGISTRATION_ALREADY_CONFIRMED}
	 * @deprecated see {@link #confirmEdgeAssociation(NetworkAssociation)}
	 */
	@Deprecated
	NetworkCertificate confirmEdgeAssociation(String username, String confirmationKey)
			throws AuthorizationException;

	/**
	 * Confirm a Edge association previously created via
	 * {@link #createEdgeAssociation(User)}. This method must be called after a
	 * call to {@link #createEdgeAssociation(Long, String)} to confirm the Edge
	 * association. The {@code username} and {@code confirmationKey} are
	 * required. If a {@code keystorePassword} is provided a private key will be
	 * generated and a certificate will be automatically requested for the Edge,
	 * which will be encrypted with the provided password.
	 * 
	 * 
	 * @param association
	 *        the association details
	 * @return new RegistrationReceipt object
	 * @throws AuthorizationException
	 *         if the details do not match those returned from a previous call
	 *         to {@link #createEdgeAssociation(User)} then the reason code will
	 *         be set to
	 *         {@link AuthorizationException.Reason#REGISTRATION_NOT_CONFIRMED};
	 *         if the Edge has already been confirmed then
	 *         {@link AuthorizationException.Reason#REGISTRATION_ALREADY_CONFIRMED}
	 * @since 1.3
	 */
	NetworkCertificate confirmEdgeAssociation(NetworkAssociation association)
			throws AuthorizationException;

	/**
	 * Obtain a certificate generated and signed by EniwareUser on behalf of the
	 * Edge. This method can be called <em>after</em> a call to
	 * {@link #confirmEdgeAssociation(NetworkAssociation)} where a
	 * {@code keystorePassword} was also supplied. The {@code username},
	 * {@code confirmationKey}, and {@code keystorePassword} are required in
	 * this call, and must match the values previously used in
	 * {@link #confirmEdgeAssociation(NetworkAssociation)}.
	 * 
	 * @param association
	 *        the association details
	 * @return the network certificate
	 * @throws AuthorizationException
	 *         if the details do not match those returned from a previous call
	 *         to {@link #confirmEdgeAssociation(NetworkAssociation)}
	 * @since 1.3
	 */
	NetworkCertificate getEdgeCertificate(NetworkAssociation association);

	/**
	 * Renew a certificate generated and signed by EniwareUser by a previous call
	 * to {@link #confirmEdgeAssociation(NetworkAssociation)}. The provided
	 * certificate itself must be valid for the active {@code SecurityActor}.
	 * 
	 * This method is meant to support renewing certificates via a EniwareEdge.
	 * 
	 * @param pkcs12InputStream
	 *        the PKCS12 keystore data containing the Edge's existing
	 *        private/public key public/private key
	 * @return the renewed network certificate
	 * @throws AuthorizationException
	 *         if the details do not match the active {@code SecurityActor}
	 * @since 1.5
	 * @see #renewEdgeCertificate(UserEdge, String)
	 */
	NetworkCertificate renewEdgeCertificate(InputStream pkcs12InputStream, String keystorePassword)
			throws IOException;

	/**
	 * Get the period, ending at a Edge certificate's expiration date, that a
	 * Edge's certificate may be renewed.
	 * 
	 * @return The Edge certificate renewal period, or {@code null} if there is
	 *         no limit.
	 * @since 1.4
	 */
	ReadablePeriod getEdgeCertificateRenewalPeriod();

	/**
	 * Renew a certificate generated and signed by EniwareUser by a previous call
	 * to {@link #confirmEdgeAssociation(NetworkAssociation)} where a
	 * {@code keystorePassword} was also supplied. After the certificate is
	 * renewed, it must still be installed on the Edge. This method <em>may</em>
	 * attempt to inform the Edge of the available certificate. If so,
	 * {@link UserEdgeCertificateRenewal#getConfirmationKey()} will provide a
	 * unique key that can be passed to the
	 * {@link #getPendingEdgeCertificateRenewal(UserEdge, String)} method to
	 * check the status of the Edge certificate install process.
	 *
	 * This method is meant to support renewing certificates via EniwareUser.
	 *
	 * @param userEdge
	 *        the user Edge to renew the certificate for
	 * @param keystorePassword
	 *        the password used to encrypt the certificate store
	 * @return the network certificate renewal
	 * @throws AuthorizationException
	 *         if the details do not match those returned from a previous call
	 *         to {@link #confirmEdgeAssociation(NetworkAssociation)}
	 * @since 1.4
	 */
	UserEdgeCertificateRenewal renewEdgeCertificate(UserEdge userEdge, String keystorePassword);

	/**
	 * Get a certificate that has been renewed via
	 * {@link #renewEdgeCertificate(UserEdge, String)}. This can be used to
	 * check if the certificate has been installed by the Edge.
	 * 
	 * @param userEdge
	 *        the user Edge to renew the certificate for
	 * @param confirmationKey
	 *        a confirmation key previously returned by
	 *        {@link RegistrationBiz#renewEdgeCertificate(UserEdge, String)}
	 * @return the network certificate renewal, or <em>null</em> if not
	 *         available
	 * @throws AuthorizationException
	 *         if the details do not match those returned from a previous call
	 *         to {@link #confirmEdgeAssociation(NetworkAssociation)}
	 * @since 1.4
	 */
	UserEdgeCertificateRenewal getPendingEdgeCertificateRenewal(UserEdge userEdge,
			String confirmationKey);

	/**
	 * Update the details of a user entity.
	 * 
	 * <p>
	 * The {@link User#getId()} value must be populated with the ID of the user
	 * to update, and then any modifiable fields that are not <em>null</em> will
	 * be updated with the provided value.
	 * </p>
	 * 
	 * @param userEntry
	 *        the input data
	 * @return the updated user entity
	 */
	User updateUser(User userEntry);

	/**
	 * Generate a password reset receipt for a given username.
	 * 
	 * @param email
	 *        the email to generate the receipt for
	 * @return the receipt
	 * @throws AuthorizationException
	 *         if the username is not found, then
	 *         {@link AuthorizationException.Reason#UNKNOWN_EMAIL}
	 */
	RegistrationReceipt generateResetPasswordReceipt(String email) throws AuthorizationException;

	/**
	 * Reset a user's password.
	 * 
	 * @param receipt
	 *        the receipt obtained previously via a call to
	 *        {@link #generateResetPasswordReceipt(String)}
	 * @param password
	 *        the new password to set
	 * @throws AuthorizationException
	 *         if the user cannot be found, or the details do not match those
	 *         returned from a previous call to
	 *         {@link #generateResetPasswordReceipt(String)} then the reason
	 *         code will be set to
	 *         {@link AuthorizationException.Reason#FORGOTTEN_PASSWORD_NOT_CONFIRMED}
	 */
	void resetPassword(RegistrationReceipt receipt, PasswordEntry password);

}
