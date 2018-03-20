/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import java.util.Map;

/**
 * Password encoder that delegates to a configurable list of Spring Security
 * {@code org.springframework.security.crypto.password.PasswordEncoder}
 * instances, returning passwords with a prefix tag to be able to recognize what
 * encryption technique was used.
 * 
 * <p>
 * The configurable properties of this class are:
 * </p>
 * 
 * <dl class="class-properties">
 * <dt>encoders</dt>
 * <dd>An ordered Map of password prefix tag keys to associated
 * <code>PasswordEncoder</code> instances. The first entry in the map according
 * to iteration order will be used as the primary encoder. Thus a map
 * implementation like {@link java.util.LinkedHashMap} is recommended.</dd>
 * </dl>
 * 
 * @author matt
 * @version 1.0
 */
public class DelegatingPasswordEncoder implements PasswordEncoder,
		org.springframework.security.crypto.password.PasswordEncoder {

	private Map<String, org.springframework.security.crypto.password.PasswordEncoder> encoders;

	@Override
	public boolean isPasswordEncrypted(CharSequence password) {
		if ( encoders == null || password == null ) {
			return false;
		}
		for ( String prefix : encoders.keySet() ) {
			if ( password.length() > prefix.length()
					&& password.subSequence(0, prefix.length()).equals(prefix) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String encode(CharSequence rawPassword) {
		if ( encoders == null || encoders.size() < 1 ) {
			throw new RuntimeException("No password encoders configured");
		}
		Map.Entry<String, org.springframework.security.crypto.password.PasswordEncoder> entry = encoders
				.entrySet().iterator().next();
		return entry.getValue().encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if ( encodedPassword == null || rawPassword == null ) {
			return false;
		}
		for ( Map.Entry<String, org.springframework.security.crypto.password.PasswordEncoder> entry : encoders
				.entrySet() ) {
			String prefixTag = entry.getKey();
			if ( encodedPassword.startsWith(prefixTag) ) {
				return entry.getValue().matches(rawPassword, encodedPassword);
			}
		}
		return false;
	}

	public void setEncoders(
			Map<String, org.springframework.security.crypto.password.PasswordEncoder> encoders) {
		this.encoders = encoders;
	}

}
