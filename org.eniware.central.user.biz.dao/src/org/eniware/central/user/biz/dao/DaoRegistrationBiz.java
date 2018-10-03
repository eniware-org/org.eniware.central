/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.biz.dao;

import static org.eniware.central.user.biz.dao.UserBizConstants.getOriginalEmail;
import static org.eniware.central.user.biz.dao.UserBizConstants.getUnconfirmedEmail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.eniware.central.ValidationException;
import org.eniware.central.dao.EniwareLocationDao;
import org.eniware.central.dao.EniwareEdgeDao;
import org.eniware.central.domain.EniwareLocation;
import org.eniware.central.domain.EniwareEdge;
import org.eniware.central.in.biz.NetworkIdentityBiz;
import org.eniware.central.instructor.biz.InstructorBiz;
import org.eniware.central.instructor.domain.Instruction;
import org.eniware.central.instructor.domain.InstructionParameter;
import org.eniware.central.instructor.domain.EdgeInstruction;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.PasswordEncoder;
import org.eniware.central.security.SecurityEdge;
import org.eniware.central.security.SecurityUtils;
import org.eniware.central.security.AuthorizationException.Reason;
import org.eniware.central.user.biz.EdgePKIBiz;
import org.eniware.central.user.biz.RegistrationBiz;
import org.eniware.central.user.dao.UserDao;
import org.eniware.central.user.dao.UserEdgeCertificateDao;
import org.eniware.central.user.dao.UserEdgeConfirmationDao;
import org.eniware.central.user.dao.UserEdgeDao;
import org.eniware.central.user.domain.BasicUserEdgeCertificateRenewal;
import org.eniware.central.user.domain.NewEdgeRequest;
import org.eniware.central.user.domain.PasswordEntry;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.domain.UserEdgeCertificate;
import org.eniware.central.user.domain.UserEdgeCertificateInstallationStatus;
import org.eniware.central.user.domain.UserEdgeCertificateRenewal;
import org.eniware.central.user.domain.UserEdgeCertificateStatus;
import org.eniware.central.user.domain.UserEdgeConfirmation;
import org.eniware.central.user.domain.UserEdgePK;
import org.eniware.domain.BasicRegistrationReceipt;
import org.eniware.domain.NetworkAssociation;
import org.eniware.domain.NetworkAssociationDetails;
import org.eniware.domain.NetworkCertificate;
import org.eniware.domain.NetworkIdentity;
import org.eniware.domain.RegistrationReceipt;
import org.eniware.support.CertificateException;
import org.eniware.support.CertificateService;
import org.eniware.util.JavaBeanXmlSerializer;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * DAO-based implementation of {@link RegistrationBiz}.
 * 
 * <p>
 * The configurable properties of this class are:
 * </p>
 * 
 * <dl class="class-properties">
 * <dt>userDao</dt>
 * <dd>The {@link UserDao} to use for persisting users.</dd>
 * 
 * <dt>userValidator</dt>
 * <dd>A {@link Validator} to use for validating user objects.</dd>
 * 
 * <dt>passwordEncoder</dt>
 * <dd>The {@link PasswordEncoder} to use for encrypting passwords.</dd>
 * </dl>
 * 
 * @version 1.8
 */
public class DaoRegistrationBiz implements RegistrationBiz {

	public static final SortedSet<String> DEFAULT_CONFIRMED_USER_ROLES = Collections
			.unmodifiableSortedSet(new TreeSet<String>(Arrays.asList("ROLE_USER")));

	/**
	 * Instruction topic for sending a renewed certificate to a Edge.
	 * 
	 * @since 1.8
	 */
	public static final String INSTRUCTION_TOPIC_RENEW_CERTIFICATE = "RenewCertificate";

	/**
	 * Instruction parameter for certificate data. Since instruction parameters
	 * are limited in length, there can be more than one parameter of the same
	 * key, with the full data being the concatenation of all parameter values.
	 * 
	 * @since 1.8
	 */
	public static final String INSTRUCTION_PARAM_CERTIFICATE = "Certificate";

	/**
	 * The default maximum length for instruction parameter values.
	 * 
	 * @since 1.8
	 */
	public static final int INSTRUCTION_PARAM_DEFAULT_MAX_LENGTH = 256;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private UserDao userDao;
	private UserEdgeDao userEdgeDao;
	private UserEdgeConfirmationDao userEdgeConfirmationDao;
	private UserEdgeCertificateDao userEdgeCertificateDao;
	private Validator userValidator;
	private EniwareEdgeDao eniwareEdgeDao;
	private EniwareLocationDao eniwareLocationDao;
	private NetworkIdentityBiz networkIdentityBiz;
	private PasswordEncoder passwordEncoder;
	private Set<String> confirmedUserRoles = DEFAULT_CONFIRMED_USER_ROLES;
	private JavaBeanXmlSerializer xmlSerializer = new JavaBeanXmlSerializer();
	private Ehcache emailThrottleCache;
	private CertificateService certificateService;
	private EdgePKIBiz EdgePKIBiz;
	private int EdgePrivateKeySize = 2048;
	private String EdgeKeystoreAlias = "Edge";
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private int approveCSRMaximumWaitSecs = 15;
	private InstructorBiz instructorBiz;
	private int instructionParamMaxLength = INSTRUCTION_PARAM_DEFAULT_MAX_LENGTH;

	private Period invitationExpirationPeriod = new Period(0, 0, 1, 0, 0, 0, 0, 0); // 1 week
	private Period EdgeCertificateRenewalPeriod = new Period(0, 3, 0, 0, 0, 0, 0, 0); // 3 months
	private String defaultEniwareLocationName = "Unknown";
	private String networkCertificateSubjectDNFormat = "UID=%s,O=EniwareNetwork";

