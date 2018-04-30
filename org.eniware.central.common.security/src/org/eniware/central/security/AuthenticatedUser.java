/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import org.springframework.security.core.userdetails.User;

/**
 * Extension of Spring Security's {@link User} object to add EniwareNetwork
 * attributes.
 
 * @version 1.1
 */
public class AuthenticatedUser extends User implements SecurityUser {

	private static final long serialVersionUID = 4517031455367343502L;

	private final Long userId;
	private final String name;
	private final boolean authenticatedWithToken;

	/**
	 * Construct from existing {@link User} and
	 * {@link org.eniware.central.user.domain.User} objects.
	 * 
	 * @param user
	 *        the user
	 * @param domainUser
	 *        the domain User
	 * @param authenticatedWithToken
	 *        the authenticated with token flag
	 */
	public AuthenticatedUser(User user, Long userId, String name, boolean authenticatedWithToken) {
		super(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user
				.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
		this.userId = userId;
		this.name = name;
		this.authenticatedWithToken = authenticatedWithToken;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	@Override
	public String getEmail() {
		return getUsername();
	}

	@Override
	public boolean isAuthenticatedWithToken() {
		return authenticatedWithToken;
	}

}
