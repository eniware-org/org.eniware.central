/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.security.jdbc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eniware.central.security.AuthenticatedToken;
import org.eniware.central.security.AuthenticatedUser;
import org.eniware.central.security.BasicSecurityPolicy;
import org.eniware.central.security.SecurityPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Extension of {@link JdbcDaoImpl} that returns {@link AuthenticatedUser}
 * objects.
 * 
 * <p>
 * The SQL required by this implementation adds the following additional column
 * requirements to the base {@link JdbcDaoImpl} requirements:
 * </p>
 * 
 * <ol>
 * <li>User ID (Long) - the User primary key</li>
 * <li>Display Name (String) - the User's display name</li>
 * <li>Token (Boolean) - <em>true</em> if this represents a token</li>
 * <li>Token type (String) - if Token is <em>true</em>, this should be the token
 * type</li>
 * <li>Token IDs (String) - if Token is <em>true</em>, this is an optional set
 * of IDs</li>
 * </ol>

 * @version 1.1
 */
public class JdbcUserDetailsService extends JdbcDaoImpl implements UserDetailsService {

	public static final String DEFAULT_USERS_BY_USERNAME_SQL = "SELECT username, password, enabled, user_id, display_name, FALSE AS is_token"
			+ " FROM eniwareuser.user_login WHERE username = ?";

	public static final String DEFAULT_AUTHORITIES_BY_USERNAME_SQL = "SELECT username, authority FROM eniwareuser.user_login_role WHERE username = ?";

	public static final String DEFAULT_TOKEN_USERS_BY_USERNAME_SQL = "SELECT username, password, enabled, user_id, display_name, TRUE AS is_token, token_type, jpolicy"
			+ " FROM eniwareuser.user_auth_token_login WHERE username = ?";

	public static final String DEFAULT_TOKEN_AUTHORITIES_BY_USERNAME_SQL = "SELECT username, authority FROM eniwareuser.user_auth_token_role WHERE username = ?";

	private List<GrantedAuthority> staticAuthorities;
	private final ObjectMapper objectMapper;

	private final Logger log = LoggerFactory.getLogger(getClass());

	public JdbcUserDetailsService() {
		this(new ObjectMapper());
	}

	public JdbcUserDetailsService(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
		setUsersByUsernameQuery(DEFAULT_USERS_BY_USERNAME_SQL);
		setAuthoritiesByUsernameQuery(DEFAULT_AUTHORITIES_BY_USERNAME_SQL);
	}

	@Override
	protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
			List<GrantedAuthority> combinedAuthorities) {
		User user = (User) super.createUserDetails(username, userFromUserQuery, combinedAuthorities);
		if ( userFromUserQuery instanceof AuthenticatedToken ) {
			AuthenticatedToken token = (AuthenticatedToken) userFromUserQuery;
			return new AuthenticatedToken(user, token.getTokenType(), token.getUserId(),
					token.getPolicy());
		}
		AuthenticatedUser authUser = (AuthenticatedUser) userFromUserQuery;
		return new AuthenticatedUser(user, authUser.getUserId(), authUser.getName(),
				authUser.isAuthenticatedWithToken());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	protected List<UserDetails> loadUsersByUsername(String username) {
		return getJdbcTemplate().query(getUsersByUsernameQuery(), new String[] { username },
				new RowMapper<UserDetails>() {

					@Override
					public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
						String username = rs.getString(1);
						String password = rs.getString(2);
						boolean enabled = rs.getBoolean(3);
						Long id = rs.getLong(4);
						String name = rs.getString(5);
						boolean authWithToken = rs.getBoolean(6);
						if ( authWithToken ) {
							String tokenType = rs.getString(7);
							String policyJson = rs.getString(8);
							SecurityPolicy policy = null;
							if ( policyJson != null ) {
								try {
									policy = objectMapper.readValue(policyJson,
											BasicSecurityPolicy.class);
								} catch ( IOException e ) {
									log.warn("Error deserializing SecurityPolicy from [{}]: {}",
											policyJson, e.getMessage());
								}
							}
							return new AuthenticatedToken(new User(username, password, enabled, true,
									true, true, AuthorityUtils.NO_AUTHORITIES), tokenType, id, policy);
						}
						return new AuthenticatedUser(new User(username, password, enabled, true, true,
								true, AuthorityUtils.NO_AUTHORITIES), id, name, false);
					}
				});
	}

	@Override
	protected void addCustomAuthorities(String username, List<GrantedAuthority> authorities) {
		if ( staticAuthorities != null ) {
			authorities.addAll(staticAuthorities);
		}
	}

	/**
	 * Set a list of statically assigned authorites based on a list of role
	 * names.
	 * 
	 * <p>
	 * All users will be automatically granted these authorities.
	 * </p>
	 * 
	 * @param roles
	 *        the role names to grant
	 */
	public void setStaticRoles(List<String> roles) {
		List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>(roles == null ? 0 : roles.size());
		for ( String role : roles ) {
			auths.add(new SimpleGrantedAuthority(role));
		}
		staticAuthorities = auths;
	}

}
