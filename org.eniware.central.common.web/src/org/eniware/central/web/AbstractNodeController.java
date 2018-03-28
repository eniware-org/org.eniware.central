/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.web;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.solarnetwork.web.support.WebUtils;

import org.eniware.central.ValidationException;
import org.eniware.central.dao.SolarNodeDao;
import org.eniware.central.domain.SolarNode;
import org.eniware.central.security.AuthorizationException;
import org.eniware.util.CloningPropertyEditorRegistrar;
import org.eniware.util.JodaDateFormatEditor;
import org.eniware.web.domain.Response;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Abstract base class to support node-related controllers.

 * @version 1.1
 */
public abstract class AbstractNodeController {

	/** The default value for the {@code requestDateFormat} property. */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	/** The default value for the {@code requestDateFormat} property. */
	public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

	/** A class-level logger. */
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSource messageSource;

	private SolarNodeDao solarNodeDao;
	private String viewName;
	private String[] requestDateFormats = new String[] { DEFAULT_DATE_TIME_FORMAT, DEFAULT_DATE_FORMAT };

	/**
	 * Resolve a ModelAndView with an empty model and a view name determined by
	 * the URL "suffix".
	 * 
	 * <p>
	 * If the {@link #getViewName()} method returns a value, that view name is
	 * used for every request. Otherwise, this sets the view name to the value
	 * of the URL "suffix", that is, everything after the last period in the
	 * URL. This uses {@link StringUtils#getFilenameExtension(String)} on the
	 * request URI to accomplish this. For example a URL like
	 * {@code /myController.json} would resolve to a view named {@code json}.
	 * This can be handy when you want to return different data formats for the
	 * same business logic, such as XML or JSON.
	 * </p>
	 * 
	 * @param request
	 *        the HTTP request
	 * @return a ModelAndView (never <em>null</em>)
	 */
	protected ModelAndView resolveViewFromUrlExtension(HttpServletRequest request) {
		String viewName = WebUtils.resolveViewFromUrlExtension(request, getViewName());
		return new ModelAndView(viewName);
	}

	/**
	 * Set up a {@link CloningPropertyEditorRegistrar} as a request attribute.
	 * 
	 * <p>
	 * This sets up a new {@link CloningPropertyEditorRegistrar} as a request
	 * attribute, which could be used by the view for serializing model
	 * properties in some way. A common use for this is to serialize
	 * {@link DateTime} objects into Strings, so this method accepts a
	 * {@code dateFormat} and {@code node} property which, if provided, will add
	 * a {@link JodaDateFormatEditor} to the registrar for all {@link DateTime}
	 * objects, configured with the node's time zone.
	 * </p>
	 * 
	 * @param request
	 *        the HTTP request
	 * @param dateFormat
	 *        an optional date format
	 * @param node
	 *        an optional node (required if {@code dateFormat} provided)
	 * @return the registrar
	 */
	protected CloningPropertyEditorRegistrar setupViewPropertyEditorRegistrar(
			HttpServletRequest request, String dateFormat, SolarNode node) {
		return setupViewPropertyEditorRegistrar(request, dateFormat,
				(node == null ? null : node.getTimeZone()));
	}

	/**
	 * Set up a {@link CloningPropertyEditorRegistrar} as a request attribute.
	 * 
	 * <p>
	 * This sets up a new {@link CloningPropertyEditorRegistrar} as a request
	 * attribute, which could be used by the view for serializing model
	 * properties in some way. A common use for this is to serialize
	 * {@link DateTime} objects into Strings, so this method accepts a
	 * {@code dateFormat} and {@code node} property which, if provided, will add
	 * a {@link JodaDateFormatEditor} to the registrar for all {@link DateTime}
	 * objects, configured with the node's time zone.
	 * </p>
	 * 
	 * @param request
	 *        the HTTP request
	 * @param dateFormat
	 *        an optional date format
	 * @param timeZone
	 *        an optional node (required if {@code dateFormat} provided)
	 * @return the registrar
	 */
	protected CloningPropertyEditorRegistrar setupViewPropertyEditorRegistrar(
			HttpServletRequest request, String dateFormat, TimeZone timeZone) {
		// set up a PropertyEditorRegistrar that can be used for serializing data into view-friendly values
		CloningPropertyEditorRegistrar registrar = new CloningPropertyEditorRegistrar();
		if ( dateFormat != null && timeZone != null ) {
			// TODO implement caching of JodaDateFormatEditors based on dateFormat + time zone
			registrar.setPropertyEditor(DateTime.class, new JodaDateFormatEditor(dateFormat, timeZone));
		}
		request.setAttribute("propertyEditorRegistrar", registrar);
		return registrar;
	}

