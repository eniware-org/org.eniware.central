/* ==================================================================
 * Eniware Open Source:Nikolai Manchev
 * Apache License 2.0
 * ==================================================================
 */

package org.eniware.central;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

/**
 * Exception for validation errors.
 * @version 1.1
 */
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = -40848031815846620L;

	private final Errors errors;
	private final MessageSource messageSource;

	/**
	 * Default constructor.
	 */
	public ValidationException() {
		this(null);
	}

	/**
	 * Constructor with Errors.
	 * 
	 * @param errors
	 */
	public ValidationException(Errors errors) {
		this(errors, null);
	}

	/**
	 * Constructor with Errors and a MessageSource.
	 * 
	 * @param errors
	 *        the errors
	 * @param messageSource
	 *        the message source to use to resolve the Errors against
	 */
	public ValidationException(Errors errors, MessageSource messageSource) {
		this(errors, messageSource, null);
	}

	/**
	 * Constructor with Errors and a MessageSource and root cause.
	 * 
	 * @param errors
	 *        the errors
	 * @param messageSource
	 *        the message source to use to resolve the Errors against
	 * @param cause
	 *        the causing exception
	 */
	public ValidationException(Errors errors, MessageSource messageSource, Throwable cause) {
		super(cause);
		this.errors = errors;
		this.messageSource = messageSource;
	}

	public Errors getErrors() {
		return errors;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

}
