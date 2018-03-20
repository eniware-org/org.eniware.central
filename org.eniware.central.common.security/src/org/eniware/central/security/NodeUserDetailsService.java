/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Implementation of {@link UserDetailsService} for X.509 authenticated nodes.
 * 
 * @author matt
 * @version $Revision$
 */
public class NodeUserDetailsService implements UserDetailsService {

	/** The default authorities to grant. */
	protected static final Collection<GrantedAuthority> AUTHORITIES = getAuthorities();

	private static Collection<GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);
		authorities.add(new SimpleGrantedAuthority("ROLE_NODE"));
		return Collections.unmodifiableCollection(authorities);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException,
			DataAccessException {
		Long id = Long.valueOf(username);
		return new AuthenticatedNode(id, AUTHORITIES, false);
	}

}