/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import java.util.Map;

import org.eniware.central.dao.PriceSourceDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisFilterableDao;
import org.eniware.central.domain.EntityMatch;
import org.eniware.central.domain.PriceSource;
import org.eniware.central.domain.SourceLocation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link PriceSourceDao}.
 * @version 1.0
 */
public class MyBatisPriceSourceDao extends
		BaseMyBatisFilterableDao<PriceSource, EntityMatch, SourceLocation, Long> implements
		PriceSourceDao {

	/** The query name used for {@link #getPriceSourceForName(String)}. */
	public static final String QUERY_FOR_NAME = "get-PriceSource-for-name";

	/**
	 * Default constructor.
	 */
	public MyBatisPriceSourceDao() {
		super(PriceSource.class, Long.class, EntityMatch.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PriceSource getPriceSourceForName(String sourceName) {
		return selectFirst(QUERY_FOR_NAME, sourceName);
	}

	@Override
	protected void postProcessFilterProperties(SourceLocation filter, Map<String, Object> sqlProps) {
		// add flags to the query processor for dynamic logic
		StringBuilder fts = new StringBuilder();
		spaceAppend(filter.getSource(), fts);
		if ( filter.getLocation() != null ) {
			spaceAppend(filter.getLocation().getName(), fts);
		}
		if ( fts.length() > 0 ) {
			sqlProps.put("fts", fts.toString());
		}
	}

}
