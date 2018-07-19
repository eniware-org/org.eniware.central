/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web;

import static org.eniware.web.domain.Response.response;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletResponse;

import org.eniware.central.RepeatableTaskException;
import org.eniware.central.user.biz.EdgeOwnershipBiz;
import org.eniware.central.user.biz.RegistrationBiz;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.domain.NewEdgeRequest;
import org.eniware.central.user.domain.User;
import org.eniware.central.user.domain.UserAlertStatus;
import org.eniware.central.user.domain.UserAlertType;
import org.eniware.central.user.domain.UserEdge;
import org.eniware.central.user.domain.UserEdgeCertificate;
import org.eniware.central.user.domain.UserEdgeCertificateInstallationStatus;
import org.eniware.central.user.domain.UserEdgeCertificateRenewal;
import org.eniware.central.user.domain.UserEdgeConfirmation;
import org.eniware.central.user.domain.UserEdgeTransfer;
import org.eniware.support.CertificateException;
import org.eniware.support.CertificateService;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import org.eniware.central.mail.MailService;
import org.eniware.central.mail.support.BasicMailAddress;
import org.eniware.central.mail.support.ClasspathResourceMessageTemplateDataSource;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityUser;
import org.eniware.central.security.SecurityUtils;
import org.eniware.domain.NetworkAssociation;
import org.eniware.domain.NetworkCertificate;
import org.eniware.web.domain.Response;

/**
 * Controller for "my Edges".
 *
 * @version 1.4
 */
@Controller
@RequestMapping("/sec/my-Edges")
public class MyEdgesController extends ControllerSupport {

	private final UserBiz userBiz;
	private final RegistrationBiz registrationBiz;
	private final EdgeOwnershipBiz EdgeOwnershipBiz;
	private final CertificateService certificateService;

	@Autowired(required = false)
	private MailService mailService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Constructor.
	 * 
	 * @param userBiz
	 *        The {@link UserBiz} to use.
	 * @param registrationBiz
	 *        The {@link RegistrationBiz} to use.
	 * @param EdgeOwnershipBiz
	 *        the {@link EdgeOwnershipBiz} to use.
	 * @param certificateService
	 *        The {@link CertificateService} to use.
	 */
	@Autowired
	public MyEdgesController(UserBiz userBiz, RegistrationBiz registrationBiz,
			EdgeOwnershipBiz EdgeOwnershipBiz, CertificateService certificateService) {
		super();
		this.userBiz = userBiz;
		this.registrationBiz = registrationBiz;
		this.certificateService = certificateService;
		this.EdgeOwnershipBiz = EdgeOwnershipBiz;
	}

