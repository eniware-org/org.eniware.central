/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of Spring Security's {@link UserDetails} object for
 * authenticated Edges.
 * @version 1.1
 */
public class AuthenticatedEdge implements UserDetails, SecurityEdge {

	private static final long serialVersionUID = -3196310376474763843L;

	private final Long EdgeId;
	private final Collection<GrantedAuthority> authorities;
	private final String username;
	private final String password;
	private final boolean authenticatedWithToken;

	/**
	 * Construct from and a Edge ID.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param auths
	 *        the granted authorities
	 * @param authenticatedWithToken
	 *        the authenticated with token flag
	 */
	public AuthenticatedEdge(Long EdgeId, Collection<GrantedAuthority> auths,
			boolean authenticatedWithToken) {
		this(EdgeId, EdgeId.toString(), "", auths, authenticatedWithToken);
	}

	/**
	 * Construct from a Edge ID, username, and password.
	 * 
	 * @param EdgeId
	 *        the Edge ID
	 * @param username
	 *        the username, e.g. auth token
	 * @param password
	 *        the password, e.g. auth secret
	 * @param auths
	 *        the granted authorities
	 * @param authenticatedWithToken
	 *        the authenticated with token flag
	 */
	public AuthenticatedEdge(Long EdgeId, String username, String password,
			Collection<GrantedAuthority> auths, boolean authenticatedWithToken) {
		super();
		this.username = username;
		this.password = password;
		this.EdgeId = EdgeId;
		this.authorities = auths;
		this.authenticatedWithToken = authenticatedWithToken;
	}

	/**
	 * @return the EdgeId
	 */
	@Override
	public Long getEdgeId() {
		return EdgeId;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isAuthenticatedWithToken() {
		return authenticatedWithToken;
	}

}
