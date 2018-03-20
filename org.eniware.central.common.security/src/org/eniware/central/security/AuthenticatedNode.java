/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of Spring Security's {@link UserDetails} object for
 * authenticated nodes.
 * 
 * @author matt
 * @version 1.1
 */
public class AuthenticatedNode implements UserDetails, SecurityNode {

	private static final long serialVersionUID = -3196310376474763843L;

	private final Long nodeId;
	private final Collection<GrantedAuthority> authorities;
	private final String username;
	private final String password;
	private final boolean authenticatedWithToken;

	/**
	 * Construct from and a node ID.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param auths
	 *        the granted authorities
	 * @param authenticatedWithToken
	 *        the authenticated with token flag
	 */
	public AuthenticatedNode(Long nodeId, Collection<GrantedAuthority> auths,
			boolean authenticatedWithToken) {
		this(nodeId, nodeId.toString(), "", auths, authenticatedWithToken);
	}

	/**
	 * Construct from a node ID, username, and password.
	 * 
	 * @param nodeId
	 *        the node ID
	 * @param username
	 *        the username, e.g. auth token
	 * @param password
	 *        the password, e.g. auth secret
	 * @param auths
	 *        the granted authorities
	 * @param authenticatedWithToken
	 *        the authenticated with token flag
	 */
	public AuthenticatedNode(Long nodeId, String username, String password,
			Collection<GrantedAuthority> auths, boolean authenticatedWithToken) {
		super();
		this.username = username;
		this.password = password;
		this.nodeId = nodeId;
		this.authorities = auths;
		this.authenticatedWithToken = authenticatedWithToken;
	}

	/**
	 * @return the nodeId
	 */
	@Override
	public Long getNodeId() {
		return nodeId;
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
