/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.datum.domain;

import java.io.Serializable;

import org.eniware.central.domain.BaseEntity;

/**
 * Abstract base class for {@link NodeDatum} implementations.
 *
 * @author matt
 * @version $Revision$ $Date$
 */
public abstract class BaseDatum extends BaseEntity implements Datum, Cloneable, Serializable {

	private static final long serialVersionUID = 3386488447751466774L;

}
