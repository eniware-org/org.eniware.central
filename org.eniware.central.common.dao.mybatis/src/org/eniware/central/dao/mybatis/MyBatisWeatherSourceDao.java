/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis;

import org.eniware.central.dao.WeatherSourceDao;
import org.eniware.central.dao.mybatis.support.BaseMyBatisGenericDao;
import org.eniware.central.domain.WeatherSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link WeatherSourceDao}.
 * 
 * @author matt
 * @version 1.0
 */
public class MyBatisWeatherSourceDao extends BaseMyBatisGenericDao<WeatherSource, Long> implements
		WeatherSourceDao {

	/** The query name used for {@link #getWeatherSourceForName(String)}. */
	public static final String QUERY_FOR_NAME = "get-WeatherSource-for-name";

	/**
	 * Default constructor.
	 */
	public MyBatisWeatherSourceDao() {
		super(WeatherSource.class, Long.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public WeatherSource getWeatherSourceForName(String sourceName) {
		return selectFirst(QUERY_FOR_NAME, sourceName);
	}

}
