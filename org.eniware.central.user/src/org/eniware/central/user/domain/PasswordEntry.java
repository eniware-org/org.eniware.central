/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

/**
 * Password reset bean.
 * 
 * @version 1.0
 */
public class PasswordEntry {

	private String username;
	private String confirmationCode;
	private String password;
	private String passwordConfirm;

	/**
	 * Default constructor.
	 */
	public PasswordEntry() {
		super();
	}

	/**
	 * Construct with a password.
	 * 
	 * @param password
	 *        the password to set
	 */
	public PasswordEntry(String password) {
		super();
		setPassword(password);
		setPasswordConfirm(password);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

}
