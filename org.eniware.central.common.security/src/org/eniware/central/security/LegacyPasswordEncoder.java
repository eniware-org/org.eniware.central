/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Password encoder using unsalted SHA-256 hashes.

 * @version 1.0
 * @deprecated do not use this encoder for anything other than supporting legacy
 *             passwords
 */
@Deprecated
public class LegacyPasswordEncoder implements
		org.springframework.security.crypto.password.PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return (rawPassword == null ? null : "{SHA}" + DigestUtils.sha256Hex(rawPassword.toString()));
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return (rawPassword == null || encodedPassword == null ? false : encode(rawPassword).equals(
				encodedPassword));
	}

}