	/**
	 * Set a {@link MailService} to use.
	 * 
	 * @param mailService
	 *        The service to use.
	 */
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	/**
	 * The {@link MessageSource} to use in conjunction with
	 * {@link #setMailService(MailService)}.
	 * 
	 * @param messageSource
	 *        A message source to use.
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ModelAttribute("EdgeDataAlertTypes")
	public List<UserAlertType> EdgeDataAlertTypes() {
		// now now, only one alert type!
		return Collections.singletonList(UserAlertType.EdgeStaleData);
	}

	@ModelAttribute("alertStatuses")
	public UserAlertStatus[] alertStatuses() {
		return UserAlertStatus.values();
	}

	/**
	 * View a "home" page for the "my Edges" section.
	 * 
	 * @return model and view
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView viewMyEdges() {
		final SecurityUser actor = SecurityUtils.getCurrentUser();
		List<UserEdge> edges = userBiz.getUserEdges(SecurityUtils.getCurrentUser().getUserId());

		// move any Edges with pending transfer into own list
		List<UserEdge> pendingTransferEdges = new ArrayList<UserEdge>(Node == null ? 0 : edges.size());
		if ( Nodes != null ) {
			for ( Iterator<UserEdge> itr = Nodes.iterator(); itr.hasNext(); ) {
				UserEdge Edge = itr.next();
				if ( Edge.getTransfer() != null ) {
					itr.remove();
					pendingTransferEdges.add(node);
				}
			}
		}

		List<UserEdgeConfirmation> pendingConfirmationList = userBiz
				.getPendingUserEdgeConfirmations(actor.getUserId());
		List<UserEdgeTransfer> pendingEdgeOwnershipRequests = EdgeOwnershipBiz
				.pendingEdgeOwnershipTransfersForEmail(actor.getEmail());
		ModelAndView mv = new ModelAndView("my-Edges/my-Edges");
		mv.addObject("userEdgesList", Nodes);
		mv.addObject("pendingUserEdgeConfirmationsList", pendingConfirmationList);
		mv.addObject("pendingUserEdgeTransferList", pendingTransferEdges);
		mv.addObject("pendingEdgeOwnershipRequests", pendingEdgeOwnershipRequests);
		return mv;
	}

	/**
	 * Get a list of all archived Edges.
	 * 
	 * @return All archived Edges.
	 * @since 1.4
	 */
	@RequestMapping(value = "/archived", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<UserEdge>> getArchivedEdges() {
		final SecurityUser actor = SecurityUtils.getCurrentUser();
		List<UserEdge> Edges = userBiz.getArchivedUserEdges(actor.getUserId());
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
	 * @since 1.4
	 */
	@RequestMapping(value = "/archived", method = RequestMethod.POST)
	@ResponseBody
	public Response<Object> updateArchivedStatus(@RequestParam("EdgeIds") Long[] EdgeIds,
			@RequestParam("archived") boolean archived) {
		final SecurityUser actor = SecurityUtils.getCurrentUser();
		userBiz.updateUserEdgeArchivedStatus(actor.getUserId(), EdgeIds, archived);
		return Response.response(null);
	}

	/**
	 * Generate a new Edge confirmation code.
	 * 
	 * @param userId
	 *        the optional user ID to generate the code for; defaults to the
	 *        acting user
	 * @param securityPhrase
	 *        a security phrase to associate with the invitation
	 * @param timeZoneName
	 *        the time zone to associate the Edge with
	 * @param country
	 *        the country to associate the Edge with
	 * @return model and view
	 */
	@RequestMapping("/new")
	public ModelAndView newEdgeAssociation(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam("phrase") String securityPhrase, @RequestParam("timeZone") String timeZoneName,
			@RequestParam("country") String countryCode) {
		if ( userId == null ) {
			userId = SecurityUtils.getCurrentUser().getUserId();
		}
		final TimeZone timeZone = TimeZone.getTimeZone(timeZoneName);
		String lang = "en";
		for ( Locale locale : Locale.getAvailableLocales() ) {
			if ( locale.getCountry().equals(countryCode) ) {
				lang = locale.getLanguage();
			}
		}
		final Locale locale = new Locale(lang, countryCode);
		final NetworkAssociation details = registrationBiz
				.createEdgeAssociation(new NewEdgeRequest(userId, securityPhrase, timeZone, locale));
		return new ModelAndView("my-Edges/invitation", "details", details);
	}

	@RequestMapping("/tzpicker.html")
	public String tzpicker() {
		return "tzpicker-500";
	}

	@RequestMapping("/invitation")
	public ModelAndView viewConfirmation(@RequestParam(value = "id") Long userEdgeConfirmationId) {
		NetworkAssociation details = registrationBiz.getEdgeAssociation(userEdgeConfirmationId);
		return new ModelAndView("my-Edges/invitation", "details", details);
	}

	@RequestMapping("/cancelInvitation")
	public String cancelConfirmation(@RequestParam(value = "id") Long userEdgeConfirmationId) {
		registrationBiz.cancelEdgeAssociation(userEdgeConfirmationId);
		return "redirect:/u/sec/my-Edges";
	}

	/**
	 * Get a certificate, either as a {@link UserEdgeCertificate} object or the
	 * PEM encoded value file attachment.
	 * 
	 * @param certId
	 *        the ID of the certificate to get
	 * @param download
	 *        if TRUE, then download the certificate as a PEM file
	 * @return the response data
	 */
	@RequestMapping(value = "/cert/{EdgeId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> viewCert(@PathVariable("EdgeId") Long EdgeId) {
		SecurityUser actor = SecurityUtils.getCurrentUser();
		UserEdgeCertificate cert = userBiz.getUserEdgeCertificate(actor.getUserId(), EdgeId);
		if ( cert == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, EdgeId);
		}

		final byte[] data = cert.getKeystoreData();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(data.length);
		headers.setContentType(MediaType.parseMediaType("application/x-pkcs12"));
		headers.setLastModified(System.currentTimeMillis());
		headers.setCacheControl("no-cache");

		headers.set("Content-Disposition",
				"attachment; filename=eniwareedge-" + cert.getEdge().getId() + ".p12");

		return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
	}

	/**
	 * Get a certificate as a {@link UserEdgeCertificate} object or the PEM
	 * encoded value file attachment.
	 * 
	 * @param certId
	 *        the ID of the certificate to get
	 * @param password
	 *        the password to decrypt the certificate store with
	 * @return the response data
	 * @since 1.3
	 */
	@RequestMapping(value = "/cert/{EdgeId}", method = RequestMethod.POST)
	@ResponseBody
	public UserEdgeCertificate viewCert(@PathVariable("EdgeId") Long EdgeId,
			@RequestParam(value = "password") String password) {
		SecurityUser actor = SecurityUtils.getCurrentUser();
		UserEdgeCertificate cert = userBiz.getUserEdgeCertificate(actor.getUserId(), EdgeId);
		if ( cert == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, EdgeId);
		}

		final byte[] data = cert.getKeystoreData();

		// see if a renewal is pending
		UserEdgeCertificateInstallationStatus installationStatus = null;
		if ( cert.getRequestId() != null ) {
			UserEdge userEdge = new UserEdge(cert.getUser(), cert.getEdge());
			UserEdgeCertificateRenewal renewal = registrationBiz
					.getPendingEdgeCertificateRenewal(userEdge, cert.getRequestId());
			if ( renewal != null ) {
				installationStatus = renewal.getInstallationStatus();
			}
		}

		String pkcs7 = "";
		X509Certificate EdgeCert = null;
		if ( data != null ) {
			KeyStore keystore = cert.getKeyStore(password);
			X509Certificate[] chain = cert.getEdgeCertificateChain(keystore);
			if ( chain != null && chain.length > 0 ) {
				EdgeCert = chain[0];
			}
			pkcs7 = certificateService.generatePKCS7CertificateChainString(chain);
		}
		return new UserEdgeCertificateDecoded(cert, installationStatus, EdgeCert, pkcs7,
				registrationBiz.getEdgeCertificateRenewalPeriod());
	}

