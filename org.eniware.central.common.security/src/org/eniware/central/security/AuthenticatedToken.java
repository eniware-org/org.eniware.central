/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * {@link SecurityUser} implementation for authenticated tokens.
 
 * @version 1.1
 */
public class AuthenticatedToken extends User implements SecurityToken {

	private static final long serialVersionUID = -4857188995583662187L;

	private final String tokenType;
	private final Long userId;
	private final SecurityPolicy policy;

	/**
	 * Construct with values.
	 * 
	 * @param tokenType
	 *        the token type
	 * @param userId
	 *        the user ID (that the token belongs to)
	 * @param policy
	 *        optional policy associated with the token
	 */
	public AuthenticatedToken(UserDetails user, String tokenType, Long userId, SecurityPolicy policy) {
		super(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(),
				user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
		this.tokenType = tokenType;
		this.userId = userId;
		this.policy = policy;
	}

	@Override
	public boolean isAuthenticatedWithToken() {
		return true;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	@Override
	public String getToken() {
		return getUsername();
	}

	@Override
	public String getTokenType() {
		return tokenType;
	}

	@Override
	public SecurityPolicy getPolicy() {
		return policy;
	}

}