	/**
	 * Add a {@link DateTime} property editor, using the
	 * {@link #getRequestDateFormat()} pattern.
	 * 
	 * <p>
	 * This is typically called from an "init binder" method.
	 * </p>
	 * 
	 * @param binder
	 *        the binder to add the editor to
	 */
	protected void initBinderDateFormatEditor(WebDataBinder binder) {
		binder.registerCustomEditor(DateTime.class, new JodaDateFormatEditor(this.requestDateFormats,
				null));
	}

	/**
	 * Handle an {@link AuthorizationException}.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 */
	@ExceptionHandler(AuthorizationException.class)
	@ResponseBody
	public Response<?> handleAuthorizationException(AuthorizationException e,
			HttpServletResponse response) {
		log.debug("AuthorizationException in {} controller: {}", getClass().getSimpleName(),
				e.getMessage());
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		return new Response<Object>(Boolean.FALSE, null, e.getReason().toString(), null);
	}

	/**
	 * Handle an {@link RuntimeException}.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 */
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public Response<?> handleRuntimeException(RuntimeException e, HttpServletResponse response) {
		log.error("RuntimeException in {} controller", getClass().getSimpleName(), e);
		return new Response<Object>(Boolean.FALSE, null, "Internal error", null);
	}

	/**
	 * Handle an {@link BindException}.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 */
	@ExceptionHandler(BindException.class)
	@ResponseBody
	public Response<?> handleBindException(BindException e, HttpServletResponse response, Locale locale) {
		log.debug("BindException in {} controller", getClass().getSimpleName(), e);
		response.setStatus(422);
		String msg = generateErrorsMessage(e, locale, messageSource);
		return new Response<Object>(Boolean.FALSE, null, msg, null);
	}

	/**
	 * Handle an {@link ValidationException}.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 */
	@ExceptionHandler(ValidationException.class)
	@ResponseBody
	public Response<?> handleValidationException(ValidationException e, HttpServletResponse response,
			Locale locale) {
		log.debug("ValidationException in {} controller", getClass().getSimpleName(), e);
		response.setStatus(422);
		String msg = generateErrorsMessage(e.getErrors(), locale,
				e.getMessageSource() != null ? e.getMessageSource() : messageSource);
		return new Response<Object>(Boolean.FALSE, null, msg, null);
	}

	private String generateErrorsMessage(Errors e, Locale locale, MessageSource msgSrc) {
		String msg = (msgSrc == null ? "Validation error" : msgSrc.getMessage("error.validation", null,
				"Validation error", locale));
		if ( msgSrc != null && e.hasErrors() ) {
			StringBuilder buf = new StringBuilder();
			for ( ObjectError error : e.getAllErrors() ) {
				if ( buf.length() > 0 ) {
					buf.append(" ");
				}
				buf.append(msgSrc.getMessage(error, locale));
			}
			msg = buf.toString();
		}
		return msg;
	}

	/**
	 * Get the first request date format.
	 * 
	 * @return the requestDateFormat
	 */
	public String getRequestDateFormat() {
		if ( requestDateFormats == null || requestDateFormats.length < 1 ) {
			return null;
		}
		return requestDateFormats[0];
	}

	/**
	 * Set a single request date format.
	 * 
	 * @param requestDateFormat
	 *        the requestDateFormat to set
	 */
	public void setRequestDateFormat(String requestDateFormat) {
		this.requestDateFormats = new String[] { requestDateFormat };
	}

	public SolarNodeDao getSolarNodeDao() {
		return solarNodeDao;
	}

	public void setSolarNodeDao(SolarNodeDao solarNodeDao) {
		this.solarNodeDao = solarNodeDao;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String[] getRequestDateFormats() {
		return requestDateFormats;
	}

	public void setRequestDateFormats(String[] requestDateFormats) {
		this.requestDateFormats = requestDateFormats;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
