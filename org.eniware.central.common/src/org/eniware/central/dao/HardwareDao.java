/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.dao;

import org.eniware.central.domain.EntityMatch;
import org.eniware.central.domain.Hardware;
import org.eniware.central.domain.HardwareFilter;

/**
 * DAO API for Hardware.
 * @version $Revision$
 */
public interface HardwareDao extends GenericDao<Hardware, Long>,
FilterableDao<EntityMatch, Long, HardwareFilter>{

}
