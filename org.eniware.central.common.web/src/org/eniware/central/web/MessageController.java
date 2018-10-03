/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.web;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eniware.web.support.MessagesSource;
import org.eniware.web.support.WebUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for returning i18n message resources.
 
 * @version $Revision$
 */
@Controller
@RequestMapping("/msg")
public class MessageController {
	
	@Autowired private MessageSource messageSource;

	private String viewName;

	/**
	 * Get a single message.
	 * 
	 * @param request the request
	 * @param model the view model
	 * @param locale the locale
	 * @param msgKey the message key
	 * @param params optional parameters for the message
	 * @return the view name
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/msg.*")
	public String getMessage(HttpServletRequest request, Model model, Locale locale,
			@RequestParam("key") String msgKey, 
			@RequestParam(required = false, value = "param") String[] params) {
		String value = messageSource.getMessage(msgKey, params, locale);
		model.addAttribute("message", value);
		return WebUtils.resolveViewFromUrlExtension(request, getViewName());
	}
	
	/**
	 * Get all messages.
	 * 
	 * @param request the request
	 * @param model the view model
	 * @param locale the locale
	 * @return the view name
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/msgs.*")
	public String getAllMessages(HttpServletRequest request, Model model, Locale locale) {
		if ( !(messageSource instanceof MessagesSource) ) {
			throw new RuntimeException("MessageSource does not implement MessagesSource.");
		}
		MessagesSource ms = (MessagesSource)messageSource;
		Enumeration<String> enumeration = ms.getKeys(locale);
		Map<String, Object> messages = new LinkedHashMap<String, Object>();
		while (enumeration.hasMoreElements()) {
			String msgKey = enumeration.nextElement();
			Object val = ms.getMessage(msgKey, null, locale);
			if (val != null) {
				messages.put(msgKey, val);
			}
		}
		model.addAttribute("messages", messages);
		return WebUtils.resolveViewFromUrlExtension(request, getViewName());

	}

	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
}
