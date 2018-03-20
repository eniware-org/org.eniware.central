/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.web.support;

import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;

import org.eniware.central.ValidationException;
import org.eniware.central.security.AuthorizationException;
import org.eniware.central.security.SecurityActor;
import org.eniware.central.security.SecurityException;
import org.eniware.central.security.SecurityNode;
import org.eniware.central.security.SecurityToken;
import org.eniware.central.security.SecurityUser;
import org.eniware.central.security.SecurityUtils;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.domain.UserAuthTokenType;
import org.eniware.central.user.domain.UserNode;
import org.eniware.web.domain.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.core.JsonParseException;

/**
 * A base class to support web service style controllers.
 * 
 * @author matt
 * @version 1.7
 */
public abstract class WebServiceControllerSupport {

	/** The default value for the {@code requestDateFormat} property. */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	/** The default value for the {@code requestDateFormat} property. */
	public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm";

	/** A class-level logger. */
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSource messageSource;

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
	 * Handle a {@link org.eniware.central.security.SecurityException}.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 * @since 1.2
	 */
	@ExceptionHandler(org.eniware.central.security.SecurityException.class)
	@ResponseBody
	public Response<?> handleSecurityException(org.eniware.central.security.SecurityException e,
			HttpServletResponse response) {
		log.debug("SecurityException in {} controller: {}", getClass().getSimpleName(), e.getMessage());
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		return new Response<Object>(Boolean.FALSE, null, e.getMessage(), null);
	}

	/**
	 * Handle an {@link TypeMismatchException}.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 * @since 1.4
	 */
	@ExceptionHandler(TypeMismatchException.class)
	@ResponseBody
	public Response<?> handleTypeMismatchException(TypeMismatchException e,
			HttpServletResponse response) {
		log.error("TypeMismatchException in {} controller", getClass().getSimpleName(), e);
		return new Response<Object>(Boolean.FALSE, null, "Illegal argument: " + e.getMessage(), null);
	}

	/**
	 * Handle an {@link IllegalArgumentException}.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 * @since 1.3
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	public Response<?> handleIllegalArgumentException(IllegalArgumentException e,
			HttpServletResponse response) {
		log.error("IllegalArgumentException in {} controller", getClass().getSimpleName(), e);
		return new Response<Object>(Boolean.FALSE, null, "Illegal argument: " + e.getMessage(), null);
	}

	/**
	 * Handle a {@link JsonParseException}, presuming from malformed JSON input.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 * @since 1.6
	 */
	@ExceptionHandler(JsonParseException.class)
	@ResponseBody
	public Response<?> handleJsonParseException(JsonParseException e, HttpServletResponse response) {
		log.error("JsonParseException in {} controller", getClass().getSimpleName(), e);
		return new Response<Object>(Boolean.FALSE, null, "Malformed JSON: " + e.getMessage(), null);
	}

	/**
	 * Handle a {@link HttpMessageNotReadableException}, from malformed JSON
	 * input.
	 * 
	 * @param e
	 *        the exception
	 * @param response
	 *        the response
	 * @return an error response object
	 * @since 1.6
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public Response<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
			HttpServletResponse response) {
		Throwable t = e.getMostSpecificCause();
		if ( t instanceof JsonParseException ) {
			return handleJsonParseException((JsonParseException) t, response);
		}
		log.error("HttpMessageNotReadableException in {} controller", getClass().getSimpleName(), e);
		return new Response<Object>(Boolean.FALSE, null, "Malformed JSON: " + e.getMessage(), null);
	}

	/**
	 * Handle a {@link RuntimeException} not handled by other exception
	 * handlers.
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
		// NOTE: in Spring 4.3 the root exception will be unwrapped; support Spring 4.2 here
		Throwable cause = e;
		while ( cause.getCause() != null ) {
			cause = cause.getCause();
		}
		if ( cause instanceof IllegalArgumentException ) {
			return handleIllegalArgumentException((IllegalArgumentException) cause, response);
		}
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
	public Response<?> handleBindException(BindException e, HttpServletResponse response,
			Locale locale) {
		log.debug("BindException in {} controller", getClass().getSimpleName(), e);
		response.setStatus(422);
		String msg = generateErrorsMessage(e, locale, messageSource);
		return new Response<Object>(Boolean.FALSE, null, msg, null);
	}

	private String generateErrorsMessage(Errors e, Locale locale, MessageSource msgSrc) {
		String msg = (msgSrc == null ? "Validation error"
				: msgSrc.getMessage("error.validation", null, "Validation error", locale));
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

	/**
	 * Get all node IDs the current actor is authorized to access.
	 * 
	 * @param userBiz
	 *        The UserBiz to use to fill in all available nodes for user-based
	 *        actors, or {@code null} to to fill in nodes.
	 * @return The allowed node IDs.
	 * @throws AuthorizationException
	 *         if no node IDs are allowed or there is no actor
	 * @since 1.5
	 */
	protected Long[] authorizedNodeIdsForCurrentActor(UserBiz userBiz) {
		final SecurityActor actor;
		try {
			actor = SecurityUtils.getCurrentActor();
		} catch ( SecurityException e ) {
			log.warn("Access DENIED to node {} for non-authenticated user");
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, null);
		}

		if ( actor instanceof SecurityNode ) {
			SecurityNode node = (SecurityNode) actor;
			return new Long[] { node.getNodeId() };
		} else if ( actor instanceof SecurityUser ) {
			SecurityUser user = (SecurityUser) actor;
			// default to all nodes for actor
			List<UserNode> nodes = userBiz.getUserNodes(user.getUserId());
			if ( nodes != null && !nodes.isEmpty() ) {
				Long[] result = new Long[nodes.size()];
				for ( ListIterator<UserNode> itr = nodes.listIterator(); itr.hasNext(); ) {
					result[itr.nextIndex()] = itr.next().getId();
				}
				return result;
			}
		} else if ( actor instanceof SecurityToken ) {
			SecurityToken token = (SecurityToken) actor;
			Long[] result = null;
			if ( UserAuthTokenType.User.toString().equals(token.getTokenType()) ) {
				// default to all nodes for actor
				List<UserNode> nodes = userBiz.getUserNodes(token.getUserId());
				if ( nodes != null && !nodes.isEmpty() ) {
					result = new Long[nodes.size()];
					for ( ListIterator<UserNode> itr = nodes.listIterator(); itr.hasNext(); ) {
						result[itr.nextIndex()] = itr.next().getId();
					}
				}
			} else if ( UserAuthTokenType.ReadNodeData.toString().equals(token.getTokenType()) ) {
				// all node IDs in token
				if ( token.getPolicy() != null && token.getPolicy().getNodeIds() != null ) {
					Set<Long> nodeIds = token.getPolicy().getNodeIds();
					result = nodeIds.toArray(new Long[nodeIds.size()]);
				}
			}
			if ( result != null && result.length > 0 ) {
				return result;
			}
		}
		throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, null);
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