	private User getCurrentUser() {
		String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = null;
		if ( currentUserEmail != null ) {
			user = userDao.getUserByEmail(currentUserEmail);
		}
		return user;
	}

	@Override
	public RegistrationReceipt createReceipt(String username, String confirmationCode) {
		return new BasicRegistrationReceipt(username, confirmationCode);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public RegistrationReceipt registerUser(User user) throws AuthorizationException {

		// perform service-side validation
		if ( this.userValidator != null ) {
			Errors errors = new BindException(user, "user");
			this.userValidator.validate(user, errors);
			if ( errors.hasErrors() ) {
				throw new ValidationException(errors);
			}
		}

		User clone = (User) user.clone();

		// store user
		prepareUserForStorage(clone);

		// adjust email so we know they are not confirmed
		clone.setEmail(getUnconfirmedEmail(clone.getEmail()));

		User entity;
		try {
			entity = userDao.get(userDao.store(clone));
		} catch ( DataIntegrityViolationException e ) {
			if ( log.isWarnEnabled() ) {
				log.warn("Duplicate user registration: " + clone.getEmail());
			}
			throw new AuthorizationException(user.getEmail(),
					AuthorizationException.Reason.DUPLICATE_EMAIL);
		}

		// generate confirmation string
		String conf = calculateConfirmationCode(entity);
		if ( log.isInfoEnabled() ) {
			log.info("Registered user '" + entity.getEmail() + "' with confirmation '" + conf + "'");
		}

		return new BasicRegistrationReceipt(entity.getEmail(), conf);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public User confirmRegisteredUser(RegistrationReceipt receipt) throws AuthorizationException {
		final String confirmedEmail;
		try {
			confirmedEmail = getOriginalEmail(receipt.getUsername());
		} catch ( IllegalArgumentException e ) {
			throw new AuthorizationException(receipt.getUsername(),
					AuthorizationException.Reason.UNKNOWN_EMAIL);
		}

		// first check if already registered
		User entity = userDao.getUserByEmail(confirmedEmail);
		if ( entity != null ) {
			throw new AuthorizationException(receipt.getUsername(),
					AuthorizationException.Reason.REGISTRATION_ALREADY_CONFIRMED);
		}

		final String unregEmail = receipt.getUsername();
		entity = userDao.getUserByEmail(unregEmail);
		if ( entity == null ) {
			throw new AuthorizationException(receipt.getUsername(),
					AuthorizationException.Reason.UNKNOWN_EMAIL);
		}

		// validate confirmation code
		String entityConf = calculateConfirmationCode(entity);
		if ( !entityConf.equals(receipt.getConfirmationCode()) ) {
			throw new AuthorizationException(receipt.getUsername(),
					AuthorizationException.Reason.REGISTRATION_NOT_CONFIRMED);
		}

		// change their email to "registered"
		entity.setEmail(confirmedEmail);

		// update confirmed user
		entity = userDao.get(userDao.store(entity));

		// store initial user roles
		userDao.storeUserRoles(entity, confirmedUserRoles);
		entity.setRoles(userDao.getUserRoles(entity));

		return entity;
	}

	private String calculateConfirmationCode(User user) {
		return DigestUtils.sha256Hex(
				user.getCreated().getMillis() + user.getId() + user.getEmail() + user.getPassword());
	}

	private void prepareUserForStorage(User user) throws AuthorizationException {

		// check for "unchanged" password value
		if ( user.getId() != null && DO_NOT_CHANGE_VALUE.equals(user.getPassword()) ) {
			// retrieve user from back-end and copy that password onto our user
			User realUser = userDao.get(user.getId());
			user.setPassword(realUser.getPassword());
		}

		// check password is encrypted
		if ( !passwordEncoder.isPasswordEncrypted(user.getPassword()) ) {
			// encrypt the password now
			String encryptedPass = passwordEncoder.encode(user.getPassword());
			user.setPassword(encryptedPass);
		}
		if ( user.getCreated() == null ) {
			user.setCreated(new DateTime());
		}

		// verify email not already in use, after trimming
		if ( user.getEmail() != null && user.getEmail().trim().equals(user.getEmail()) == false ) {
			user.setEmail(user.getEmail().trim());
		}
		User existingUser = userDao.getUserByEmail(user.getEmail());
		if ( existingUser != null && !existingUser.getId().equals(user.getId()) ) {
			throw new AuthorizationException(user.getEmail(),
					AuthorizationException.Reason.DUPLICATE_EMAIL);
		}

		// sent enabled if not configured
		if ( user.getEnabled() == null ) {
			user.setEnabled(Boolean.TRUE);
		}
	}

	private String encodeNetworkAssociationDetails(NetworkAssociationDetails details) {
		ByteArrayOutputStream byos = new ByteArrayOutputStream();
		Base64OutputStream b64 = new Base64OutputStream(byos, true);
		GZIPOutputStream zip = null;
		try {
			zip = new GZIPOutputStream(b64);
			xmlSerializer.renderBean(details, zip);
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		} finally {
			if ( zip != null ) {
				try {
					zip.flush();
					zip.close();
				} catch ( IOException e2 ) {
					// ignore me
				}
			}
			if ( b64 != null ) {
				try {
					b64.flush();
					b64.close();
				} catch ( IOException e ) {
					// ignore me
				}
			}
		}
		return byos.toString();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public NetworkAssociation createEdgeAssociation(final NewEdgeRequest request) {
		User user = null;
		if ( request.getUserId() == null ) {
			user = getCurrentUser();
		} else {
			user = userDao.get(request.getUserId());
		}
		assert user != null;

		DateTime now = new DateTime();
		NetworkIdentity ident = networkIdentityBiz.getNetworkIdentity();

		NetworkAssociationDetails details = new NetworkAssociationDetails();
		details.setHost(ident.getHost());
		details.setPort(ident.getPort());
		details.setForceTLS(ident.isForceTLS());
		details.setIdentityKey(ident.getIdentityKey());
		details.setUsername(user.getEmail());
		details.setExpiration(now.plus(invitationExpirationPeriod).toDate());

		String confKey = DigestUtils.sha256Hex(
				String.valueOf(now.getMillis()) + details.getIdentityKey() + details.getTermsOfService()
						+ details.getUsername() + details.getExpiration() + request.getSecurityPhrase());
		details.setConfirmationKey(confKey);

		String xml = encodeNetworkAssociationDetails(details);
		details.setConfirmationKey(xml);

		// the following are not encoded into confirmation XML
		details.setSecurityPhrase(request.getSecurityPhrase());
		details.setTermsOfService(ident.getTermsOfService());

		// create UserEdgeConfirmation now
		UserEdgeConfirmation conf = new UserEdgeConfirmation();
		conf.setCreated(now);
		conf.setUser(user);
		conf.setConfirmationKey(confKey);
		conf.setSecurityPhrase(request.getSecurityPhrase());
		conf.setCountry(request.getLocale().getCountry());
		conf.setTimeZoneId(request.getTimeZone().getID());
		userEdgeConfirmationDao.store(conf);

		return details;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public NetworkAssociation getEdgeAssociation(final Long userEdgeConfirmationId)
			throws AuthorizationException {
		final UserEdgeConfirmation conf = userEdgeConfirmationDao.get(userEdgeConfirmationId);
		if ( conf == null ) {
			return null;
		}
		final NetworkIdentity ident = networkIdentityBiz.getNetworkIdentity();
		NetworkAssociationDetails details = new NetworkAssociationDetails();
		details.setHost(ident.getHost());
		details.setPort(ident.getPort());
		details.setForceTLS(ident.isForceTLS());
		details.setNetworkId(conf.getEdgeId());
		details.setIdentityKey(ident.getIdentityKey());
		details.setUsername(conf.getUser().getEmail());
		details.setExpiration(conf.getCreated().plus(invitationExpirationPeriod).toDate());
		details.setConfirmationKey(conf.getConfirmationKey());

		String xml = encodeNetworkAssociationDetails(details);
		details.setConfirmationKey(xml);

		// the following are not encoded into confirmation XML
		details.setSecurityPhrase(conf.getSecurityPhrase());
		details.setTermsOfService(ident.getTermsOfService());

		return details;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void cancelEdgeAssociation(Long userEdgeConfirmationId) throws AuthorizationException {
		final UserEdgeConfirmation conf = userEdgeConfirmationDao.get(userEdgeConfirmationId);
		if ( conf == null ) {
			return;
		}
		userEdgeConfirmationDao.delete(conf);
	}

	private String calculateEdgeAssociationConfirmationCode(DateTime date, Long EdgeId) {
		return DigestUtils.sha256Hex(String.valueOf(date.getMillis()) + String.valueOf(EdgeId));
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public NetworkCertificate confirmEdgeAssociation(final String username,
			final String confirmationKey) {
		assert username != null;
		assert confirmationKey != null;
		return confirmEdgeAssociation(new NetworkAssociationDetails(username, confirmationKey, null));
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public NetworkCertificate getEdgeCertificate(final NetworkAssociation association) {
		if ( association == null ) {
			throw new IllegalArgumentException("NetworkAssociation must be provided.");
		}
		final String username = association.getUsername();
		final String confirmationKey = association.getConfirmationKey();
		final String keystorePassword = association.getKeystorePassword();
		if ( username == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		if ( confirmationKey == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		if ( keystorePassword == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}

		final User user = userDao.getUserByEmail(username);
		if ( user == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_EMAIL, username);
		}

		final UserEdgeConfirmation conf = userEdgeConfirmationDao.getConfirmationForKey(user.getId(),
				confirmationKey);
		if ( conf == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT,
					confirmationKey);
		}

		final Long EdgeId = conf.getEdgeId();
		if ( EdgeId == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}

		final UserEdgeCertificate cert = userEdgeCertificateDao
				.get(new UserEdgePK(user.getId(), EdgeId));
		if ( cert == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}

		final KeyStore keystore;
		try {
			keystore = cert.getKeyStore(keystorePassword);
		} catch ( CertificateException e ) {
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, null);
		}

		final NetworkAssociationDetails details = new NetworkAssociationDetails();

		final X509Certificate certificate = cert.getEdgeCertificate(keystore);
		if ( certificate != null ) {
			details.setNetworkCertificateSubjectDN(certificate.getSubjectX500Principal().getName());

			// if the certificate has been signed by a CA, then include the entire .p12 in the response (Base 64 encoded)
			if ( certificate.getIssuerX500Principal()
					.equals(certificate.getSubjectX500Principal()) == false ) {
				details.setNetworkCertificate(Base64.encodeBase64String(cert.getKeystoreData()));
			}
		}

		details.setNetworkId(EdgeId);
		details.setConfirmationKey(confirmationKey);
		details.setNetworkCertificateStatus(cert.getStatus().getValue());
		return details;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public UserEdgeCertificateRenewal renewEdgeCertificate(final UserEdge userEdge,
			final String keystorePassword) {
		if ( userEdge == null ) {
			throw new IllegalArgumentException("UserEdge must be provided.");
		}
		if ( keystorePassword == null ) {
			throw new IllegalArgumentException("Keystore password must be provided.");
		}

		final Long EdgeId = userEdge.getId();
		if ( EdgeId == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		final UserEdgeCertificate cert = userEdgeCertificateDao
				.get(new UserEdgePK(userEdge.getUser().getId(), EdgeId));
		return renewEdgeCertificate(cert, keystorePassword);
	}

	private UserEdgeCertificateRenewal renewEdgeCertificate(final UserEdgeCertificate cert,
			final String keystorePassword) {
		if ( cert == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		final User user = cert.getUser();
		if ( user == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		final Long EdgeId = cert.getEdgeId();
		if ( EdgeId == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}

		final KeyStore keystore;
		try {
			keystore = cert.getKeyStore(keystorePassword);
		} catch ( CertificateException e ) {
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, null);
		}

		final X509Certificate certificate = cert.getEdgeCertificate(keystore);
		if ( certificate == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}

		if ( EdgeCertificateRenewalPeriod != null ) {
			if ( new DateTime(certificate.getNotAfter()).minus(EdgeCertificateRenewalPeriod)
					.isAfterNow() ) {
				throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, null);
			}
		}

		String renewRequestID = EdgePKIBiz.submitRenewalRequest(certificate);
		if ( renewRequestID == null ) {
			log.error("No renew request ID returned for {}", certificate.getSubjectDN());
			throw new CertificateException("No CSR request ID returned");
		}

		// update the UserEdgeCert's request ID to the renewal ID
		cert.setRequestId(renewRequestID);
		cert.setStatus(UserEdgeCertificateStatus.a);

		final String certSubjectDN = String.format(networkCertificateSubjectDNFormat, EdgeId.toString());

		final Future<UserEdgeCertificate> approval = approveCSR(certSubjectDN, keystorePassword, user,
				cert);
		Instruction installInstruction = null;
		try {
			UserEdgeCertificate renewedCert = approval.get(approveCSRMaximumWaitSecs, TimeUnit.SECONDS);
			cert.setStatus(renewedCert.getStatus());
			cert.setCreated(renewedCert.getCreated());
			cert.setKeystoreData(renewedCert.getKeystoreData());
			installInstruction = queueRenewedEdgeCertificateInstruction(renewedCert, keystorePassword);
		} catch ( TimeoutException e ) {
			log.debug("Timeout waiting for {} cert renewal approval", certSubjectDN);
			// save to DB when we do get our reply
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					try {
						UserEdgeCertificate renewedCert = approval.get();
						cert.setStatus(renewedCert.getStatus());
						cert.setCreated(renewedCert.getCreated());
						cert.setKeystoreData(renewedCert.getKeystoreData());
						userEdgeCertificateDao.store(cert);
						queueRenewedEdgeCertificateInstruction(renewedCert, keystorePassword);
					} catch ( Exception e ) {
						log.error("Error approving cert {}", certSubjectDN, e);
					}
				}
			});
		} catch ( InterruptedException e ) {
			log.debug("Interrupted waiting for {} cert renewal approval", certSubjectDN);
			// just continue
		} catch ( ExecutionException e ) {
			log.error("CSR {} approval threw an exception: {}", certSubjectDN, e.getMessage());
			throw new CertificateException("Error approving cert renewal", e);
		}

		// update the request ID to the instruction ID, if available; then we can query for 
		// the instruction later
		if ( installInstruction != null && installInstruction.getId() != null ) {
			cert.setRequestId(installInstruction.getId().toString());
		}

		userEdgeCertificateDao.store(cert);

		BasicUserEdgeCertificateRenewal details = new BasicUserEdgeCertificateRenewal();
		details.setNetworkId(EdgeId);
		if ( cert != null ) {
			details.setNetworkCertificateStatus(cert.getStatus().getValue());
			if ( cert.getStatus() == UserEdgeCertificateStatus.v ) {
				details.setNetworkCertificate(getCertificateAsString(cert.getKeystoreData()));
			}
		}
		details.setNetworkCertificateSubjectDN(certSubjectDN);

		// provide the instruction ID as the confirmation ID
		if ( installInstruction != null && installInstruction.getId() != null ) {
			details.setConfirmationKey(installInstruction.getId().toString());
		}

		return details;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public UserEdgeCertificateRenewal getPendingEdgeCertificateRenewal(UserEdge userEdge,
			String confirmationKey) {
		Long instructionId;
		try {
			instructionId = Long.valueOf(confirmationKey);
		} catch ( RuntimeException e ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT,
					confirmationKey);
		}
		EdgeInstruction instruction = instructorBiz.getInstruction(instructionId);
		if ( instruction == null ) {
			return null;
		}

		// verify the Edge ID matches
		Long EdgeId = userEdge.getId();
		if ( EdgeId == null && userEdge.getEdge() != null ) {
			EdgeId = userEdge.getEdge().getId();
		}
		if ( !instruction.getEdgeId().equals(EdgeId) ) {
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED,
					confirmationKey);
		}

		BasicUserEdgeCertificateRenewal details = new BasicUserEdgeCertificateRenewal();
		details.setNetworkId(userEdge.getId());
		details.setConfirmationKey(instructionId.toString());

		UserEdgeCertificateInstallationStatus installStatus;
		switch (instruction.getState()) {
			case Queued:
				installStatus = UserEdgeCertificateInstallationStatus.RequestQueued;
				break;

			case Received:
			case Executing:
				installStatus = UserEdgeCertificateInstallationStatus.RequestReceived;
				break;

			case Completed:
				installStatus = UserEdgeCertificateInstallationStatus.Installed;
				break;

			case Declined:
				installStatus = UserEdgeCertificateInstallationStatus.Declined;
				break;

			default:
				installStatus = null;
		}

		details.setInstallationStatus(installStatus);

		if ( instruction.getParameters() != null ) {
			StringBuilder buf = new StringBuilder();
			for ( InstructionParameter param : instruction.getParameters() ) {
				if ( INSTRUCTION_PARAM_CERTIFICATE.equals(param.getName()) ) {
					buf.append(param.getValue());
				}
			}
			if ( buf.length() > 0 ) {
				details.setNetworkCertificate(buf.toString());
			}
		}

		return details;
	}

	/**
	 * Create a new Edge instruction with the renewed Edge certificate. Only the
	 * certificate will be queued, not the private key.
	 * 
	 * @param cert
	 *        The renewed certificate the Edge should download.
	 * @param keystorePassword
	 *        The password to read the keystore with.
	 */
	private Instruction queueRenewedEdgeCertificateInstruction(final UserEdgeCertificate cert,
			final String keystorePassword) {
		final InstructorBiz instructor = instructorBiz;
		final CertificateService certService = certificateService;
		if ( instructor == null || certService == null ) {
			log.debug(
					"Either InstructorBiz or CertificateService are null, cannot queue cert renewal instruction.");
			return null;
		}
		if ( keystorePassword == null ) {
			throw new IllegalArgumentException("Keystore password must be provided.");
		}
		final KeyStore keystore;
		try {
			keystore = cert.getKeyStore(keystorePassword);
		} catch ( CertificateException e ) {
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, null);
		}

		final X509Certificate certificate = cert.getEdgeCertificate(keystore);
		if ( certificate == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		String pem = certService
				.generatePKCS7CertificateChainString(new X509Certificate[] { certificate });
		Instruction instr = new Instruction(INSTRUCTION_TOPIC_RENEW_CERTIFICATE, new DateTime());
		final int max = instructionParamMaxLength;
		int i = 0, len = pem.length();
		while ( i < len ) {
			int end = i + (i + max < len ? max : (len - i));
			String val = pem.substring(i, end);
			instr.addParameter(INSTRUCTION_PARAM_CERTIFICATE, val);
			i += max;
		}
		return instructor.queueInstruction(cert.getEdgeId(), instr);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public NetworkCertificate renewEdgeCertificate(final InputStream pkcs12InputStream,
			final String keystorePassword) throws IOException {
		if ( pkcs12InputStream == null ) {
			throw new IllegalArgumentException("Keystore must be provided.");
		}
		if ( keystorePassword == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}

		SecurityEdge actor = SecurityUtils.getCurrentEdge();
		final Long EdgeId = actor.getEdgeId();
		if ( EdgeId == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}

		final UserEdge userEdge = userEdgeDao.get(EdgeId);
		if ( userEdge == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}

		// get existing UserEdgeCertificate, else create a new one
		final UserEdgePK userEdgePK = new UserEdgePK(userEdge.getUser().getId(), EdgeId);
		UserEdgeCertificate userEdgeCert = userEdgeCertificateDao.get(userEdgePK);
		if ( userEdgeCert == null ) {
			userEdgeCert = new UserEdgeCertificate();
			userEdgeCert.setId(userEdgePK);
			userEdgeCert.setUser(userEdge.getUser());
			userEdgeCert.setEdge(userEdge.getEdge());
			userEdgeCert.setCreated(new DateTime());
			userEdgeCert.setStatus(UserEdgeCertificateStatus.a);
		}

		// extract the existing Edge certificate
		userEdgeCert.setKeystoreData(FileCopyUtils.copyToByteArray(pkcs12InputStream));

		return renewEdgeCertificate(userEdgeCert, keystorePassword);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public NetworkCertificate confirmEdgeAssociation(NetworkAssociation association)
			throws AuthorizationException {
		if ( association == null ) {
			throw new IllegalArgumentException("NetworkAssociation must be provided.");
		}
		final String username = association.getUsername();
		final String confirmationKey = association.getConfirmationKey();
		if ( username == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		if ( confirmationKey == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		final User user = userDao.getUserByEmail(username);
		if ( user == null ) {
			throw new AuthorizationException(Reason.UNKNOWN_EMAIL, null);
		}

		UserEdgeConfirmation conf = userEdgeConfirmationDao.getConfirmationForKey(user.getId(),
				confirmationKey);
		if ( conf == null ) {
			log.info("Association failed: confirmation not found for username {} key {}", username,
					confirmationKey);
			throw new AuthorizationException(username,
					AuthorizationException.Reason.REGISTRATION_NOT_CONFIRMED);
		}

		// security check: user must be the same that invited Edge
		if ( !user.equals(conf.getUser()) ) {
			log.info("Association failed: confirmation user {} != confirming user {}",
					conf.getUser().getId(), user.getId());
			throw new AuthorizationException(username,
					AuthorizationException.Reason.REGISTRATION_NOT_CONFIRMED);
		}

		// security check: must not be expired
		DateTime expiry = conf.getCreated().plus(invitationExpirationPeriod);
		if ( expiry.isBeforeNow() ) {
			log.info("Association failed: confirmation expired on {}", expiry);
			throw new AuthorizationException(username,
					AuthorizationException.Reason.REGISTRATION_NOT_CONFIRMED);
		}

		// security check: already confirmed?
		if ( conf.getConfirmationDate() != null ) {
			log.info("Association failed: confirmation already confirmed on {}",
					conf.getConfirmationDate());
			throw new AuthorizationException(username,
					AuthorizationException.Reason.REGISTRATION_ALREADY_CONFIRMED);
		}

		// find or create EniwareLocation now, for country + time zone
		EniwareLocation loc = eniwareLocationDao.getEniwareLocationForTimeZone(conf.getCountry(),
				conf.getTimeZoneId());
		if ( loc == null ) {
			// create location now
			loc = new EniwareLocation();
			loc.setName(conf.getCountry() + " - " + conf.getTimeZoneId());
			loc.setCountry(conf.getCountry());
			loc.setTimeZoneId(conf.getTimeZoneId());
			loc = eniwareLocationDao.get(eniwareLocationDao.store(loc));
		}
		assert loc != null;

		// create EniwareEdge now, and allow using a pre-populated Edge ID, and possibly pre-existing
		final Long EdgeId = (conf.getEdgeId() == null ? eniwareEdgeDao.getUnusedEdgeId()
				: conf.getEdgeId());
		EniwareEdge Edge = eniwareEdgeDao.get(EdgeId);
		if ( Edge == null ) {
			Edge = new EniwareEdge();
			Edge.setId(EdgeId);
			Edge.setLocation(loc);
			eniwareEdgeDao.store(Edge);
		}

		// create UserEdge now if it doesn't already exist
		UserEdge userEdge = userEdgeDao.get(EdgeId);
		if ( userEdge == null ) {
			userEdge = new UserEdge();
			userEdge.setEdge(Edge);
			userEdge.setUser(user);
			userEdgeDao.store(userEdge);
		}

		conf.setConfirmationDate(new DateTime());
		conf.setEdgeId(EdgeId);
		userEdgeConfirmationDao.store(conf);

		final String certSubjectDN = String.format(networkCertificateSubjectDNFormat, EdgeId.toString());

		UserEdgeCertificate cert = null;
		if ( association.getKeystorePassword() != null ) {
			// we must become the User now for CSR to be generated
			SecurityUtils.becomeUser(user.getEmail(), user.getName(), user.getId());

			// we'll generate a key and CSR for the user, encrypting with the provided password
			cert = generateEdgeCSR(association, certSubjectDN);
			if ( cert.getRequestId() == null ) {
				log.error("No CSR request ID returned for {}", certSubjectDN);
				throw new CertificateException("No CSR request ID returned");
			}

			cert.setCreated(conf.getConfirmationDate());
			cert.setEdgeId(Edge.getId());
			cert.setUserId(user.getId());

			final Future<UserEdgeCertificate> approval = approveCSR(certSubjectDN,
					association.getKeystorePassword(), user, cert);
			try {
				cert = approval.get(approveCSRMaximumWaitSecs, TimeUnit.SECONDS);
			} catch ( TimeoutException e ) {
				log.debug("Timeout waiting for {} CSR approval", certSubjectDN);
				// save to DB when we do get our reply
				executorService.submit(new Runnable() {

					@Override
					public void run() {
						try {
							UserEdgeCertificate approvedCert = approval.get();
							userEdgeCertificateDao.store(approvedCert);
						} catch ( Exception e ) {
							log.error("Error approving cert {}", certSubjectDN, e);
						}
					}
				});
			} catch ( InterruptedException e ) {
				log.debug("Interrupted waiting for {} CSR approval", certSubjectDN);
				// just continue
			} catch ( ExecutionException e ) {
				log.error("CSR {} approval threw an exception: {}", certSubjectDN, e.getMessage());
				throw new CertificateException("Error approving CSR", e);
			}

			userEdgeCertificateDao.store(cert);
		}

		NetworkAssociationDetails details = new NetworkAssociationDetails();
		details.setNetworkId(EdgeId);
		details.setConfirmationKey(
				calculateEdgeAssociationConfirmationCode(conf.getConfirmationDate(), EdgeId));
		if ( cert != null ) {
			details.setNetworkCertificateStatus(cert.getStatus().getValue());
			if ( cert.getStatus() == UserEdgeCertificateStatus.v ) {
				details.setNetworkCertificate(getCertificateAsString(cert.getKeystoreData()));
			}
		}
		details.setNetworkCertificateSubjectDN(certSubjectDN);
		return details;
	}

	private String getCertificateAsString(byte[] data) {
		return Base64.encodeBase64String(data);
	}

	private Future<UserEdgeCertificate> approveCSR(final String certSubjectDN,
			final String keystorePassword, final User user, final UserEdgeCertificate cert) {
		return executorService.submit(new Callable<UserEdgeCertificate>() {

			@Override
			public UserEdgeCertificate call() throws Exception {
				SecurityUtils.becomeUser(user.getEmail(), user.getName(), user.getId());
				log.debug("Approving CSR {} request ID {}", certSubjectDN, cert.getRequestId());
				X509Certificate[] chain = EdgePKIBiz.approveCSR(cert.getRequestId());
				saveEdgeSignedCertificate(keystorePassword, cert, chain);
				return cert;
			}
		});
	}

	private void saveEdgeSignedCertificate(final String keystorePassword, UserEdgeCertificate cert,
			X509Certificate[] chain) throws CertificateException {
		log.debug("Saving approved certificate {}",
				(chain != null && chain.length > 0 ? chain[0].getSubjectDN().getName() : null));
		KeyStore keyStore = cert.getKeyStore(keystorePassword);
		Key key;
		try {
			key = keyStore.getKey(UserEdgeCertificate.KEYSTORE_Edge_ALIAS,
					keystorePassword.toCharArray());
		} catch ( GeneralSecurityException e ) {
			throw new CertificateException("Error opening Edge private key", e);
		}
		X509Certificate EdgeCert = cert.getEdgeCertificate(keyStore);
		if ( EdgeCert == null ) {
			throw new CertificateException(
					"UserEdgeCertificate " + cert.getId() + " does not have a private key.");
		}

		log.info("Installing Edge certificate reply {} issued by {}",
				(chain != null && chain.length > 0 ? chain[0].getSubjectDN().getName() : null),
				(chain != null && chain.length > 0 ? chain[0].getIssuerDN().getName() : null));
		try {
			keyStore.setKeyEntry(UserEdgeCertificate.KEYSTORE_Edge_ALIAS, key,
					keystorePassword.toCharArray(), chain);
		} catch ( KeyStoreException e ) {
			throw new CertificateException("Error opening Edge certificate", e);
		}

		ByteArrayOutputStream byos = new ByteArrayOutputStream();
		storeKeyStore(keyStore, keystorePassword, byos);
		cert.setKeystoreData(byos.toByteArray());
		cert.setStatus(UserEdgeCertificateStatus.v);
	}

	private UserEdgeCertificate generateEdgeCSR(NetworkAssociation association,
			final String certSubjectDN) {
		log.debug("Generating private key and CSR for {}", certSubjectDN);
		try {
			KeyStore keystore = loadKeyStore(association.getKeystorePassword(), null);

			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(EdgePrivateKeySize, new SecureRandom());
			KeyPair keypair = keyGen.generateKeyPair();
			X509Certificate certificate = EdgePKIBiz.generateCertificate(certSubjectDN,
					keypair.getPublic(), keypair.getPrivate());
			keystore.setKeyEntry(EdgeKeystoreAlias, keypair.getPrivate(),
					association.getKeystorePassword().toCharArray(), new Certificate[] { certificate });

			log.debug("Submitting CSR {} to CA", certSubjectDN);

			String csrID = EdgePKIBiz.submitCSR(certificate, keypair.getPrivate());

			log.debug("Submitted CSR {} to CA, got request ID {}", certSubjectDN, csrID);

			ByteArrayOutputStream byos = new ByteArrayOutputStream();
			storeKeyStore(keystore, association.getKeystorePassword(), byos);

			UserEdgeCertificate cert = new UserEdgeCertificate();
			cert.setStatus(UserEdgeCertificateStatus.a);
			cert.setRequestId(csrID);
			cert.setKeystoreData(byos.toByteArray());
			return cert;
		} catch ( GeneralSecurityException e ) {
			log.error("Error creating Edge CSR {}: {}", certSubjectDN, e.getMessage());
			throw new CertificateException("Unable to create Edge CSR " + certSubjectDN, e);
		}
	}

	private KeyStore loadKeyStore(String password, InputStream in) {
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance("pkcs12");
			keyStore.load(in, password.toCharArray());
			return keyStore;
		} catch ( KeyStoreException e ) {
			throw new CertificateException("Error loading certificate key store", e);
		} catch ( NoSuchAlgorithmException e ) {
			throw new CertificateException("Error loading certificate key store", e);
		} catch ( java.security.cert.CertificateException e ) {
			throw new CertificateException("Error loading certificate key store", e);
		} catch ( IOException e ) {
			String msg;
			if ( e.getCause() instanceof UnrecoverableKeyException ) {
				msg = "Invalid password loading key store";
			} else {
				msg = "Error loading certificate key store";
			}
			throw new CertificateException(msg, e);
		} finally {
			if ( in != null ) {
				try {
					in.close();
				} catch ( IOException e ) {
					// ignore this one
				}
			}
		}
	}

	private void storeKeyStore(KeyStore keystore, String password, OutputStream out) {
		final char[] pass = (password == null ? new char[0] : password.toCharArray());
		try {
			keystore.store(out, pass);
		} catch ( IOException e ) {
			throw new CertificateException("Unable to serialize keystore", e);
		} catch ( GeneralSecurityException e ) {
			throw new CertificateException("Unable to serialize keystore", e);
		} finally {
			try {
				out.flush();
				out.close();
			} catch ( IOException e ) {
				// ignore this one
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public User updateUser(User userEntry) {
		assert userEntry != null;
		assert userEntry.getId() != null;

		User entity = userDao.get(userEntry.getId());
		if ( entity == null ) {
			throw new AuthorizationException(userEntry.getEmail(), Reason.UNKNOWN_EMAIL);
		}

		if ( StringUtils.hasText(userEntry.getEmail()) ) {
			entity.setEmail(userEntry.getEmail());
		}
		if ( StringUtils.hasText(userEntry.getName()) ) {
			entity.setName(userEntry.getName());
		}
		if ( StringUtils.hasText(userEntry.getPassword())
				&& !DO_NOT_CHANGE_VALUE.equals(userEntry.getPassword()) ) {
			entity.setPassword(userEntry.getPassword());
		}

		prepareUserForStorage(entity);

		try {
			entity = userDao.get(userDao.store(entity));
		} catch ( DataIntegrityViolationException e ) {
			if ( log.isWarnEnabled() ) {
				log.warn("Duplicate user registration: " + entity.getEmail());
			}
			throw new AuthorizationException(entity.getEmail(),
					AuthorizationException.Reason.DUPLICATE_EMAIL);
		}
		return entity;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public RegistrationReceipt generateResetPasswordReceipt(final String email)
			throws AuthorizationException {
		if ( emailThrottleCache != null && emailThrottleCache.isKeyInCache(email) ) {
			log.debug("Email {} in throttle cache; not generating reset password receipt", email);
			throw new AuthorizationException(email, Reason.ACCESS_DENIED);
		}
		final User entity = userDao.getUserByEmail(email);
		if ( entity == null ) {
			throw new AuthorizationException(email, Reason.UNKNOWN_EMAIL);
		}

		if ( emailThrottleCache != null ) {
			emailThrottleCache.put(new Element(email, Boolean.TRUE));
		}

		final String conf = calculateResetPasswordConfirmationCode(entity, null);
		return new BasicRegistrationReceipt(email, conf);
	}

	private String calculateResetPasswordConfirmationCode(User entity, String salt) {
		StringBuilder buf = new StringBuilder();
		if ( salt == null ) {
			// generate 8-byte salt
			final SecureRandom random = new SecureRandom();
			final int start = 97;
			final int end = 122;
			final int range = end - start;
			for ( int i = 0; i < 8; i++ ) {
				buf.append((char) (random.nextInt(range) + start));
			}
			salt = buf.toString();
		} else {
			buf.append(salt);
		}

		// use data from the existing user to create the confirmation hash
		buf.append(entity.getId()).append(entity.getCreated().getMillis()).append(entity.getPassword());

		return salt + DigestUtils.sha256Hex(buf.toString());
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void resetPassword(RegistrationReceipt receipt, PasswordEntry password) {
		if ( receipt == null || receipt.getUsername() == null || receipt.getConfirmationCode() == null
				|| receipt.getConfirmationCode().length() != 72 || password.getPassword() == null
				|| !password.getPassword().equals(password.getPasswordConfirm()) ) {
			throw new AuthorizationException(receipt.getUsername(),
					Reason.FORGOTTEN_PASSWORD_NOT_CONFIRMED);
		}

		final User entity = userDao.getUserByEmail(receipt.getUsername());
		if ( entity == null ) {
			throw new AuthorizationException(receipt.getUsername(), Reason.UNKNOWN_EMAIL);
		}

		final String salt = receipt.getConfirmationCode().substring(0, 8);
		final String expectedCode = calculateResetPasswordConfirmationCode(entity, salt);
		if ( !expectedCode.equals(receipt.getConfirmationCode()) ) {
			throw new AuthorizationException(receipt.getUsername(),
					Reason.FORGOTTEN_PASSWORD_NOT_CONFIRMED);
		}

		// ok, the conf code matches, let's reset the password
		final String encryptedPass = passwordEncoder.encode(password.getPassword());
		entity.setPassword(encryptedPass);
		userDao.store(entity);
	}

	public Set<String> getConfirmedUserRoles() {
		return confirmedUserRoles;
	}

	public void setConfirmedUserRoles(Set<String> confirmedUserRoles) {
		this.confirmedUserRoles = confirmedUserRoles;
	}

	public Period getInvitationExpirationPeriod() {
		return invitationExpirationPeriod;
	}

	public void setInvitationExpirationPeriod(Period invitationExpirationPeriod) {
		this.invitationExpirationPeriod = invitationExpirationPeriod;
	}

	public String getDefaultEniwareLocationName() {
		return defaultEniwareLocationName;
	}

	public void setDefaultEniwareLocationName(String defaultEniwareLocationName) {
		this.defaultEniwareLocationName = defaultEniwareLocationName;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setUserEdgeDao(UserEdgeDao userEdgeDao) {
		this.userEdgeDao = userEdgeDao;
	}

	public void setUserEdgeConfirmationDao(UserEdgeConfirmationDao userEdgeConfirmationDao) {
		this.userEdgeConfirmationDao = userEdgeConfirmationDao;
	}

	public void setUserValidator(Validator userValidator) {
		this.userValidator = userValidator;
	}

	public void setEniwareEdgeDao(EniwareEdgeDao eniwareEdgeDao) {
		this.eniwareEdgeDao = eniwareEdgeDao;
	}

	public void setEniwareLocationDao(EniwareLocationDao eniwareLocationDao) {
		this.eniwareLocationDao = eniwareLocationDao;
	}

	public void setNetworkIdentityBiz(NetworkIdentityBiz networkIdentityBiz) {
		this.networkIdentityBiz = networkIdentityBiz;
	}

	public void setUserEdgeCertificateDao(UserEdgeCertificateDao userEdgeCertificateDao) {
		this.userEdgeCertificateDao = userEdgeCertificateDao;
	}

	public void setNetworkCertificateSubjectDNFormat(String networkCertificateSubjectDNFormat) {
		this.networkCertificateSubjectDNFormat = networkCertificateSubjectDNFormat;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setXmlSerializer(JavaBeanXmlSerializer xmlSerializer) {
		this.xmlSerializer = xmlSerializer;
	}

	public void setEmailThrottleCache(Ehcache emailThrottleCache) {
		this.emailThrottleCache = emailThrottleCache;
	}

	public void setEdgePKIBiz(EdgePKIBiz EdgePKIBiz) {
		this.EdgePKIBiz = EdgePKIBiz;
	}

	public void setEdgePrivateKeySize(int EdgePrivateKeySize) {
		this.EdgePrivateKeySize = EdgePrivateKeySize;
	}

	public void setEdgeKeystoreAlias(String EdgeKeystoreAlias) {
		this.EdgeKeystoreAlias = EdgeKeystoreAlias;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void setApproveCSRMaximumWaitSecs(int approveCSRMaximumWaitSecs) {
		this.approveCSRMaximumWaitSecs = approveCSRMaximumWaitSecs;
	}

	@Override
	public Period getEdgeCertificateRenewalPeriod() {
		return EdgeCertificateRenewalPeriod;
	}

	public void setEdgeCertificateRenewalPeriod(Period EdgeCertificateRenewalPeriod) {
		this.EdgeCertificateRenewalPeriod = EdgeCertificateRenewalPeriod;
	}

	/**
	 * Configure the Edge certificate renewal period as a number of months.
	 * 
	 * This is a convenience method that simply calls
	 * {@link #setEdgeCertificateRenewalPeriod(Period)} with an appropriate
	 * {@code Period} for the provided months, or {@code null} if {@code months}
	 * is less than {@code 1}.
	 * 
	 * @param months
	 *        The number of months to set the renewal period to, or {@code 0} to
	 *        not enforce any limit.
	 * @since 1.8
	 */
	public void setEdgeCertificateRenewalPeriodMonths(int months) {
		setEdgeCertificateRenewalPeriod(months > 0 ? new Period(0, months, 0, 0, 0, 0, 0, 0) : null);
	}

	/**
	 * Set the InstructorBiz to use for queuing instructions.
	 * 
	 * @param instructorBiz
	 *        The service to use.
	 * @since 1.8
	 */
	public void setInstructorBiz(InstructorBiz instructorBiz) {
		this.instructorBiz = instructorBiz;
	}

	/**
	 * Set the {@link CertificateService} to use.
	 * 
	 * @param certificateService
	 *        The service to use.
	 * @since 1.8
	 */
	public void setCertificateService(CertificateService certificateService) {
		this.certificateService = certificateService;
	}

	/**
	 * The maximum length to use for instruction parameter values.
	 * 
	 * @param instructionParamMaxLength
	 *        The maximum length.
	 * 
	 * @since 1.8
	 */
	public void setInstructionParamMaxLength(int instructionParamMaxLength) {
		this.instructionParamMaxLength = instructionParamMaxLength;
	}

}