	/**
	 * AuthorizationException handler.
	 * 
	 * <p>
	 * Logs a WARN log and returns HTTP 403 (Forbidden).
	 * </p>
	 * 
	 * @param e
	 *        the exception
	 * @param res
	 *        the servlet response
	 */
	@ExceptionHandler(CertificateException.class)
	public void handleCertificateException(CertificateException e, HttpServletResponse res) {
		if ( log.isWarnEnabled() ) {
			log.warn("Certificate exception: " + e.getMessage());
		}
		res.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	@RequestMapping(value = "/cert/renew/{EdgeId}", method = RequestMethod.POST)
	@ResponseBody
	public UserEdgeCertificate renewCert(@PathVariable("EdgeId") final Long EdgeId,
			@RequestParam("password") final String password) {
		SecurityUser actor = SecurityUtils.getCurrentUser();
		UserEdge userEdge = userBiz.getUserEdge(actor.getUserId(), EdgeId);
		if ( userEdge == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, EdgeId);
		}
		NetworkCertificate renewed = registrationBiz.renewEdgeCertificate(userEdge, password);
		if ( renewed != null && renewed.getNetworkCertificate() != null ) {
			return viewCert(EdgeId, password);
		}
		throw new RepeatableTaskException("Certificate renewal processing");
	}

	public static class UserEdgeCertificateDecoded extends UserEdgeCertificate {

		private static final long serialVersionUID = -2314002517991208690L;

		private final UserEdgeCertificateInstallationStatus installationStatus;
		private final String pemValue;
		private final X509Certificate EdgeCert;
		private final DateTime renewAfter;

