/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.eniware.central.user.biz.RegistrationBiz;
import org.eniware.central.user.domain.PasswordEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;
import org.eniware.central.mail.MailService;
import org.eniware.central.mail.support.BasicMailAddress;
import org.eniware.central.mail.support.ClasspathResourceMessageTemplateDataSource;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityUtils;
import org.eniware.domain.BasicRegistrationReceipt;
import org.eniware.domain.RegistrationReceipt;

/**
 * Controller for managing the reset password functionality.
 * 
 * @version 1.1
 */
@Controller
@RequestMapping("/resetPassword")
public class ResetPasswordController extends ControllerSupport {

	@Autowired
	private RegistrationBiz registrationBiz;

	@Autowired
	private MailService mailService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AuthenticationManager authenticationManager;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String home() {
		return "resetpass/start";
	}

	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	public ModelAndView generateResetCode(@RequestParam("email") String email, Locale locale,
			UriComponentsBuilder uriBuilder) {
		RegistrationReceipt receipt;
		try {
			receipt = registrationBiz.generateResetPasswordReceipt(email);

			// set up the confirmation URL
			uriBuilder.pathSegment("resetPassword", "confirm");
			uriBuilder.replaceQuery(null);
			uriBuilder.queryParam("c", receipt.getConfirmationCode());
			uriBuilder.queryParam("m", email);

			Map<String, Object> mailModel = new HashMap<String, Object>(2);
			mailModel.put("receipt", receipt);
			mailModel.put("url", uriBuilder.build().toUriString());

			mailService.sendMail(new BasicMailAddress(null, receipt.getUsername()),
					new ClasspathResourceMessageTemplateDataSource(locale,
							messageSource.getMessage("user.resetpassword.mail.subject", null, locale),
							"/net/eniwarenetwork/central/reg/web/reset-password.txt", mailModel));
		} catch ( AuthorizationException e ) {
			// don't want to let anyone know about failures here... just pretend nothing happened
			log.info("Ignoring password reset request for {}: {}", email, e.getReason());
			receipt = new BasicRegistrationReceipt(email, "");
		}

		return new ModelAndView("resetpass/generated", "receipt", receipt);
	}

	@RequestMapping(value = "confirm", method = RequestMethod.GET)
	public ModelAndView confirmResetPassword(@RequestParam("c") String confirmationCode,
			@RequestParam("m") String email) {
		PasswordEntry form = new PasswordEntry();
		form.setConfirmationCode(confirmationCode);
		form.setUsername(email);
		return new ModelAndView("resetpass/confirm", "form", form);
	}

	@RequestMapping(value = "reset", method = RequestMethod.POST)
	public ModelAndView resetPassword(PasswordEntry form, HttpServletRequest req) {
		try {
			registrationBiz.resetPassword(
					new BasicRegistrationReceipt(form.getUsername(), form.getConfirmationCode()), form);
		} catch ( AuthorizationException e ) {
			// go back to confirm
			ModelAndView result = new ModelAndView("resetpass/confirm", "form", form);
			result.addObject(WebConstants.MODEL_KEY_ERROR_MSG, "user.resetpassword.confirm.error");
			return result;
		}

		// automatically log the user in now, and then redirect to home
		SecurityUtils.authenticate(authenticationManager, form.getUsername(), form.getPassword());
		req.getSession().setAttribute(WebConstants.MODEL_KEY_STATUS_MSG,
				"user.resetpassword.reset.message");
		return new ModelAndView("redirect:/u/sec/home");
	}

	public void setRegistrationBiz(RegistrationBiz registrationBiz) {
		this.registrationBiz = registrationBiz;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

}
