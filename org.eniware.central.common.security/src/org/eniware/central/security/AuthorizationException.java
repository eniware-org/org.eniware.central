/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import java.util.Arrays;

/**
 * Exception thrown when authorization to some resource fails.
 * 
 * @author matt
 * @version 1.1
 */
public class AuthorizationException extends SecurityException {

	private static final long serialVersionUID = -7269908721527606492L;

	/** Authorization exception reason. */
	public enum Reason {

		/** Bad password. */
		BAD_PASSWORD,

		/** Unknown email. */
		UNKNOWN_EMAIL,

		/** Duplicate email. */
		DUPLICATE_EMAIL,

		/** Registration not confirmed. */
		REGISTRATION_NOT_CONFIRMED,

		/** Registration already confirmed. */
		REGISTRATION_ALREADY_CONFIRMED,

		/** Forgotten password not confirmed. */
		FORGOTTEN_PASSWORD_NOT_CONFIRMED,

		/** Access denied to something. */
		ACCESS_DENIED,

		/** Access for anonymous users denied. */
		ANONYMOUS_ACCESS_DENIED,

		/** Access was requested to an unknown object. */
		UNKNOWN_OBJECT,
	}

	private final Reason reason;
	private final String email;
	private final Object id;

	/**
	 * Construct authorization exception.
	 * 
	 * @param email
	 *        the attempted login
	 * @param reason
	 *        the reason for the exception
	 */
	public AuthorizationException(String username, Reason reason) {
		super();
		this.reason = reason;
		this.email = username;
		this.id = null;
	}

	/**
	 * Construct authorization exception related to some primary key
	 * 
	 * @param reason
	 *        the reason for the exception
	 * @param id
	 *        the object ID
	 */
	public AuthorizationException(Reason reason, Object id) {
		super();
		this.reason = reason;
		this.email = null;
		this.id = id;
	}

	/**
	 * Get the attempted login.
	 * 
	 * @return login value (or <em>null</em> if not available)
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Get the primary key.
	 * 
	 * @return the primary key (or <em>null</em> if not available)
	 */
	public Object getId() {
		return id;
	}

	/**
	 * Get the authorization exception reason.
	 * 
	 * @return reason
	 */
	public Reason getReason() {
		return reason;
	}

	@Override
	public String getMessage() {
		return (reason == null ? null
				: reason.toString() + " [" + (email == null
						? (id != null && id.getClass().isArray() ? Arrays.toString((Object[]) id) : id)
						: email) + "]");
	}

}