		private UserEdgeCertificateDecoded(UserEdgeCertificate cert,
				UserEdgeCertificateInstallationStatus installationStatus, X509Certificate EdgeCert,
				String pkcs7, ReadablePeriod renewPeriod) {
			super();
			setCreated(cert.getCreated());
			setId(cert.getId());
			setEdgeId(cert.getEdgeId());
			setRequestId(cert.getRequestId());
			setUserId(cert.getUserId());
			this.installationStatus = installationStatus;
			this.pemValue = pkcs7;
			this.EdgeCert = EdgeCert;
			if ( EdgeCert != null ) {
				if ( renewPeriod != null ) {
					this.renewAfter = new DateTime(EdgeCert.getNotAfter()).minus(renewPeriod);
				} else {
					this.renewAfter = null;
				}
			} else {
				this.renewAfter = null;
			}
		}

		public String getPemValue() {
			return pemValue;
		}

		/**
		 * Get a hexidecimal string value of the certificate serial number.
		 * 
		 * @return The certificate serial number.
		 */
		public String getCertificateSerialNumber() {
			return (EdgeCert != null ? "0x" + EdgeCert.getSerialNumber().toString(16) : null);
		}

		/**
		 * Get the date the certificate is valid from.
		 * 
		 * @return The valid from date.
		 */
		public DateTime getCertificateValidFromDate() {
			return (EdgeCert != null ? new DateTime(EdgeCert.getNotBefore()) : null);
		}

		/**
		 * Get the date the certificate is valid until.
		 * 
		 * @return The valid until date.
		 */
		public DateTime getCertificateValidUntilDate() {
			return (EdgeCert != null ? new DateTime(EdgeCert.getNotAfter()) : null);
		}

		/**
		 * Get the certificate subject DN.
		 * 
		 * @return The certificate subject DN.
		 */
		public String getCertificateSubjectDN() {
			return (EdgeCert != null ? EdgeCert.getSubjectDN().getName() : null);
		}

		/**
		 * Get the certificate issuer DN.
		 * 
		 * @return The certificate issuer DN.
		 */
		public String getCertificateIssuerDN() {
			return (EdgeCert != null ? EdgeCert.getIssuerDN().getName() : null);
		}

		/**
		 * Get a date after which the certificate may be renewed.
		 * 
		 * @return A renewal minimum date.
		 */
		public DateTime getCertificateRenewAfterDate() {
			return renewAfter;
		}

		/**
		 * Get the status of the installation process, if available.
		 * 
		 * @return The installation status, or <em>null</em>.
		 */
		public UserEdgeCertificateInstallationStatus getInstallationStatus() {
			return installationStatus;
		}

	}

	@RequestMapping(value = "/editEdge", method = RequestMethod.GET)
	public String editEdgeView(@RequestParam("userId") Long userId, @RequestParam("EdgeId") Long EdgeId,
			Model model) {
		model.addAttribute("userEdge", userBiz.getUserEdge(userId, EdgeId));
		return "my-Edges/edit-Edge";
	}

	@ResponseBody
	@RequestMapping(value = "/Edge", method = RequestMethod.GET)
	public Response<UserEdge> getUserEdge(@RequestParam("userId") Long userId,
			@RequestParam("EdgeId") Long EdgeId) {
		return response(userBiz.getUserEdge(userId, EdgeId));
	}

	@ResponseBody
	@RequestMapping(value = "/updateEdge", method = RequestMethod.POST)
	public UserEdge editEdgeSave(UserEdge userEdge, Errors userEdgeErrors, Model model) {
		return userBiz.saveUserEdge(userEdge);
	}

