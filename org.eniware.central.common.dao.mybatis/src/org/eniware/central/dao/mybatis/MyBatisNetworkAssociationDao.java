/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.eniware.central.dao.NetworkAssociationDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisDao;
import org.eniware.domain.NetworkAssociation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link NetworkAssociationDao}.
 * @version 1.0
 */
public class MyBatisNetworkAssociationDao extends BaseMyBatisDao implements NetworkAssociationDao {

	private final String queryForConfirmationCode = "get-NetworkAssociation-for-code";

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public NetworkAssociation getNetworkAssociationForConfirmationKey(String username,
			String confirmationCode) {
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put("key", confirmationCode);
		params.put("username", username);
		return selectFirst(queryForConfirmationCode, params);
	}

}
