/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.user.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.user.dao.UserAuthTokenDao;
import org.eniware.central.user.domain.UserAuthToken;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.eniware.web.security.AuthorizationV2Builder;

/**
 * MyBatis implementation of {@link UserAuthTokenDao}.
 *
 * @version 1.2
 */
public class MyBatisUserAuthTokenDao extends BaseMyBatisGenericDao<UserAuthToken, String>
		implements UserAuthTokenDao {

	/** The query name used for {@link #findUserAuthTokensForUser(Long)}. */
	public static final String QUERY_FOR_USER_ID = "find-UserAuthToken-for-UserID";

	/**
	 * The query name used for
	 * {@link #createAuthorizationV2Builder(String, DateTime)}.
	 */
	public static final String QUERY_FOR_SIGNING_KEY = "get-snws2-signingkey-for-tokenid";

	/**
	 * Default constructor.
	 */
	public MyBatisUserAuthTokenDao() {
		super(UserAuthToken.class, String.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<UserAuthToken> findUserAuthTokensForUser(Long userId) {
		return getSqlSession().selectList(QUERY_FOR_USER_ID, userId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String store(final UserAuthToken datum) {
		final String pk = handleAssignedPrimaryKeyStore(datum);
		return pk;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public AuthorizationV2Builder createAuthorizationV2Builder(String tokenId, DateTime signingDate) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("id", tokenId);
		long date = signingDate.withZone(DateTimeZone.UTC).toLocalDate()
				.toDateTimeAtStartOfDay(DateTimeZone.UTC).getMillis();
		java.sql.Date sqlDate = new java.sql.Date(date);
		params.put("date", sqlDate);
		log.debug("Requesting signing key for token {} with date {}", tokenId, sqlDate);
		Byte[] data = selectFirst(QUERY_FOR_SIGNING_KEY, params);
		if ( data == null || data.length < 1 ) {
			return null;
		}
		byte[] key = new byte[data.length];
		for ( int i = 0, len = data.length; i < len; i++ ) {
			key[i] = data[i].byteValue();
		}
		AuthorizationV2Builder builder = new AuthorizationV2Builder(tokenId).date(signingDate.toDate())
				.signingKey(key);
		return builder;
	}

}