	/**
	 * Request an ownership transfer of a Edge to another EniwareNetwork account.
	 * 
	 * @param userId
	 *        The user ID of the current Edge owner.
	 * @param EdgeId
	 *        The ID of the Edge to transfer ownership of.
	 * @param email
	 *        The recipient of the Edge ownership request.
	 * @param locale
	 *        The request locale to use in the generated email content.
	 * @param uriBuilder
	 *        A URI builder to assist in the generated email content.
	 * @return A {@code TRUE} value on success.
	 */
	@ResponseBody
	@RequestMapping(value = "/requestEdgeTransfer", method = RequestMethod.POST)
	public Response<Boolean> requestEdgeOwnershipTransfer(@RequestParam("userId") Long userId,
			@RequestParam("EdgeId") Long EdgeId, @RequestParam("recipient") String email, Locale locale,
			UriComponentsBuilder uriBuilder) {
		EdgeOwnershipBiz.requestEdgeOwnershipTransfer(userId, EdgeId, email);
		if ( mailService != null ) {
			try {
				User actor = userBiz.getUser(SecurityUtils.getCurrentActorUserId());

				uriBuilder.pathSegment("sec", "my-Edges");

				Map<String, Object> mailModel = new HashMap<String, Object>(2);
				mailModel.put("actor", actor);
				mailModel.put("recipient", email);
				mailModel.put("EdgeId", EdgeId);
				mailModel.put("url", uriBuilder.build().toUriString());

				mailService.sendMail(new BasicMailAddress(null, email),
						new ClasspathResourceMessageTemplateDataSource(locale,
								messageSource.getMessage("my-Edges.transferOwnership.mail.subject", null,
										locale),
								"/net/eniwarenetwork/central/reg/web/transfer-ownership.txt", mailModel));
			} catch ( RuntimeException e ) {
				// ignore this other than log
				log.warn("Error sending ownership transfer mail message to {}: {}", email,
						e.getMessage(), e);
			}
		}
		return response(Boolean.TRUE);
	}

	@ResponseBody
	@RequestMapping(value = "/cancelEdgeTransferRequest", method = RequestMethod.POST)
	public Response<Boolean> cancelEdgeOwnershipTransfer(@RequestParam("userId") Long userId,
			@RequestParam("EdgeId") Long EdgeId, Locale locale) {
		UserEdgeTransfer xfer = EdgeOwnershipBiz.getEdgeOwnershipTransfer(userId, EdgeId);
		if ( xfer != null ) {
			EdgeOwnershipBiz.cancelEdgeOwnershipTransfer(userId, EdgeId);
			if ( mailService != null ) {
				// notify the recipient about the cancellation
				try {
					User actor = userBiz.getUser(SecurityUtils.getCurrentActorUserId());

					Map<String, Object> mailModel = new HashMap<String, Object>(2);
					mailModel.put("actor", actor);
					mailModel.put("transfer", xfer);

					mailService.sendMail(new BasicMailAddress(null, xfer.getEmail()),
							new ClasspathResourceMessageTemplateDataSource(locale,
									messageSource.getMessage(
											"my-Edges.transferOwnership.mail.subject.cancelled", null,
											locale),
									"/net/eniwarenetwork/central/reg/web/transfer-ownership-cancelled.txt",
									mailModel));
				} catch ( RuntimeException e ) {
					// ignore this other than log
					log.warn("Error sending ownership transfer mail message to {}: {}", xfer.getEmail(),
							e.getMessage(), e);
				}
			}
		}
		return response(Boolean.TRUE);
	}

	@ResponseBody
	@RequestMapping(value = "/confirmEdgeTransferRequest", method = RequestMethod.POST)
	public Response<Boolean> confirmEdgeOwnershipTransfer(@RequestParam("userId") Long userId,
			@RequestParam("EdgeId") Long EdgeId, @RequestParam("accept") boolean accept, Locale locale) {
		UserEdgeTransfer xfer = EdgeOwnershipBiz.confirmEdgeOwnershipTransfer(userId, EdgeId, accept);
		if ( xfer != null ) {
			if ( mailService != null ) {
				// notify the recipient about the cancellation
				try {
					User actor = userBiz.getUser(SecurityUtils.getCurrentActorUserId());

					Map<String, Object> mailModel = new HashMap<String, Object>(2);
					mailModel.put("actor", actor);
					mailModel.put("transfer", xfer);

					mailService
							.sendMail(new BasicMailAddress(null, xfer.getUser().getEmail()),
									new ClasspathResourceMessageTemplateDataSource(locale,
											messageSource.getMessage(
													("my-Edges.transferOwnership.mail.subject."
															+ (accept ? "accepted" : "declined")),
													null, locale),
											("/net/eniwarenetwork/central/reg/web/transfer-ownership-"
													+ (accept ? "accepted" : "declined") + ".txt"),
											mailModel));
				} catch ( RuntimeException e ) {
					// ignore this other than log
					log.warn("Error sending ownership transfer mail message to {}: {}", xfer.getEmail(),
							e.getMessage(), e);
				}
			}
		}
		return response(Boolean.TRUE);
	}

}
