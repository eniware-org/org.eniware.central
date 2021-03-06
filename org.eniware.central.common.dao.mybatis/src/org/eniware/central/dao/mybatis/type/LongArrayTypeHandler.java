/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao.mybatis.type;

import org.apache.ibatis.type.TypeHandler;

/**
 * {@link TypeHandler} for Array of BIGINT types.
 * @version 1.0
 */
public class LongArrayTypeHandler extends BaseArrayTypeHandler {

	/**
	 * Default constructor.
	 */
	public LongArrayTypeHandler() {
		super("bigint");
	}

}
