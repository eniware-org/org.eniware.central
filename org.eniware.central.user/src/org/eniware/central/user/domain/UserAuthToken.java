/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.domain;

import java.util.Set;

import org.eniware.central.domain.BaseStringEntity;
import org.eniware.central.security.BasicSecurityPolicy;
import org.eniware.central.support.JsonUtils;
import org.eniware.util.SerializeIgnore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A user authorization token.
 * 
 * @author matt
 * @version 1.1
 */
public class UserAuthToken extends BaseStringEntity {

	private static final long serialVersionUID = -2125712171325565247L;

	private Long userId;
	private String authSecret;
	private UserAuthTokenStatus status;
	private UserAuthTokenType type;
	private BasicSecurityPolicy policy;
	private String policyJson;

	/**
	 * Default constructor.
	 */
	public UserAuthToken() {
		super();
	}

	/**
	 * Create a new, active token.
	 * 
	 * @param token
	 *        the token value
	 * @param userId
	 *        the user ID
	 * @param secret
	 *        the secret
	 * @param type
	 *        the type
	 */
	public UserAuthToken(String token, Long userId, String secret, UserAuthTokenType type) {
		super();
		setId(token);
		setUserId(userId);
		setAuthSecret(secret);
		setStatus(UserAuthTokenStatus.Active);
		setType(type);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * Get the ID value.
	 * 
	 * This is just an alias for {@link BaseStringEntity#getId()}.
	 * 
	 * @return the auth token
	 */
	@SerializeIgnore
	@JsonIgnore
	public String getAuthToken() {
		return getId();
	}

	/**
	 * Set the ID value.
	 * 
	 * This is just an alias for {@link BaseStringEntity#setId(String)}.
	 * 
	 * @param authToken
	 *        the auth token
	 */
	public void setAuthToken(String authToken) {
		setId(authToken);
	}

	public String getAuthSecret() {
		return authSecret;
	}

	public void setAuthSecret(String authSecret) {
		this.authSecret = authSecret;
	}

	public UserAuthTokenStatus getStatus() {
		return status;
	}

	public void setStatus(UserAuthTokenStatus status) {
		this.status = status;
	}

	public UserAuthTokenType getType() {
		return type;
	}

	public void setType(UserAuthTokenType type) {
		this.type = type;
	}

	/**
	 * Get the node IDs included in the policy, if available.
	 * 
	 * @return node IDs, or {@code null}
	 */
	@SerializeIgnore
	@JsonIgnore
	public Set<Long> getNodeIds() {
		BasicSecurityPolicy p = getPolicy();
		return (p == null ? null : p.getNodeIds());
	}

	/**
	 * Get the {@link BasicSecurityPolicy}.
	 * 
	 * If {@link #setPolicyJson(String)} has been previously called, this will
	 * parse that JSON into a {@code BasicSecurityPolicy} instance and return
	 * that.
	 * 
	 * @return the policy
	 */
	public BasicSecurityPolicy getPolicy() {
		if ( policy == null && policyJson != null ) {
			policy = JsonUtils.getObjectFromJSON(policyJson, BasicSecurityPolicy.class);
		}
		return policy;
	}

	/**
	 * Set the {@link BasicSecurityPolicy} instance to use.
	 * 
	 * This will replace any value set previously via
	 * {@link #setPolicyJson(String)} as well.
	 * 
	 * @param policy
	 *        the policy instance to set
	 */
	public void setPolicy(BasicSecurityPolicy policy) {
		this.policy = policy;
		policyJson = null;
	}

	/**
	 * Get the {@link BasicSecurityPolicy} object as a JSON string.
	 * 
	 * This method will ignore <em>null</em> values.
	 * 
	 * @return a JSON encoded string, or {@code null}
	 */
	@SerializeIgnore
	@JsonIgnore
	public String getPolicyJson() {
		if ( policyJson == null ) {
			policyJson = JsonUtils.getJSONString(policy, null);
		}
		return policyJson;
	}

	/**
	 * Set the {@link BasicSecurityPolicy} object via a JSON string.
	 * 
	 * This method will remove any previously set {@code BasicSecurityPolicy}
	 * and replace it with the values parsed from the JSON.
	 * 
	 * @param json
	 *        The policy JSON to set.
	 */
	@JsonProperty // @JsonProperty needed because of @JsonIgnore on getter
	public void setPolicyJson(String json) {
		policyJson = json;
		policy = null;
	}
}
