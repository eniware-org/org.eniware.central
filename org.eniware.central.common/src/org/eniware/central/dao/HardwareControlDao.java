/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.EntityMatch;
import org.eniware.central.domain.HardwareControl;
import org.eniware.central.domain.HardwareFilter;

/**
 * DAO API for HardwareControl.
 * 
 * @author matt
 * @version $Revision$
 */
public interface HardwareControlDao extends GenericDao<HardwareControl, Long>,
		FilterableDao<EntityMatch, Long, HardwareFilter> {

}
