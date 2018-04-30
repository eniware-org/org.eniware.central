/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.agg;

import org.osgi.service.event.EventAdmin;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * Job to process "stale" general location datum reporting aggregate data.
 * 
 * <p>
 * This job executes a JDBC procedure, which is expected to return an Integer
 * result representing the number of rows processed by the call. If the
 * procedure returns zero, the job stops immediately.
 * </p>
 *
 * @version 1.0
 */
public class StaleGeneralLocationDatumProcessor extends StaleGeneralNodeDatumProcessor {

	/**
	 * Construct with properties.
	 * 
	 * @param eventAdmin
	 *        the EventAdmin
	 * @param jdbcOps
	 *        the JdbcOperations to use
	 */
	public StaleGeneralLocationDatumProcessor(EventAdmin eventAdmin, JdbcOperations jdbcOps) {
		super(eventAdmin, jdbcOps);
		setJdbcCall("{? = call eniwareagg.process_agg_stale_loc_datum(?, ?)}");
	}

}
